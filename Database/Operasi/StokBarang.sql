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
        RAISERROR('Gagal Checkout: Keranjang belanja kosong.', 16, 1);
        RETURN;
    END

    IF EXISTS (
        SELECT 1 FROM detail_keranjang dk
        JOIN varian_produk vp ON dk.id_produk = vp.id_produk AND dk.sku = vp.sku
        WHERE dk.id_keranjang = @id_keranjang AND (vp.stock - dk.kuantitas) < 0
    )
    BEGIN
        RAISERROR('Gagal Checkout: Stok produk tidak mencukupi untuk direservasi.', 16, 1);
        RETURN;
    END

    DECLARE @total_pembelian DECIMAL(19,4);
    SELECT @total_pembelian = SUM(sub_total) FROM detail_keranjang WHERE id_keranjang = @id_keranjang;

    BEGIN TRY
        BEGIN TRANSACTION;

        INSERT INTO transaksi (tanggal_transaksi, total_pembelian, status_pembayaran, metode_pembayaran)
        VALUES (GETDATE(), @total_pembelian, 'PENDING', @metode_pembayaran);
        
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
        PRINT 'Keranjang dihapus, stok direservasi. ID Transaksi: ' + CAST(@id_transaksi_baru AS VARCHAR(10));
        
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
        RAISERROR('Data transaksi tidak ditemukan.', 16, 1);
        RETURN;
    END

    BEGIN TRY
        BEGIN TRANSACTION;

        IF @status_saat_ini = 'PAID'
        BEGIN
            PRINT 'Transaksi telah Lunas. Pengurangan stok produk telah menjadi permanen.';
        END
        
        ELSE IF @status_saat_ini IN ('FAILED')
        BEGIN
            UPDATE vp
            SET vp.stock = vp.stock + dt.kuantitas
            FROM varian_produk vp
            JOIN detail_transaksi dt ON vp.id_produk = dt.id_produk AND vp.sku = dt.sku
            WHERE dt.id_transaksi = @id_transaksi;

            PRINT 'Transaksi Gagal. Stok produk telah dikembalikan ke database.';
        END
        ELSE
        BEGIN
            PRINT 'Status saat ini adalah PENDING. Menunggu pembayaran lunas atau batal.';
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