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