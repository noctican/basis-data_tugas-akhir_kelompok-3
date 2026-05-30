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