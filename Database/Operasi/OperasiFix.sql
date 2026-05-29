USE db_eiger;
GO

CREATE VIEW v_Top5BarangPelanggan AS
WITH UrutanPembelian AS (
    SELECT 
        pt.id_pengguna AS ID_Pengguna,
        p.nama_produk AS Nama_Produk,
        k.nama_kategori AS Kategori,
        SUM(dt.kuantitas) AS Total_Jumlah,
        MAX(dt.harga_pembelian) AS Harga_Satuan,
        ROW_NUMBER() OVER (
            PARTITION BY pt.id_pengguna 
            ORDER BY SUM(dt.kuantitas) DESC
        ) AS Ranking
    FROM 
        pelanggan_transaksi pt
    JOIN 
        detail_transaksi dt ON pt.id_transaksi = dt.id_transaksi
    JOIN 
        produk p ON dt.id_produk = p.id_produk
    JOIN 
        kategori k ON p.id_kategori = k.id_kategori
    GROUP BY 
        pt.id_pengguna,
        p.id_produk,
        p.nama_produk,
        k.nama_kategori
)
SELECT 
    ID_Pengguna,
    Nama_Produk,
    Kategori,
    Total_Jumlah,
    Harga_Satuan
FROM 
    UrutanPembelian
WHERE 
    Ranking <= 5;
GO

-- SP 1: Ambil Top 3 Produk yang Sering Dibeli Bersama dengan Produk Target

CREATE PROCEDURE sp_Ambil_Top3_Produk_Dibeli_Bersama
    @id_produk_target INT
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1 FROM PRODUK WHERE id_produk = @id_produk_target)
    BEGIN
        RAISERROR('Gagal Analisis: ID Produk target tidak ditemukan di database.', 16, 1);
        RETURN;
    END

    BEGIN TRY
        SELECT TOP 3
            p.nama_produk, 
            COUNT(*) AS jumlah_dibeli_bersama
        FROM DETAIL_TRANSAKSI dt1
        JOIN DETAIL_TRANSAKSI dt2 ON dt1.id_transaksi = dt2.id_transaksi
        JOIN PRODUK p ON dt2.id_produk = p.id_produk
        WHERE dt1.id_produk = @id_produk_target 
          AND dt2.id_produk <> @id_produk_target
        GROUP BY p.id_produk, p.nama_produk
        ORDER BY jumlah_dibeli_bersama DESC;
    END TRY
    BEGIN CATCH
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END;
GO

-- SP 2: Ambil Top 3 Produk yang Paling Sering Dibeli Bersamaan (Tanpa Produk Target)

CREATE PROCEDURE sp_Ambil_Top3_Produk_Paling_Sering_Dibeli_Bersamaan
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        SELECT TOP 3
            p.nama_produk,
            COUNT(dt.id_produk) AS jumlah_muncul
        FROM DETAIL_TRANSAKSI dt
        JOIN PRODUK p ON dt.id_produk = p.id_produk
        WHERE dt.id_transaksi IN (
            SELECT id_transaksi 
            FROM DETAIL_TRANSAKSI
            GROUP BY id_transaksi
            HAVING COUNT(id_produk) > 1
        )
        GROUP BY p.id_produk, p.nama_produk
        ORDER BY jumlah_muncul DESC;
    END TRY
    BEGIN CATCH
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END;
GO

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
        -- Auto-expire: panggil SP gagalkan supaya stok kembali juga
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
    WHERE  status_pembayaran = 'Pending'
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

use db_eiger;
GO

CREATE OR ALTER PROCEDURE sp_ResponseTopup 
    @id_topup INT,
    @is_approve BIT,
    @msg_response NVARCHAR(255) OUTPUT
AS
BEGIN
    BEGIN TRANSACTION
    BEGIN TRY
        IF @is_approve=0
        BEGIN
            UPDATE riwayat_topup SET status = 'Failed' WHERE id_topup = @id_topup;
            SET @msg_response = 'Top-up request rejected.'; 
        END
        ELSE
        BEGIN
            DECLARE @id_pengguna INT
            DECLARE @nominal DECIMAL(19, 4)
            UPDATE riwayat_topup SET status = 'Success' WHERE id_topup = @id_topup;

            SELECT @id_pengguna = id_pengguna, @nominal = nominal FROM riwayat_topup WHERE id_topup = @id_topup;
            UPDATE pelanggan SET wallet = wallet + @nominal WHERE id_pengguna = @id_pengguna;
            SET @msg_response = 'SUCCESS: Top-up request approved.';
        END
        COMMIT TRANSACTION
    END TRY
    BEGIN CATCH
        SET @msg_response = 'ERROR: Error processing top-up request.';
        ROLLBACK TRANSACTION
    END CATCH
END;
GO

