CREATE PROCEDURE sp_get_top_5_spending_customers
    @tanggal_mulai DATE = NULL  -- Parameter untuk memfilter kurun waktu terakhir
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
        -- Jika parameter diisi, filter transaksi yang >= tanggal tersebut
        (@tanggal_mulai IS NULL OR t.tanggal_transaksi >= @tanggal_mulai)
    GROUP BY pg.nama_depan, pg.nama_belakang
    ORDER BY total_spend DESC;
END;
GO