-- DDL
USE master;
GO

DROP DATABASE IF EXISTS db_eiger;
GO

CREATE DATABASE db_eiger;
GO

USE db_eiger;
GO

-- CREATE TABLE KATEGORI & PRODUK
CREATE TABLE kategori (
	id_kategori INT IDENTITY(1, 1) PRIMARY KEY,
	nama_kategori VARCHAR(255) NOT NULL UNIQUE,
);
GO

CREATE TABLE sub_kategori (
	id_sub_kategori INT IDENTITY(1, 1),
	id_kategori INT NOT NULL,
	nama_sub_kategori VARCHAR(255) NOT NULL,
	PRIMARY KEY(id_kategori, id_sub_kategori),
	CONSTRAINT fk_kategori_sub FOREIGN KEY (id_kategori) REFERENCES kategori(id_kategori),
);
GO

CREATE TABLE produk (
	id_produk INT IDENTITY(1, 1) PRIMARY KEY,
	id_kategori INT NOT NULL,
	id_sub_kategori INT NOT NULL,
	nama_produk VARCHAR(255) NOT NULL,
	deskripsi_produk TEXT,
	harga_base DECIMAL(19, 4) DEFAULT(0),
	demografi VARCHAR(255) CHECK(demografi IN ('pria', 'wanita', 'anak', 'equipment')),
	aktivitas VARCHAR(255),
	CONSTRAINT fk_sub_kategori_produk FOREIGN KEY (id_kategori, id_sub_kategori) REFERENCES sub_kategori(id_kategori, id_sub_kategori),
);
GO

CREATE TABLE varian_produk (
	id_produk INT NOT NULL,
	sku VARCHAR(10) NOT NULL,
	stock INT DEFAULT 0,
	warna VARCHAR(255),
	harga_varian DECIMAL(19, 4) DEFAULT(0),
	ukuran VARCHAR(255),
	PRIMARY KEY(id_produk, sku),
	CONSTRAINT fk_produk_varian_produk FOREIGN KEY (id_produk) REFERENCES produk(id_produk),
);
GO

-- CREATE TABLE PENGGUNA, KARYAWAN, PELANGGAN
CREATE TABLE pengguna (
	id_pengguna INT IDENTITY(1, 1) PRIMARY KEY,
	email VARCHAR(255) UNIQUE NOT NULL,
	nama_depan VARCHAR(255) NOT NULL,
	nama_belakang VARCHAR(255),
	nomor_telepon VARCHAR(20) UNIQUE,
	password VARCHAR(255) NOT NULL
);
GO

CREATE TABLE departemen (
	id_departemen INT IDENTITY(1, 1) PRIMARY KEY,
	id_manajer INT,
	nama_departemen VARCHAR(100) NOT NULL UNIQUE,
	lokasi VARCHAR(255),
	deskripsi_tugas TEXT,
);
GO

CREATE TABLE karyawan (
	id_pengguna INT PRIMARY KEY,
	id_departemen INT NOT NULL,
	jabatan VARCHAR(255),
	CONSTRAINT fk_pengguna_karyawan FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna),
	CONSTRAINT fk_departemen_karyawan FOREIGN KEY (id_departemen) REFERENCES departemen(id_departemen),
);
GO

ALTER TABLE departemen ADD CONSTRAINT fk_manajer_departemen FOREIGN KEY (id_manajer) REFERENCES karyawan(id_pengguna);
GO

CREATE TABLE member (
	jenis VARCHAR(50) PRIMARY KEY,
	poin INT NOT NULL,
	voucher_price DECIMAL(19, 4),
	voucher_percentage DECIMAL(3, 3),
);
GO

CREATE TABLE pelanggan (
	id_pengguna INT PRIMARY KEY,
	jenis_member VARCHAR(50) DEFAULT('BLUE'),
	tanggal_bergabung DATE DEFAULT(GETDATE()),
	poin INT DEFAULT(0),
	is_used_voucher_percentage BIT DEFAULT(0),
	is_used_voucher_price BIT DEFAULT(0),
	CONSTRAINT fk_pengguna_pelanggan FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna),
	CONSTRAINT fk_member_pelanggan FOREIGN KEY (jenis_member) REFERENCES member(jenis),
);
GO

CREATE TABLE alamat_pelanggan (
	id_pengguna INT NOT NULL,
	id_alamat INT IDENTITY(1, 1) NOT NULL,
	provinsi VARCHAR(255),
	kota VARCHAR(255),
	jalan VARCHAR(255),
	nama_penerima VARCHAR(255),
	no_telp VARCHAR(20),
	PRIMARY KEY (id_pengguna, id_alamat),
	CONSTRAINT fk_pelanggan_alamat FOREIGN KEY (id_pengguna) REFERENCES pelanggan(id_pengguna),
);
GO