CREATE PROCEDURE sp_Checkout_Reservasi_HapusKeranjang
    @id_pengguna INT,
    @metode_pembayaran VARCHAR(255),
    @id_transaksi_baru INT OUTPUT 
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @id_keranjang INT;
    SELECT @id_keranjang = id_keranjang FROM keranjang WHERE id_pengguna = @id_pengguna;
    
    IF @id_keranjang IS NULL OR NOT EXISTS (SELECT 1 FROM detail_keranjang WHERE id_keranjang = @id_keranjang)
    BEGIN
        RAISERROR('Failed Checkout: Keranjang belanja kosong.', 16, 1);
        RETURN;
    END

    IF EXISTS (
        SELECT 1 FROM detail_keranjang dk
        JOIN varian_produk vp ON dk.id_produk = vp.id_produk AND dk.sku = vp.sku
        WHERE dk.id_keranjang = @id_keranjang AND (vp.stock - dk.kuantitas) < 0
    )
    BEGIN
        RAISERROR('Failed Checkout: Stock product does not suffice for reservation.', 16, 1);
        RETURN;
    END

    DECLARE @total_pembelian DECIMAL(19,4);
    SELECT @total_pembelian = SUM(sub_total) FROM detail_keranjang WHERE id_keranjang = @id_keranjang;

    BEGIN TRY
        BEGIN TRANSACTION;

        INSERT INTO transaksi (tanggal_transaksi, total_pembelian, status_pembayaran, metode_pembayaran)
        VALUES (GETDATE(), @total_pembelian, 'Pending', @metode_pembayaran);
        
        SET @id_transaksi_baru = SCOPE_IDENTITY();

        INSERT INTO pelanggan_transaksi (id_pengguna, id_transaksi)
        VALUES (@id_pengguna, @id_transaksi_baru);

        INSERT INTO detail_transaksi (id_produk, sku, id_transaksi, harga_pembelian, kuantitas)
        SELECT dk.id_produk, dk.sku, @id_transaksi_baru, vp.harga_varian, dk.kuantitas
        FROM detail_keranjang dk
        JOIN varian_produk vp ON dk.id_produk = vp.id_produk AND dk.sku = vp.sku
        WHERE dk.id_keranjang = @id_keranjang;

        UPDATE vp
        SET vp.stock = vp.stock - dk.kuantitas
        FROM varian_produk vp
        JOIN detail_keranjang dk ON vp.id_produk = dk.id_produk AND vp.sku = dk.sku
        WHERE dk.id_keranjang = @id_keranjang;

        DELETE FROM detail_keranjang WHERE id_keranjang = @id_keranjang;

        COMMIT TRANSACTION;
        PRINT 'Cart deleted, stock reserved. ID Transaction: ' + CAST(@id_transaksi_baru AS VARCHAR(10));
        
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END;
GO

CREATE PROCEDURE sp_Validasi_Status_Stok
    @id_transaksi INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @status_saat_ini VARCHAR(100);

    SELECT @status_saat_ini = status_pembayaran 
    FROM transaksi 
    WHERE id_transaksi = @id_transaksi;

    IF @status_saat_ini IS NULL
    BEGIN
        RAISERROR('Transaction not found.', 16, 1);
        RETURN;
    END

    BEGIN TRY
        BEGIN TRANSACTION;

        IF @status_saat_ini = 'Paid'
        BEGIN
            PRINT 'Transaction is PAID. Stock reduction is permanent.';
        END

        ELSE IF @status_saat_ini IN ('Failed')
        BEGIN
            UPDATE vp
            SET vp.stock = vp.stock + dt.kuantitas
            FROM varian_produk vp
            JOIN detail_transaksi dt ON vp.id_produk = dt.id_produk AND vp.sku = dt.sku
            WHERE dt.id_transaksi = @id_transaksi;

            PRINT 'Transaction Failed. Product stock has been returned to the database.';
        END
        ELSE
        BEGIN
            PRINT 'Current status is PENDING. Awaiting payment confirmation or cancellation.';
        END

        COMMIT TRANSACTION;

    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END;
GO

CREATE PROCEDURE sp_get_top_5_spending_customers
    @tanggal_mulai DATE = NULL  
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 5 
        pg.nama_depan + ' ' + ISNULL(pg.nama_belakang, '') AS full_name, 
        SUM(t.total_pembelian) AS total_spend
    FROM pengguna pg
    JOIN pelanggan_transaksi pt ON pg.id_pengguna = pt.id_pengguna
    JOIN transaksi t ON pt.id_transaksi = t.id_transaksi
    WHERE 
        (@tanggal_mulai IS NULL OR t.tanggal_transaksi >= @tanggal_mulai)
    GROUP BY pg.nama_depan, pg.nama_belakang
    ORDER BY total_spend DESC;
END;
GO

