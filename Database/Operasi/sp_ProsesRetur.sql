USE db_eiger;
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