-- CREATE TABLE KERANJANG
CREATE TABLE keranjang (
	id_keranjang INT IDENTITY(1, 1) PRIMARY KEY,
	id_pengguna INT UNIQUE,
	CONSTRAINT fk_pelanggan_keranjang FOREIGN KEY (id_pengguna) REFERENCES pelanggan(id_pengguna),
);
GO

CREATE TABLE detail_keranjang (
	id_keranjang INT,
	id_produk INT,
	sku VARCHAR(10),
	kuantitas INT DEFAULT(1),
	sub_total DECIMAL(19, 4),
	PRIMARY KEY(id_keranjang, id_produk, sku),
	CONSTRAINT fk_keranjang_detail FOREIGN KEY (id_keranjang) REFERENCES keranjang(id_keranjang),
	CONSTRAINT fk_keranjang_varian_produk FOREIGN KEY (id_produk, sku) REFERENCES varian_produk(id_produk, sku),
);
GO

-- CREATE TABLE TRANSAKSI
CREATE TABLE transaksi (
	id_transaksi INT IDENTITY(1, 1) PRIMARY KEY,
	tanggal_transaksi DATETIME DEFAULT(GETDATE()),
	total_pembelian DECIMAL(19, 4) DEFAULT(0),
	status_pembayaran VARCHAR(100) DEFAULT('PENDING'),
	metode_pembayaran VARCHAR(255),
);
GO

CREATE TABLE pelanggan_transaksi (
	id_pengguna INT NOT NULL,
	id_transaksi INT NOT NULL,
	PRIMARY KEY (id_pengguna, id_transaksi),
	CONSTRAINT fk_pelanggan_pelanggan_transaksi FOREIGN KEY (id_pengguna) REFERENCES pelanggan(id_pengguna),
	CONSTRAINT fk_transaksi_pelanggan_transaksi FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi),
);
GO

CREATE TABLE detail_transaksi (
	id_produk INT NOT NULL,
	sku VARCHAR(10) NOT NULL,
	id_transaksi INT NOT NULL,
	harga_pembelian DECIMAL(19, 4) DEFAULT(0),
	kuantitas INT DEFAULT(1),
	PRIMARY KEY(id_transaksi, id_produk, sku),
	CONSTRAINT fk_transaksi_detail_transaksi FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi),
	CONSTRAINT fk_varian_detail_transaksi FOREIGN KEY (id_produk, sku) REFERENCES varian_produk(id_produk, sku),
);
GO

-- CREATE TABLE RETUR
CREATE TABLE retur (
	id_retur INT IDENTITY(1, 1),
	id_transaksi INT,
	tanggal_pengembalian DATE DEFAULT(GETDATE()),
	status VARCHAR(100) DEFAULT('PENDING'),
	id_validator INT,
	PRIMARY KEY (id_retur),
	CONSTRAINT fk_transaksi_retur FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi),
	CONSTRAINT fk_karyawan_retur FOREIGN KEY (id_validator) REFERENCES karyawan(id_pengguna),
);
GO

CREATE TABLE detail_retur (
	id_transaksi INT,
	id_retur INT,
	id_produk INT,
	sku VARCHAR(10),
	kuantitas INT,
	alasan TEXT,
	PRIMARY KEY(id_transaksi, id_retur, id_produk, sku),
	CONSTRAINT fk_retur_detail_retur FOREIGN KEY (id_retur) REFERENCES retur(id_retur),
	CONSTRAINT fk_detail_transaksi_detail_retur FOREIGN KEY (id_transaksi, id_produk, sku) REFERENCES detail_transaksi(id_transaksi, id_produk, sku),
);
GO

CREATE TABLE pengiriman (
	no_resi VARCHAR(255),
	id_transaksi INT,
	nama_ekspedisi VARCHAR(255),
	jalan VARCHAR(255),
	nama_penerima VARCHAR(255),
	kota VARCHAR(255),
	no_telp VARCHAR(20),
	kode_pos INT,
	provinsi VARCHAR(255),
	status_pengiriman VARCHAR(255) DEFAULT('PROSES'),
	biaya_pengiriman DECIMAL(19, 4) DEFAULT(0),
	tanggal_pengiriman DATETIME DEFAULT(GETDATE()),
	PRIMARY KEY(id_transaksi, no_resi),
	CONSTRAINT fk_transaksi_pengiriman FOREIGN KEY(id_transaksi) REFERENCES transaksi(id_transaksi),
);