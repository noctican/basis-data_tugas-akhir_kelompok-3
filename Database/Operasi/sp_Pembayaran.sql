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
        SET @hasil_pesan = 'ERROR: Transaksi tidak ditemukan atau bukan milik pengguna ini.';
        RETURN;
    END

    -- 2. Harus PENDING
    IF @status <> 'PENDING'
    BEGIN
        SET @hasil_pesan = 'ERROR: Pembayaran tidak dapat diproses. Status transaksi saat ini adalah ' + @status + '.';
        RETURN;
    END

    SET @selisih_menit = DATEDIFF(MINUTE, @tgl_transaksi, GETDATE());

    IF @selisih_menit > 10
    BEGIN
        -- Auto-expire: panggil SP gagalkan supaya stok kembali juga
        DECLARE @pesan_expire NVARCHAR(255);
        EXEC sp_GagalkanPembayaran @id_transaksi, @pesan_expire OUTPUT;
        SET @hasil_pesan = 'ERROR: Waktu pembayaran sudah habis (> 10 menit). Transaksi otomatis dibatalkan dan stok dikembalikan.';
        RETURN;
    END

    SELECT @wallet = wallet FROM pelanggan WHERE id_pengguna = @id_pengguna;

    IF @wallet IS NULL
    BEGIN
        SET @hasil_pesan = 'ERROR: Data pelanggan tidak ditemukan.';
        RETURN;
    END

    IF @wallet < @total
    BEGIN
        SET @hasil_pesan = 'ERROR: Saldo wallet tidak mencukupi. Saldo Anda: Rp ' +
                           FORMAT(@wallet, 'N2') + ', Total tagihan: Rp ' + FORMAT(@total, 'N2') + '.';
        RETURN;
    END

    BEGIN TRY
        BEGIN TRANSACTION;

        UPDATE pelanggan
        SET    wallet = wallet - @total
        WHERE  id_pengguna = @id_pengguna;

        UPDATE transaksi
        SET    status_pembayaran = 'PAID'
        WHERE  id_transaksi = @id_transaksi;

        COMMIT TRANSACTION;
        SET @hasil_pesan = 'SUKSES: Pembayaran berhasil! Sisa saldo wallet: Rp ' +
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
        SET @hasil_pesan = 'ERROR: Transaksi tidak ditemukan.';
        RETURN;
    END

    IF @status NOT IN ('PENDING')
    BEGIN
        SET @hasil_pesan = 'ERROR: Hanya transaksi PENDING yang dapat dibatalkan. Status saat ini: ' + @status + '.';
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
        SET    status_pembayaran = 'FAILED'
        WHERE  id_transaksi = @id_transaksi;

        COMMIT TRANSACTION;
        SET @hasil_pesan = 'SUKSES: Transaksi #' + CAST(@id_transaksi AS VARCHAR) +
                           ' telah dibatalkan dan stok produk sudah dikembalikan.';
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
        PRINT 'Tidak ada transaksi PENDING yang expired.';
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
        SET    status_pembayaran = 'FAILED'
        WHERE  id_transaksi IN (SELECT id_transaksi FROM @expired);

        SET @jumlah_expired = @@ROWCOUNT;

        COMMIT TRANSACTION;
        PRINT 'sp_CekExpiredPembayaran: ' + CAST(@jumlah_expired AS VARCHAR) +
              ' transaksi expired diproses.';
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @err NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@err, 16, 1);
    END CATCH
END;
GO
