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
            UPDATE riwayat_topup SET status = 'FAILED' WHERE id_topup = @id_topup;
            SET @msg_response = 'Top-up request rejected.'; 
        END
        ELSE
        BEGIN
            DECLARE @id_pengguna INT
            DECLARE @nominal DECIMAL(19, 4)
            UPDATE riwayat_topup SET status = 'SUCCESS' WHERE id_topup = @id_topup;

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
END