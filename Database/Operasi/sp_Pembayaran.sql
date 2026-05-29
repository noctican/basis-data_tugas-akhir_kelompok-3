USE db_eiger;
GO

CREATE OR ALTER PROCEDURE sp_BayarTransaksi
    @id_pengguna  INT,
    @id_transaksi INT,
    @hasil_pesan  NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @status          VARCHAR(100);
    DECLARE @total           DECIMAL(19,4);
    DECLARE @wallet          DECIMAL(36,2);
    DECLARE @tgl_transaksi   DATETIME;
    DECLARE @selisih_menit   INT;

    SELECT
        @status        = t.status_pembayaran,
        @total         = t.total_pembelian,
        @tgl_transaksi = t.tanggal_transaksi
    FROM transaksi t
    JOIN pelanggan_transaksi pt ON t.id_transaksi = pt.id_transaksi
    WHERE t.id_transaksi  = @id_transaksi
      AND pt.id_pengguna  = @id_pengguna;

    IF @status IS NULL
    BEGIN
        SET @hasil_pesan = 'ERROR: Transaction does not exist or is not owned by this user.';
        RETURN;
    END

    -- 2. Harus PENDING
    IF @status <> 'PENDING'
    BEGIN
        SET @hasil_pesan = 'ERROR: Payment cannot be processed. Current transaction status is ' + @status + '.';
        RETURN;
    END

    SET @selisih_menit = DATEDIFF(MINUTE, @tgl_transaksi, GETDATE());

    IF @selisih_menit > 10
    BEGIN
        DECLARE @pesan_expire NVARCHAR(255);
        EXEC sp_GagalkanPembayaran @id_transaksi, @pesan_expire OUTPUT;
        SET @hasil_pesan = 'ERROR: Payment time has expired (> 10 minutes). Transaction automatically cancelled and stock returned.';
        RETURN;
    END

    SELECT @wallet = wallet FROM pelanggan WHERE id_pengguna = @id_pengguna;

    IF @wallet IS NULL
    BEGIN
        SET @hasil_pesan = 'ERROR: Customer data not found.';
        RETURN;
    END

    IF @wallet < @total
    BEGIN
        SET @hasil_pesan = 'ERROR: Wallet balance is insufficient. Your balance: Rp ' +
                           FORMAT(@wallet, 'N2') + ', Total bill: Rp ' + FORMAT(@total, 'N2') + '.';
        RETURN;
    END

    BEGIN TRY
        BEGIN TRANSACTION;

        UPDATE pelanggan
        SET    wallet = wallet - @total
        WHERE  id_pengguna = @id_pengguna;

        UPDATE transaksi
        SET    status_pembayaran = 'Paid'
        WHERE  id_transaksi = @id_transaksi;

        COMMIT TRANSACTION;
        SET @hasil_pesan = 'SUCCESS: Payment successful! Remaining wallet balance: Rp ' +
                           FORMAT(@wallet - @total, 'N2') + '.';
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        SET @hasil_pesan = 'ERROR: ' + ERROR_MESSAGE();
    END CATCH
END;
GO

CREATE OR ALTER PROCEDURE sp_GagalkanPembayaran
    @id_transaksi INT,
    @hasil_pesan  NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @status VARCHAR(100);

    SELECT @status = status_pembayaran
    FROM   transaksi
    WHERE  id_transaksi = @id_transaksi;

    IF @status IS NULL
    BEGIN
        SET @hasil_pesan = 'ERROR: Transaction does not exist.';
        RETURN;
    END

    IF @status NOT IN ('PENDING')
    BEGIN
        SET @hasil_pesan = 'ERROR: Only PENDING transactions can be cancelled. Current status: ' + @status + '.';
        RETURN;
    END

    BEGIN TRY
        BEGIN TRANSACTION;

        UPDATE vp
        SET    vp.stock = vp.stock + dt.kuantitas
        FROM   varian_produk vp
        JOIN   detail_transaksi dt
               ON vp.id_produk = dt.id_produk
              AND vp.sku        = dt.sku
        WHERE  dt.id_transaksi = @id_transaksi;

        UPDATE transaksi
        SET    status_pembayaran = 'Failed'
        WHERE  id_transaksi = @id_transaksi;

        COMMIT TRANSACTION;
        SET @hasil_pesan = 'SUCCESS: Transaction #' + CAST(@id_transaksi AS VARCHAR) +
                           ' has been cancelled and product stock has been returned.';
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        SET @hasil_pesan = 'ERROR: ' + ERROR_MESSAGE();
    END CATCH
END;
GO

CREATE OR ALTER PROCEDURE sp_CekExpiredPembayaran
    @jumlah_expired INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SET @jumlah_expired = 0;

    DECLARE @expired TABLE (id_transaksi INT);

    INSERT INTO @expired (id_transaksi)
    SELECT id_transaksi
    FROM   transaksi
    WHERE  status_pembayaran = 'PENDING'
      AND  DATEDIFF(MINUTE, tanggal_transaksi, GETDATE()) > 10;

    IF NOT EXISTS (SELECT 1 FROM @expired)
    BEGIN
        PRINT 'There are no PENDING transactions that have expired.';
        RETURN;
    END

    BEGIN TRY
        BEGIN TRANSACTION;

        UPDATE vp
        SET    vp.stock = vp.stock + dt.kuantitas
        FROM   varian_produk vp
        JOIN   detail_transaksi dt  ON vp.id_produk = dt.id_produk AND vp.sku = dt.sku
        JOIN   @expired             e ON dt.id_transaksi = e.id_transaksi;

        UPDATE transaksi
        SET    status_pembayaran = 'Failed'
        WHERE  id_transaksi IN (SELECT id_transaksi FROM @expired);

        SET @jumlah_expired = @@ROWCOUNT;

        COMMIT TRANSACTION;
        PRINT 'sp_CekExpiredPembayaran: ' + CAST(@jumlah_expired AS VARCHAR) +
              ' transaction(s) expired and processed.';
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @err NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@err, 16, 1);
    END CATCH
END;
GO
