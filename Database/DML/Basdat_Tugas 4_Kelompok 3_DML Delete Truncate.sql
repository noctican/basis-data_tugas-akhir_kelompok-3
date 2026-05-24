USE master;
GO

USE db_eiger;
GO

-- Menghapus seluruh data dari tabel detail_keranjang secara permanen
TRUNCATE TABLE detail_keranjang;
GO

-- Menghapus data pengiriman untuk transaksi dengan ID 19
DELETE FROM pengiriman 
WHERE id_transaksi = 19;
GO

-- Menghapus semua data retur yang statusnya 'Warna berbeda'
DELETE FROM detail_retur
WHERE CAST(alasan AS VARCHAR(MAX)) = 'Warna berbeda';
GO