USE master;
GO

USE db_eiger;
GO

-- Menaikkan harga_base semua produk sebesar 10%
UPDATE produk
SET harga_base = harga_base * 1.10;
GO

-- Mengubah jabatan karyawan dengan ID Pengguna 8 menjadi 'Supervisor'
UPDATE karyawan
SET jabatan = 'Supervisor'
WHERE id_pengguna = 8;
GO;

-- Menambah 500 poin untuk semua pelanggan yang memiliki jenis_member 'Platinum'
UPDATE pelanggan
SET poin = poin + 500
WHERE jenis_member = 'Platinum';
GO