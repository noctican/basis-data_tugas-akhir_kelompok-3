USE db_eiger;
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

    -- 1. Validasi Kepemilikan Transaksi dan Statusnya
    SELECT @status_transaksi = t.status_pembayaran
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

    -- 2. Validasi Produk pada Transaksi
    SELECT @qty_beli = kuantitas
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

    -- Cek jika sudah pernah diretur (total kuantitas retur)
    SELECT @qty_sudah_retur = ISNULL(SUM(dr.kuantitas), 0)
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

        -- 3. Insert ke retur
        INSERT INTO retur (id_transaksi, status)
        VALUES (@id_transaksi, 'Pending');
        
        SET @id_retur = SCOPE_IDENTITY();

        -- 4. Insert ke detail_retur
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