CREATE OR ALTER PROCEDURE sp_ProsesRetur
    @id_retur INT,
    @is_approve BIT,
    @id_validator INT = NULL,
    @msg_response NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @status VARCHAR(100);
    DECLARE @id_transaksi INT;
    DECLARE @id_pengguna INT;
    DECLARE @refund_total DECIMAL(36,2) = 0;

    SELECT @status = status, @id_transaksi = id_transaksi
    FROM retur
    WHERE id_retur = @id_retur;

    IF @status IS NULL
    BEGIN
        SET @msg_response = 'ERROR: Return request does not exist.';
        RETURN;
    END

    IF @status <> 'Pending'
    BEGIN
        SET @msg_response = 'ERROR: Return request has already been processed (Current status: ' + @status + ').';
        RETURN;
    END

    BEGIN TRY
        BEGIN TRANSACTION;

        IF @is_approve = 0
        BEGIN
            UPDATE retur
            SET status = 'Failed',
                id_validator = ISNULL(@id_validator, id_validator)
            WHERE id_retur = @id_retur;

            SET @msg_response = 'SUCCESS: Return request has been rejected.';
        END
        ELSE
        BEGIN
            UPDATE retur
            SET status = 'Success',
                id_validator = ISNULL(@id_validator, id_validator)
            WHERE id_retur = @id_retur;

            SELECT @id_pengguna = id_pengguna
            FROM pelanggan_transaksi
            WHERE id_transaksi = @id_transaksi;

            SELECT @refund_total = ISNULL(SUM(dr.kuantitas * dt.harga_pembelian), 0)
            FROM detail_retur dr
            JOIN detail_transaksi dt ON dr.id_transaksi = dt.id_transaksi 
                                    AND dr.id_produk = dt.id_produk 
                                    AND dr.sku = dt.sku
            WHERE dr.id_retur = @id_retur;

            IF @refund_total > 0 AND @id_pengguna IS NOT NULL
            BEGIN
                UPDATE pelanggan
                SET wallet = wallet + @refund_total
                WHERE id_pengguna = @id_pengguna;
            END

            UPDATE vp
            SET vp.stock = vp.stock + dr.kuantitas
            FROM varian_produk vp
            JOIN detail_retur dr ON vp.id_produk = dr.id_produk AND vp.sku = dr.sku
            WHERE dr.id_retur = @id_retur;

            SET @msg_response = 'SUCCESS: Return request approved. Stock returned and wallet refunded.';
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        SET @msg_response = 'ERROR: ' + ERROR_MESSAGE();
    END CATCH
END;
GO

CREATE OR ALTER PROCEDURE sp_AjukanRetur
    @id_pengguna INT,
    @id_transaksi INT,
    @id_produk INT,
    @sku VARCHAR(10),
    @kuantitas INT,
    @alasan TEXT,
    @msg_response NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @id_retur INT;
    DECLARE @status_transaksi VARCHAR(100);
    DECLARE @qty_beli INT;
    DECLARE @qty_sudah_retur INT;

=    SELECT @status_transaksi = t.status_pembayaran
    FROM transaksi t
    JOIN pelanggan_transaksi pt ON t.id_transaksi = pt.id_transaksi
    WHERE t.id_transaksi = @id_transaksi AND pt.id_pengguna = @id_pengguna;

    IF @status_transaksi IS NULL
    BEGIN
        SET @msg_response = 'ERROR: Transaction not found or not owned by user.';
        RETURN;
    END

    IF @status_transaksi <> 'PAID'
    BEGIN
        SET @msg_response = 'ERROR: Only PAID transactions can be returned.';
        RETURN;
    END

=    SELECT @qty_beli = kuantitas
    FROM detail_transaksi
    WHERE id_transaksi = @id_transaksi AND id_produk = @id_produk AND sku = @sku;

    IF @qty_beli IS NULL
    BEGIN
        SET @msg_response = 'ERROR: Product not found in this transaction.';
        RETURN;
    END

    IF @kuantitas <= 0 OR @kuantitas > @qty_beli
    BEGIN
        SET @msg_response = 'ERROR: Invalid quantity. Must be between 1 and ' + CAST(@qty_beli AS VARCHAR) + '.';
        RETURN;
    END

=    SELECT @qty_sudah_retur = ISNULL(SUM(dr.kuantitas), 0)
    FROM detail_retur dr
    JOIN retur r ON dr.id_retur = r.id_retur
    WHERE dr.id_transaksi = @id_transaksi AND dr.id_produk = @id_produk AND dr.sku = @sku AND r.status <> 'Failed';

    IF (@qty_sudah_retur + @kuantitas) > @qty_beli
    BEGIN
        SET @msg_response = 'ERROR: Quantity exceeds purchased amount minus previous returns.';
        RETURN;
    END

    BEGIN TRY
        BEGIN TRANSACTION;

=        INSERT INTO retur (id_transaksi, status)
        VALUES (@id_transaksi, 'Pending');
        
        SET @id_retur = SCOPE_IDENTITY();

        INSERT INTO detail_retur (id_transaksi, id_retur, id_produk, sku, kuantitas, alasan)
        VALUES (@id_transaksi, @id_retur, @id_produk, @sku, @kuantitas, @alasan);

        COMMIT TRANSACTION;
        SET @msg_response = 'SUCCESS: Return request has been submitted successfully.';
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        SET @msg_response = 'ERROR: ' + ERROR_MESSAGE();
    END CATCH
END;
GO

