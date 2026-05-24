USE master;
GO

USE db_eiger;
GO

-- ================================================================
-- DELETE ALL DATA
-- ================================================================

ALTER TABLE departemen NOCHECK CONSTRAINT fk_manajer_departemen;
GO

DELETE FROM detail_retur;
DELETE FROM pengiriman;
DELETE FROM retur;
DELETE FROM detail_transaksi;
DELETE FROM pelanggan_transaksi;
DELETE FROM transaksi;
DELETE FROM detail_keranjang;
DELETE FROM keranjang;
DELETE FROM alamat_pelanggan;
DELETE FROM pelanggan;
DELETE FROM karyawan;
DELETE FROM departemen;
DELETE FROM pengguna;
DELETE FROM varian_produk;
DELETE FROM produk;
DELETE FROM sub_kategori;
DELETE FROM kategori;
DELETE FROM member;
GO

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('retur') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('retur', RESEED, 0);

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('transaksi') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('transaksi', RESEED, 0);

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('keranjang') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('keranjang', RESEED, 0);

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('alamat_pelanggan') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('alamat_pelanggan', RESEED, 0);

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('departemen') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('departemen', RESEED, 0);

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('pengguna') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('pengguna', RESEED, 0);

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('produk') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('produk', RESEED, 0);

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('sub_kategori') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('sub_kategori', RESEED, 0);

IF EXISTS (SELECT 1 FROM sys.identity_columns WHERE object_id = OBJECT_ID('kategori') AND last_value IS NOT NULL) 
    DBCC CHECKIDENT ('kategori', RESEED, 0);
GO

ALTER TABLE departemen CHECK CONSTRAINT fk_manajer_departemen;
GO

-- ================================================================
-- INSERT DATA
-- ================================================================

-- 1. KATEGORI
INSERT INTO kategori (nama_kategori) VALUES
('Sepatu'), ('Pakaian'), ('Tas'), ('Topi'), ('Aksesoris');
GO

-- 2. SUB_KATEGORI
INSERT INTO sub_kategori (id_kategori, nama_sub_kategori) VALUES
(1, 'Boots'), (1, 'Sandal'), (1, 'Sneakers'), (1, 'Trail Shoes'),
(2, 'Kaos'), (2, 'Celana Panjang'), (2, 'Jaket'), (2, 'Hoodie'), (2, 'Celana Pendek'),
(3, 'Backpack'), (3, 'Shoulder Bag'), (3, 'Waist Bag'), (3, 'Duffel Bag'),
(4, 'Beanies'), (4, 'Baseball Cap'), (4, 'Bucket Hat'),
(5, 'Jam Tangan'), (5, 'Sarung Tangan'), (5, 'Kacamata'), (5, 'Kaos Kaki');
GO

-- 3. PRODUK
INSERT INTO produk (id_kategori, id_sub_kategori, nama_produk, deskripsi_produk, harga_base, demografi, aktivitas) VALUES
(1, 1, 'EIGER Apex Boots',          'Sepatu boots anti-air untuk medan berbatu',        850000, 'pria',      'Hiking'),
(1, 1, 'EIGER Trail Boots Women',   'Boots ringan untuk wanita petualang',              780000, 'wanita',    'Hiking'),
(1, 2, 'EIGER River Sandal',        'Sandal outdoor tahan air dan anti-selip',          320000, 'anak',      'Outdoor'),
(1, 2, 'EIGER Trek Sandal',         'Sandal trekking dengan sol karet tebal',           280000, 'pria',      'Trekking'),
(1, 3, 'EIGER Urban Sneakers',      'Sneakers kasual untuk aktivitas sehari-hari',      550000, 'pria',      'Casual'),
(1, 3, 'EIGER Flow Sneakers',       'Sneakers ringan untuk wanita aktif',               490000, 'wanita',    'Casual'),
(1, 4, 'EIGER Trail Runner',        'Sepatu lari trail berteknologi grip tinggi',       920000, 'pria',      'Trail Running'),
(1, 4, 'EIGER Speed Trail',         'Sepatu trail lari cepat dengan cushioning',        870000, 'wanita',    'Trail Running'),
(2, 5, 'EIGER Base Tee',            'Kaos dasar berbahan polyester anti-bau',           199000, 'pria',      'Casual'),
(2, 5, 'EIGER Summit Tee Women',    'Kaos wanita breathable untuk pendakian',           189000, 'wanita',    'Hiking'),
(2, 6, 'EIGER Cargo Pants',         'Celana panjang multi-pocket untuk outdoor',        450000, 'pria',      'Outdoor'),
(2, 6, 'EIGER Tactical Pants',      'Celana taktis dengan bahan ripstop',               520000, 'pria',      'Tactical'),
(2, 7, 'EIGER Windbreaker Jacket',  'Jaket anti-angin tipis dan ringan',                680000, 'pria',      'Hiking'),
(2, 7, 'EIGER Rain Shield Jacket',  'Jaket hujan waterproof untuk pendakian',           750000, 'wanita',    'Camping'),
(2, 8, 'EIGER Fleece Hoodie',       'Hoodie fleece hangat untuk camping',               395000, 'anak',      'Camping'),
(2, 9, 'EIGER Quick Dry Shorts',    'Celana pendek quick-dry untuk olahraga',           250000, 'pria',      'Running'),
(3,10, 'EIGER Daypack 20L',         'Tas ransel harian kapasitas 20 liter',             480000, 'anak',      'Field Trip'),
(3,10, 'EIGER Summit Pack 45L',     'Tas gunung kapasitas 45 liter',                   1200000, 'pria',      'Hiking'),
(3,10, 'EIGER Stone Pack 30L',      'Backpack serbaguna kapasitas 30 liter',            750000, 'pria',      'Outdoor'),
(3,11, 'EIGER Crossbody Bag',       'Tas selempang kecil untuk aktivitas harian',       320000, 'wanita',    'Casual'),
(3,12, 'EIGER Hip Pack',            'Waist bag untuk menyimpan barang penting',         220000, 'anak',      'Outdoor'),
(3,13, 'EIGER Duffel 40L',          'Tas duffel besar untuk perjalanan',                580000, 'wanita',    'Traveling'),
(4,14, 'EIGER Wool Beanie',         'Kupluk wol tebal untuk cuaca dingin',              120000, 'anak',      'Camping'),
(4,15, 'EIGER Summit Cap',          'Topi baseball dengan panel ventilasi',             150000, 'pria',      'Hiking'),
(4,16, 'EIGER Boonie Hat',          'Topi bucket lebar untuk perlindungan matahari',    175000, 'anak',      'Outdoor'),
(5,17, 'EIGER Expedition Watch',    'Jam tangan digital dengan altimeter dan kompas',  1500000, 'equipment', 'Hiking'),
(5,17, 'EIGER Sport Watch',         'Jam tangan sport tahan air 50m',                   950000, 'equipment', 'Running'),
(5,18, 'EIGER Liner Gloves',        'Sarung tangan tipis anti-angin',                   185000, 'equipment', 'Camping'),
(5,19, 'EIGER Polarized Sunglass',  'Kacamata polarisasi anti-UV untuk outdoor',        380000, 'equipment', 'Outdoor'),
(5,20, 'EIGER Trekking Socks',      'Kaos kaki anti-lecet untuk trekking',               95000, 'equipment', 'Hiking');
GO

-- 4. VARIAN_PRODUK
INSERT INTO varian_produk (id_produk, sku, stock, warna, harga_varian, ukuran) VALUES
( 1,'SKU001',41,'Hitam',   900000,'41'), ( 1,'SKU002',25,'Hitam',   800000,'40'), ( 1,'SKU003',46,'Hitam',   850000,'38'),
( 1,'SKU004',30,'Abu-abu', 900000,'41'), ( 1,'SKU005',34,'Abu-abu', 800000,'40'), ( 1,'SKU006',21,'Abu-abu', 800000,'38'),
( 2,'SKU007',42,'Putih',   780000,'42'), ( 2,'SKU008',28,'Putih',   730000,'40'), ( 2,'SKU009',13,'Putih',   830000,'44'),
( 2,'SKU010',36,'Biru',    730000,'42'), ( 2,'SKU011', 8,'Biru',    730000,'40'), ( 2,'SKU012',14,'Biru',    830000,'44'),
( 3,'SKU013',43,'Putih',   320000,'42'), ( 3,'SKU014',38,'Putih',   320000,'38'), ( 3,'SKU015',40,'Putih',   270000,'41'),
( 3,'SKU016',48,'Merah',   370000,'42'), ( 3,'SKU017',12,'Merah',   370000,'38'), ( 3,'SKU018',39,'Merah',   320000,'41'),
( 4,'SKU019',34,'Hijau',   230000,'38'), ( 4,'SKU020',21,'Hijau',   330000,'40'), ( 4,'SKU021',16,'Hijau',   330000,'41'),
( 4,'SKU022',11,'Abu-abu', 330000,'38'), ( 4,'SKU023',24,'Abu-abu', 330000,'40'), ( 4,'SKU024',37,'Abu-abu', 330000,'41'),
( 5,'SKU025',43,'Putih',   550000,'40'), ( 5,'SKU026',36,'Putih',   500000,'39'), ( 5,'SKU027',12,'Putih',   550000,'42'),
( 5,'SKU028',24,'Hijau',   500000,'40'), ( 5,'SKU029', 8,'Hijau',   500000,'39'), ( 5,'SKU030',41,'Hijau',   500000,'42'),
( 6,'SKU031',13,'Hitam',   540000,'44'), ( 6,'SKU032',35,'Hitam',   540000,'38'), ( 6,'SKU033',15,'Hitam',   490000,'42'),
( 6,'SKU034',38,'Merah',   540000,'44'), ( 6,'SKU035',32,'Merah',   440000,'38'), ( 6,'SKU036',39,'Merah',   540000,'42'),
( 7,'SKU037',33,'Hijau',   970000,'43'), ( 7,'SKU038',33,'Hijau',   870000,'40'), ( 7,'SKU039',20,'Hijau',   870000,'41'),
( 7,'SKU040', 9,'Putih',   920000,'43'), ( 7,'SKU041', 6,'Putih',   970000,'40'), ( 7,'SKU042',40,'Putih',   870000,'41'),
( 8,'SKU043', 9,'Biru',    820000,'38'), ( 8,'SKU044',26,'Biru',    820000,'44'), ( 8,'SKU045',37,'Biru',    820000,'43'),
( 8,'SKU046',22,'Putih',   920000,'38'), ( 8,'SKU047',36,'Putih',   820000,'44'), ( 8,'SKU048',39,'Putih',   820000,'43'),
( 9,'SKU049',16,'Navy',    199000,'L'),  ( 9,'SKU050',52,'Navy',    199000,'S'),  ( 9,'SKU051',37,'Putih',   199000,'L'),
( 9,'SKU052',32,'Putih',   199000,'S'),  ( 9,'SKU053',37,'Merah',   199000,'L'),  ( 9,'SKU054',36,'Merah',   199000,'S'),
(10,'SKU055',35,'Navy',    189000,'XS'), (10,'SKU056',56,'Navy',    189000,'XXL'),(10,'SKU057',31,'Cokelat',  189000,'XS'),
(10,'SKU058',16,'Cokelat', 189000,'XXL'),(10,'SKU059',25,'Hijau',   189000,'XS'), (10,'SKU060',22,'Hijau',   189000,'XXL'),
(11,'SKU061',27,'Merah',   450000,'S'),  (11,'SKU062',39,'Merah',   450000,'L'),  (11,'SKU063',25,'Biru',    450000,'S'),
(11,'SKU064',14,'Biru',    450000,'L'),  (11,'SKU065',38,'Navy',    450000,'S'),  (11,'SKU066',45,'Navy',    450000,'L'),
(12,'SKU067',58,'Putih',   520000,'XL'), (12,'SKU068',25,'Putih',   520000,'XS'), (12,'SKU069',20,'Hitam',   520000,'XL'),
(12,'SKU070',36,'Hitam',   520000,'XS'), (12,'SKU071',41,'Hijau',   520000,'XL'), (12,'SKU072',40,'Hijau',   520000,'XS'),
(13,'SKU073',10,'Merah',   680000,'XS'), (13,'SKU074',34,'Merah',   680000,'S'),  (13,'SKU075',26,'Cokelat',  680000,'XS'),
(13,'SKU076',60,'Cokelat', 680000,'S'),  (13,'SKU077',60,'Navy',    680000,'XS'), (13,'SKU078',39,'Navy',    680000,'S'),
(14,'SKU079',19,'Biru',    750000,'XXL'),(14,'SKU080',22,'Biru',    750000,'XL'), (14,'SKU081',28,'Merah',   750000,'XXL'),
(14,'SKU082',23,'Merah',   750000,'XL'), (14,'SKU083',13,'Hijau',   750000,'XXL'),(14,'SKU084',47,'Hijau',   750000,'XL'),
(15,'SKU085',42,'Hitam',   395000,'XS'), (15,'SKU086',43,'Hitam',   395000,'XXL'),(15,'SKU087',20,'Hijau',   395000,'XS'),
(15,'SKU088',13,'Hijau',   395000,'XXL'),(15,'SKU089',42,'Abu-abu', 395000,'XS'), (15,'SKU090',15,'Abu-abu', 395000,'XXL'),
(16,'SKU091',17,'Abu-abu', 250000,'XS'), (16,'SKU092',46,'Abu-abu', 250000,'S'),  (16,'SKU093',25,'Hitam',   250000,'XS'),
(16,'SKU094',47,'Hitam',   250000,'S'),  (16,'SKU095',48,'Biru',    250000,'XS'), (16,'SKU096',12,'Biru',    250000,'S'),
(17,'SKU097',26,'Biru',    480000,'One Size'), (17,'SKU098',23,'Hitam',   480000,'One Size'), (17,'SKU099',23,'Merah',   480000,'One Size'),
(18,'SKU100',11,'Biru',   1200000,'One Size'), (18,'SKU101',26,'Abu-abu',1200000,'One Size'), (18,'SKU102',27,'Hijau',  1200000,'One Size'),
(19,'SKU103',17,'Abu-abu', 750000,'One Size'), (19,'SKU104', 9,'Putih',   750000,'One Size'), (19,'SKU105',26,'Cokelat', 750000,'One Size'),
(20,'SKU106',15,'Hijau',   320000,'One Size'), (20,'SKU107',29,'Abu-abu', 320000,'One Size'), (20,'SKU108', 7,'Merah',   320000,'One Size'),
(21,'SKU109',23,'Hitam',   220000,'One Size'), (21,'SKU110', 8,'Merah',   220000,'One Size'), (21,'SKU111', 7,'Biru',    220000,'One Size'),
(22,'SKU112',13,'Biru',    580000,'One Size'), (22,'SKU113', 9,'Putih',   580000,'One Size'), (22,'SKU114',16,'Cokelat', 580000,'One Size'),
(23,'SKU115',51,'Putih',   120000,'One Size'), (23,'SKU116',35,'Navy',    120000,'One Size'), (23,'SKU117',71,'Abu-abu', 120000,'One Size'),
(24,'SKU118',16,'Biru',    150000,'One Size'), (24,'SKU119',53,'Navy',    150000,'One Size'), (24,'SKU120',28,'Hijau',   150000,'One Size'),
(25,'SKU121',28,'Abu-abu', 175000,'One Size'), (25,'SKU122',34,'Navy',    175000,'One Size'), (25,'SKU123',49,'Hitam',   175000,'One Size'),
(26,'SKU124',15,'Abu-abu',1500000, NULL),      (26,'SKU125',11,'Putih',  1500000, NULL),
(27,'SKU126',13,'Abu-abu', 950000, NULL),      (27,'SKU127', 6,'Merah',   950000, NULL),
(28,'SKU128',13,'Hitam',   185000, NULL),      (28,'SKU129', 6,'Merah',   185000, NULL),
(29,'SKU130', 9,'Hitam',   380000, NULL),      (29,'SKU131',25,'Abu-abu', 380000, NULL),
(30,'SKU132',19,'Abu-abu',  95000, NULL),      (30,'SKU133',22,'Putih',    95000, NULL);
GO

-- 5. MEMBER
INSERT INTO member (jenis, poin, voucher_price, voucher_percentage) VALUES
('Blue',     2500,  25000.0000, 0.000),
('Silver',   5000,  50000.0000, 0.050),
('Gold',     7500,  75000.0000, 0.100),
('Platinum',10000, 100000.0000, 0.150);
GO

-- 6. PENGGUNA
INSERT INTO pengguna (email, nama_depan, nama_belakang, nomor_telepon, password) VALUES
('andi@email.com',  'Andi',   'Saputra',      '811111111',   'pass123'),
('budi@email.com',  'Budi',   'Santoso',       '822222222',   'pass153'),
('citra@email.com', 'Citra',  'Lestari',       '833333333',   'pass101'),
('dewi@email.com',  'Dewi',   'Anggraini',     '844444444',   'pass133'),
('eko@email.com',   'Eko',    'Prasetyo',      '855555555',   'pass023'),
('fajar@email.com', 'Fajar',  'Nugroho',       '866666666',   'pass943'),
('gina@email.com',  'Gina',   'Putri',         '877777777',   'pass349'),
('hadi@email.com',  'Hadi',   'Wijaya',        '888888888',   'pass642'),
('aulya@email.com', 'Aulya',  'Shabrina',      '8123456789',  'pass143'),
('dina@email.com',  'Dina',   'Putri',         '8234567890',  'pass432'),
('clara@email.com', 'Nadine', 'Clarissa',      '8498125923',  'pass132'),
('fifi@email.com',  'Fifi',   'Hafifah',       '8563461226',  'pass192'),
('ariq@email.com',  'Ariq',   'Anugrah',       '8123431283',  'pass792'),
('candra@email.com','Candra', 'Andika',        '8752342931',  'pass237'),
('hilmi@email.com', 'Hilmi',  'Isnaini',       '8219449231',  'pass482'),
('noval@email.com', 'Noval',  'Zakky',         '8123124121',  'pass921'),
('reza@email.com',  'Reza',   'Firmansyah',    '81298765430', 'pass132'),
('sinta@email.com', 'Sinta',  'Rahayu',        '85612345670', 'pass130'),
('bagas@email.com', 'Bagas',  'Nugroho',       '87712345670', 'pass195'),
('layla@email.com', 'Layla',  'Fitriani',      '81345678900', 'pass323'),
('dimas@email.com', 'Dimas',  'Kurniawan',     '82456789010', 'pass338'),
('nadia@email.com', 'Nadia',  'Susanti',       '83567890120', 'pass617'),
('yoga@email.com',  'Yoga',   'Pratama',       '81678901230', 'pass716'),
('putri@email.com', 'Putri',  'Wulandari',     '84789012340', 'pass127'),
('adit@email.com',  'Adit',   'Setiawan',      '85890123450', 'pass674'),
('mira@email.com',  'Mira',   'Oktaviani',     '86901234560', 'pass303'),
('rizal@email.com', 'Rizal',  'Hidayat',       '87012345670', 'pass833'),
('intan@email.com', 'Intan',  'Permatasari',   '88123456780', 'pass765'),
('febri@email.com', 'Febri',  'Ramadhan',      '89234567890', 'pass818'),
('hana@email.com',  'Hana',   'Kusumawati',    '81345609870', 'pass658');
GO

-- 7 & 8. DEPARTEMEN & KARYAWAN
ALTER TABLE departemen NOCHECK CONSTRAINT fk_manajer_departemen;
GO

INSERT INTO departemen (id_manajer, nama_departemen, lokasi, deskripsi_tugas) VALUES
(1, 'Logistik',      'Gedung A', 'Mengelola sistem'),
(2, 'RnD',           'Gedung B', 'Menangani pelanggan'),
(4, 'Product',       'Gedung C', 'Mengelola keuangan'),
(5, 'Product Design','Gedung D', 'Promosi produk'),
(3, 'Staf Ritel',    'Gedung F', 'Gudang');
GO

INSERT INTO karyawan (id_pengguna, id_departemen, jabatan) VALUES
(1, 1, 'Manager'), (2, 2, 'Manager'), (3, 1, 'Staff'),  (4, 3, 'Manager'),
(5, 4, 'Manager'), (6, 5, 'Staff'),   (7, 5, 'Staff'),  (8, 2, 'Staff');
GO

ALTER TABLE departemen CHECK CONSTRAINT fk_manajer_departemen;
GO

-- 9. PELANGGAN
INSERT INTO pelanggan (id_pengguna, jenis_member, tanggal_bergabung, poin, is_used_voucher_percentage, is_used_voucher_price) VALUES
( 9, 'Blue',    '2025-02-26', 1859, 0, 0),
(10, 'Silver',  '2025-03-13', 4914, 1, 1),
(11, 'Gold',    '2025-06-28', 5654, 0, 0),
(12, 'Platinum','2025-03-13', 8894, 1, 1),
(13, 'Blue',    '2025-03-28', 1440, 0, 1),
(14, 'Silver',  '2025-04-08', 2880, 1, 1),
(15, 'Gold',    '2025-03-30', 6471, 0, 0),
(16, 'Platinum','2025-04-28', 7678, 1, 1),
(17, 'Blue',    '2025-01-21', 1775, 0, 0),
(18, 'Silver',  '2025-05-28', 3982, 0, 1),
(19, 'Gold',    '2025-01-12', 5285, 1, 1),
(20, 'Platinum','2025-01-21', 8686, 0, 1),
(21, 'Blue',    '2025-04-08', 1206, 0, 0),
(22, 'Silver',  '2025-06-12', 4358, 1, 0),
(23, 'Gold',    '2025-04-05', 5667, 0, 0),
(24, 'Platinum','2025-06-21', 8359, 1, 0),
(25, 'Blue',    '2025-06-24', 2437, 0, 1),
(26, 'Silver',  '2025-06-12', 4996, 1, 1),
(27, 'Gold',    '2025-03-04', 7188, 0, 1),
(28, 'Platinum','2025-04-08', 9394, 1, 0),
(29, 'Blue',    '2025-06-26', 2310, 0, 1),
(30, 'Silver',  '2025-01-15', 3829, 1, 1);
GO

-- 10. ALAMAT_PELANGGAN
INSERT INTO alamat_pelanggan (id_pengguna, provinsi, kota, jalan, nama_penerima, no_telp) VALUES
( 9, 'Jawa Timur',        'Surabaya',   'Jl. Kamboja No. 19',      'Aulya Shabrina',   '143234567890'),
(10, 'Jawa Timur',        'Surabaya',   'Jl. Kamboja No. 19',      'Dina Putri',        '144345678901'),
(11, 'Jawa Barat',        'Bandung',    'Jl. Mawar No. 21',        'Nadine Clarissa',   '146981259230'),
(12, 'DIY Yogyakarta',    'Yogyakarta', 'Jl. Anggrek No. 8',       'Fifi Hafifah',      '147634612260'),
(13, 'Jawa Tengah',       'Semarang',   'Jl. Flamboyan No. 15',    'Ariq Anugrah',      '143234312830'),
(13, 'Jawa Timur',        'Malang',     'Jl. Dahlia No. 3',        'Ariq Anugrah',      '143234312830'),
(14, 'Jawa Timur',        'Malang',     'Jl. Dahlia No. 3',        'Candra Andika',     '149523429310'),
(15, 'Jawa Timur',        'Batu',       'Jl. Lely No. 45',         'Hilmi Isnaini',     '144194492310'),
(16, 'Sumatera Utara',    'Medan',      'Jl. Kenangan No. 10',     'Noval Zakky',       '143231241210'),
(17, 'Sulawesi Selatan',  'Makassar',   'Jl. Papua No. 12',        'Reza Firmansyah',   '143298765430'),
(17, 'DKI Jakarta',       'Jakarta',    'Jl. Sigura No. 8',        'Reza Firmansyah',   '143298765430'),
(18, 'DKI Jakarta',       'Jakarta',    'Jl. Sigura No. 8',        'Sinta Rahayu',      '147612345670'),
(19, 'Jawa Timur',        'Surabaya',   'Jl. Merdeka No. 5',       'Bagas Nugroho',     '149712345670'),
(20, 'Jawa Barat',        'Bandung',    'Jl. Diponegoro No. 17',   'Layla Fitriani',    '143345678900'),
(21, 'DIY Yogyakarta',    'Yogyakarta', 'Jl. Sudirman No. 22',     'Dimas Kurniawan',   '144456789010'),
(21, 'Jawa Tengah',       'Semarang',   'Jl. Gatot Subroto No. 9', 'Dimas Kurniawan',   '144456789010'),
(22, 'Jawa Tengah',       'Semarang',   'Jl. Gatot Subroto No. 9', 'Nadia Susanti',     '145567890120'),
(23, 'Jawa Timur',        'Malang',     'Jl. Ahmad Yani No. 33',   'Yoga Pratama',      '143678901230'),
(24, 'Jawa Timur',        'Batu',       'Jl. Pahlawan No. 7',      'Putri Wulandari',   '146789012340'),
(25, 'Sumatera Utara',    'Medan',      'Jl. Veteran No. 14',      'Adit Setiawan',     '147890123450'),
(25, 'Sulawesi Selatan',  'Makassar',   'Jl. Pemuda No. 28',       'Adit Setiawan',     '147890123450'),
(26, 'Sulawesi Selatan',  'Makassar',   'Jl. Pemuda No. 28',       'Mira Oktaviani',    '148901234560'),
(27, 'DKI Jakarta',       'Jakarta',    'Jl. Imam Bonjol No. 11',  'Rizal Hidayat',     '149012345670'),
(28, 'Jawa Timur',        'Surabaya',   'Jl. Pangeran No. 6',      'Intan Permatasari', '150123456780'),
(29, 'Jawa Barat',        'Bandung',    'Jl. Cendana No. 19',      'Febri Ramadhan',    '151234567890'),
(29, 'DIY Yogyakarta',    'Yogyakarta', 'Jl. Nusantara No. 41',    'Febri Ramadhan',    '151234567890'),
(30, 'DIY Yogyakarta',    'Yogyakarta', 'Jl. Nusantara No. 41',    'Hana Kusumawati',   '143345609870');
GO

-- 11. KERANJANG (1 keranjang per pelanggan)
INSERT INTO keranjang (id_pengguna) VALUES
(9),(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),(20),(21),(22),(23),(24),(25),(26),(27),(28),(29),(30);
GO

-- 12. DETAIL_KERANJANG
INSERT INTO detail_keranjang (id_keranjang, id_produk, sku, kuantitas, sub_total) VALUES
(1,  1,'SKU003',3, 2550000), (1,  5,'SKU029',1,  500000), (1,  4,'SKU020',2,  660000), (1,  7,'SKU039',3, 2610000),
(2, 21,'SKU111',1,  220000), (2,  6,'SKU033',2,  980000),
(3,  2,'SKU011',3, 2190000), (3, 16,'SKU092',1,  250000), (3,  9,'SKU054',3,  597000),
(4, 16,'SKU091',3,  750000),
(5,  7,'SKU040',2, 1840000), (5, 11,'SKU061',1,  450000), (5,  7,'SKU042',1,  870000), (5,  8,'SKU046',3,  870000),
(6, 20,'SKU106',1,  320000), (6, 11,'SKU064',3, 1350000), (6, 12,'SKU069',1,  520000),
(7,  2,'SKU010',2, 1460000), (7, 25,'SKU121',2,  350000), (7, 10,'SKU057',2,  378000), (7,  9,'SKU052',1,  199000),
(8,  2,'SKU007',2, 1560000), (8,  9,'SKU050',2,  398000),
(9,  3,'SKU018',3,  960000), (9, 12,'SKU072',3, 1560000), (9, 15,'SKU090',2,  790000),
(10, 2,'SKU008',1,  730000), (10, 5,'SKU030',3, 1500000), (10,12,'SKU067',2, 1040000),
(11, 5,'SKU028',3, 1500000),
(12,15,'SKU089',1,  395000), (12,14,'SKU081',2, 1500000), (12,22,'SKU112',3, 1740000), (12,29,'SKU131',1,  380000),
(13, 2,'SKU012',3, 2490000), (13,22,'SKU112',3, 1740000), (13, 1,'SKU001',3, 2700000),
(14,16,'SKU094',1,  250000), (14,21,'SKU111',3,  660000),
(15,14,'SKU081',3, 2250000), (15, 6,'SKU032',2, 1080000), (15,13,'SKU077',3, 2040000),
(16,14,'SKU084',1,  750000), (16,19,'SKU104',2, 1500000), (16,13,'SKU076',3, 2040000), (16, 6,'SKU033',2,  980000),
(17,13,'SKU078',3, 2040000), (17,19,'SKU104',1,  750000),
(18,13,'SKU074',3, 2040000), (18, 9,'SKU054',3,  597000), (18,21,'SKU111',3,  660000),
(19,24,'SKU120',3,  450000), (19,22,'SKU114',2, 1160000), (19,10,'SKU055',3,  567000),
(20, 4,'SKU022',3,  990000), (20,13,'SKU073',3, 2040000),
(21, 4,'SKU024',1,  330000), (21,11,'SKU061',1,  450000), (21,14,'SKU080',1,  750000),
(22, 2,'SKU012',1,  830000);
GO

-- 13. TRANSAKSI
INSERT INTO transaksi (tanggal_transaksi, total_pembelian, status_pembayaran, metode_pembayaran) VALUES
('2025-01-16',  730000, 'Lunas',      'Kartu Kredit'),
('2025-01-20', 1720000, 'Lunas',      'Kartu Kredit'),
('2025-01-22', 2989000, 'Belum Lunas','Kartu Kredit'),
('2025-01-26', 1740000, 'Lunas',      'E-Wallet'),
('2025-01-28', 1560000, 'Lunas',      'COD'),
('2025-02-01', 3807000, 'Lunas',      'E-Wallet'),
('2025-02-04', 1460000, 'Lunas',      'Kartu Kredit'),
('2025-02-06', 2045000, 'Lunas',      'E-Wallet'),
('2025-02-10', 5200000, 'Belum Lunas','E-Wallet'),
('2025-02-13', 6110000, 'Belum Lunas','Transfer Bank'),
('2025-02-14', 2270000, 'Belum Lunas','Kartu Kredit'),
('2025-02-18', 4305000, 'Belum Lunas','Kartu Kredit'),
('2025-02-22', 3265000, 'Lunas',      'E-Wallet'),
('2025-02-24', 1890000, 'Lunas',      'Kartu Kredit'),
('2025-02-28', 2645000, 'Lunas',      'Kartu Kredit'),
('2025-03-02', 4450000, 'Lunas',      'Transfer Bank'),
('2025-03-06', 1260000, 'Lunas',      'Transfer Bank'),
('2025-03-07',  398000, 'Lunas',      'COD'),
('2025-03-12',  820000, 'Belum Lunas','E-Wallet'),
('2025-03-13', 6060000, 'Lunas',      'E-Wallet'),
('2025-03-17', 1578000, 'Belum Lunas','Kartu Kredit'),
('2025-03-19', 2950000, 'Belum Lunas','Kartu Kredit'),
('2025-03-23', 4440000, 'Belum Lunas','E-Wallet'),
('2025-03-26', 2945000, 'Belum Lunas','COD'),
('2025-03-29', 1500000, 'Lunas',      'COD'),
('2025-04-02',  320000, 'Belum Lunas','Kartu Kredit'),
('2025-04-03', 1010000, 'Belum Lunas','E-Wallet'),
('2025-04-07', 3218000, 'Belum Lunas','Transfer Bank'),
('2025-04-10',  500000, 'Belum Lunas','COD'),
('2025-04-13',  850000, 'Lunas',      'Kartu Kredit'),
('2025-04-16', 2270000, 'Lunas',      'E-Wallet'),
('2025-04-20', 4617000, 'Lunas',      'COD'),
('2025-04-22', 2408000, 'Lunas',      'Kartu Kredit'),
('2025-04-25', 1490000, 'Lunas',      'E-Wallet'),
('2025-04-27', 2180000, 'Lunas',      'Kartu Kredit'),
('2025-05-01', 4765000, 'Lunas',      'Kartu Kredit'),
('2025-05-04',  660000, 'Lunas',      'Kartu Kredit'),
('2025-05-07', 3780000, 'Lunas',      'Transfer Bank'),
('2025-05-10', 1560000, 'Lunas',      'COD'),
('2025-05-14', 1640000, 'Lunas',      'E-Wallet');
GO

-- 14. PELANGGAN_TRANSAKSI
INSERT INTO pelanggan_transaksi (id_pengguna, id_transaksi) VALUES
( 9, 1),( 10, 2),( 11, 3),( 12, 4),( 13, 5),( 14, 6),( 15, 7),( 16, 8),( 17, 9),( 18,10),
( 19,11),( 20,12),( 21,13),( 22,14),( 23,15),( 24,16),( 25,17),( 26,18),( 27,19),( 28,20),
( 29,21),( 30,22),
( 9,23),( 10,24),( 11,25),( 12,26),( 13,27),( 14,28),( 15,29),( 16,30),( 17,31),( 18,32),
( 19,33),( 20,34),( 21,35),( 22,36),( 23,37),( 24,38),( 25,39),( 26,40);
GO

-- 15. DETAIL_TRANSAKSI
INSERT INTO detail_transaksi (id_produk, sku, id_transaksi, harga_pembelian, kuantitas) VALUES
( 2,'SKU008', 1, 730000,1), ( 1,'SKU006', 2, 800000,1), ( 7,'SKU040', 2, 920000,1),
( 5,'SKU030', 3, 500000,2), (10,'SKU056', 3, 189000,1), (24,'SKU120', 3, 150000,3), (11,'SKU066', 3, 450000,3),
( 7,'SKU042', 4, 870000,2),
( 2,'SKU007', 5, 780000,2),
(18,'SKU102', 6,1200000,1), ( 9,'SKU051', 6, 199000,3), ( 4,'SKU020', 6, 330000,2), (11,'SKU063', 6, 450000,3),
( 2,'SKU011', 7, 730000,2),
(16,'SKU095', 8, 250000,1), ( 3,'SKU018', 8, 320000,2), (29,'SKU130', 8, 380000,2), (15,'SKU088', 8, 395000,1),
(16,'SKU093', 9, 250000,1), (24,'SKU118', 9, 150000,3), ( 7,'SKU040', 9, 920000,3), (22,'SKU112', 9, 580000,3),
(26,'SKU124',10,1500000,3), (24,'SKU120',10, 150000,3), (22,'SKU112',10, 580000,2),
(11,'SKU063',11, 450000,2), ( 4,'SKU023',11, 330000,1), (12,'SKU072',11, 520000,2),
(15,'SKU087',12, 395000,1), ( 2,'SKU008',12, 730000,2), (27,'SKU127',12, 950000,1), (14,'SKU084',12, 750000,2),
(15,'SKU088',13, 395000,3), (12,'SKU072',13, 520000,1), (12,'SKU071',13, 520000,3),
( 4,'SKU022',14, 330000,3), (11,'SKU062',14, 450000,2),
(11,'SKU062',15, 450000,1), (25,'SKU122',15, 175000,1), (27,'SKU126',15, 950000,2), (23,'SKU115',15, 120000,1),
(11,'SKU063',16, 450000,3), (14,'SKU079',16, 750000,3), (16,'SKU095',16, 250000,2), (25,'SKU122',16, 175000,2),
(16,'SKU091',17, 250000,2), (23,'SKU117',17, 120000,2), (12,'SKU070',17, 520000,1),
( 9,'SKU050',18, 199000,2),
( 8,'SKU048',19, 820000,1),
(26,'SKU124',20,1500000,3), (12,'SKU071',20, 520000,3),
( 5,'SKU026',21, 500000,1), ( 9,'SKU050',21, 199000,2), (13,'SKU076',21, 680000,1),
( 1,'SKU004',22, 900000,1), ( 6,'SKU033',22, 490000,1), (12,'SKU071',22, 520000,3),
( 6,'SKU033',23, 490000,1), (27,'SKU126',23, 950000,3), ( 5,'SKU027',23, 550000,2),
(25,'SKU123',24, 175000,1), (22,'SKU113',24, 580000,2), (15,'SKU088',24, 395000,2), ( 8,'SKU048',24, 820000,1),
(19,'SKU103',25, 750000,2),
( 3,'SKU014',26, 320000,1),
(13,'SKU078',27, 680000,1), ( 4,'SKU022',27, 330000,1),
(10,'SKU058',28, 189000,2), (17,'SKU098',28, 480000,3), (23,'SKU116',28, 120000,2), (22,'SKU114',28, 580000,2),
( 5,'SKU026',29, 500000,1),
(12,'SKU068',30, 520000,1), ( 4,'SKU021',30, 330000,1),
( 4,'SKU020',31, 330000,1), ( 7,'SKU041',31, 970000,2),
(25,'SKU121',32, 175000,2), (13,'SKU075',32, 680000,3), ( 2,'SKU009',32, 830000,2), (10,'SKU060',32, 189000,3),
( 4,'SKU019',33, 230000,2), (10,'SKU060',33, 189000,1), (12,'SKU068',33, 520000,3), ( 9,'SKU051',33, 199000,1),
(12,'SKU069',34, 520000,1), ( 7,'SKU037',34, 970000,1),
(14,'SKU079',35, 750000,2), (13,'SKU074',35, 680000,1),
(13,'SKU078',36, 680000,3), (19,'SKU104',36, 750000,2), (12,'SKU070',36, 520000,2), (28,'SKU129',36, 185000,1),
(21,'SKU111',37, 220000,3),
(11,'SKU065',38, 450000,1), ( 2,'SKU007',38, 780000,3), ( 4,'SKU024',38, 330000,3),
(12,'SKU069',39, 520000,3),
( 8,'SKU045',40, 820000,2);
GO

-- 16. PENGIRIMAN
INSERT INTO pengiriman (no_resi, id_transaksi, nama_ekspedisi, jalan, nama_penerima, kota, no_telp, kode_pos, provinsi, status_pengiriman, biaya_pengiriman, tanggal_pengiriman) VALUES
('RESI001', 1,  'Pos Indonesia', 'Jl. Melati No. 12',        'Aulya Shabrina',   'Jakarta',   '143234567890', 10110, 'DKI Jakarta',       'Diproses', 28229,  '2025-01-17'),
('RESI002', 2,  'J&T',           'Jl. Kamboja No. 19',       'Aulya Shabrina',   'Surabaya',  '143234567890', 60111, 'Jawa Timur',         'Diproses', 42225,  '2025-01-21'),
('RESI003', 3,  'JNE',           'Jl. Kamboja No. 19',       'Dina Putri',        'Surabaya',  '144345678901', 60111, 'Jawa Timur',         'Dikirim',  36761,  '2025-01-23'),
('RESI004', 4,  'SiCepat',       'Jl. Mawar No. 21',         'Nadine Clarissa',   'Bandung',   '146981259230', 40111, 'Jawa Barat',         'Selesai',  20539,  '2025-01-27'),
('RESI005', 5,  'SiCepat',       'Jl. Anggrek No. 8',        'Fifi Hafifah',      'Yogyakarta','147634612260', 55111, 'DI Yogyakarta',      'Diproses', 28888,  '2025-01-29'),
('RESI006', 6,  'Anteraja',      'Jl. Flamboyan No. 15',     'Ariq Anugrah',      'Semarang',  '143234312830', 50111, 'Jawa Tengah',        'Selesai',  39807,  '2025-02-02'),
('RESI007', 7,  'JNE',           'Jl. Dahlia No. 3',         'Ariq Anugrah',      'Malang',    '143234312830', 65111, 'Jawa Timur',         'Dikirim',  31186,  '2025-02-05'),
('RESI008', 8,  'JNE',           'Jl. Dahlia No. 3',         'Candra Andika',     'Malang',    '149523429310', 65111, 'Jawa Timur',         'Selesai',  40276,  '2025-02-07'),
('RESI009', 9,  'Anteraja',      'Jl. Lely No. 45',          'Hilmi Isnaini',     'Batu',      '144194492310', 65311, 'Jawa Timur',         'Selesai',  43976,  '2025-02-11'),
('RESI010', 10, 'SiCepat',       'Jl. Kenangan No. 10',      'Noval Zakky',       'Medan',     '143231241210', 20111, 'Sumatera Utara',     'Diproses', 38969,  '2025-02-14'),
('RESI011', 11, 'JNE',           'Jl. Papua No. 12',         'Reza Firmansyah',   'Makassar',  '143298765430', 90111, 'Sulawesi Selatan',   'Dikirim',  18586,  '2025-02-15'),
('RESI012', 12, 'SiCepat',       'Jl. Sigura No. 8',         'Reza Firmansyah',   'Jakarta',   '143298765430', 10110, 'DKI Jakarta',        'Diproses', 17957,  '2025-02-19'),
('RESI013', 13, 'JNE',           'Jl. Sigura No. 8',         'Sinta Rahayu',      'Jakarta',   '147612345670', 10110, 'DKI Jakarta',        'Selesai',  20379,  '2025-02-23'),
('RESI014', 14, 'SiCepat',       'Jl. Merdeka No. 5',        'Bagas Nugroho',     'Surabaya',  '149712345670', 60111, 'Jawa Timur',         'Selesai',  36738,  '2025-02-25'),
('RESI015', 15, 'JNE',           'Jl. Diponegoro No. 17',    'Layla Fitriani',    'Bandung',   '143345678900', 40111, 'Jawa Barat',         'Selesai',  40241,  '2025-03-01'),
('RESI016', 16, 'JNE',           'Jl. Sudirman No. 22',      'Dimas Kurniawan',   'Yogyakarta','144456789010', 55111, 'DI Yogyakarta',      'Selesai',  42661,  '2025-03-03'),
('RESI017', 17, 'SiCepat',       'Jl. Gatot Subroto No. 9',  'Dimas Kurniawan',   'Semarang',  '144456789010', 50111, 'Jawa Tengah',        'Dikirim',  37229,  '2025-03-07'),
('RESI018', 18, 'Anteraja',      'Jl. Gatot Subroto No. 9',  'Nadia Susanti',     'Semarang',  '145567890120', 50111, 'Jawa Tengah',        'Selesai',  39935,  '2025-03-08'),
('RESI019', 19, 'Pos Indonesia',  'Jl. Ahmad Yani No. 33',   'Yoga Pratama',      'Malang',    '143678901230', 65111, 'Jawa Timur',         'Diproses', 22491,  '2025-03-13'),
('RESI020', 20, 'Pos Indonesia',  'Jl. Pahlawan No. 7',      'Putri Wulandari',   'Batu',      '146789012340', 65311, 'Jawa Timur',         'Selesai',  14575,  '2025-03-14'),
('RESI021', 21, 'SiCepat',       'Jl. Veteran No. 14',       'Adit Setiawan',     'Medan',     '147890123450', 20111, 'Sumatera Utara',     'Dikirim',  43275,  '2025-03-18'),
('RESI022', 22, 'SiCepat',       'Jl. Pemuda No. 28',        'Adit Setiawan',     'Makassar',  '147890123450', 90111, 'Sulawesi Selatan',   'Dikirim',  29566,  '2025-03-20'),
('RESI023', 23, 'Pos Indonesia',  'Jl. Melati No. 12',       'Aulya Shabrina',    'Jakarta',   '143234567890', 10110, 'DKI Jakarta',        'Diproses', 19738,  '2025-03-24'),
('RESI024', 24, 'Anteraja',      'Jl. Kamboja No. 19',       'Aulya Shabrina',    'Surabaya',  '143234567890', 60111, 'Jawa Timur',         'Diproses', 32618,  '2025-03-27'),
('RESI025', 25, 'SiCepat',       'Jl. Kamboja No. 19',       'Dina Putri',        'Surabaya',  '144345678901', 60111, 'Jawa Timur',         'Diproses', 39841,  '2025-03-30'),
('RESI026', 26, 'SiCepat',       'Jl. Mawar No. 21',         'Nadine Clarissa',   'Bandung',   '146981259230', 40111, 'Jawa Barat',         'Selesai',  35098,  '2025-04-03'),
('RESI027', 27, 'J&T',           'Jl. Anggrek No. 8',        'Fifi Hafifah',      'Yogyakarta','147634612260', 55111, 'DI Yogyakarta',      'Selesai',  30852,  '2025-04-04'),
('RESI028', 28, 'Anteraja',      'Jl. Flamboyan No. 15',     'Ariq Anugrah',      'Semarang',  '143234312830', 50111, 'Jawa Tengah',        'Diproses', 35302,  '2025-04-08'),
('RESI029', 29, 'J&T',           'Jl. Dahlia No. 3',         'Ariq Anugrah',      'Malang',    '143234312830', 65111, 'Jawa Timur',         'Selesai',  18273,  '2025-04-11'),
('RESI030', 30, 'Pos Indonesia',  'Jl. Dahlia No. 3',        'Candra Andika',     'Malang',    '149523429310', 65111, 'Jawa Timur',         'Dikirim',  16577,  '2025-04-14'),
('RESI031', 31, 'Anteraja',      'Jl. Lely No. 45',          'Hilmi Isnaini',     'Batu',      '144194492310', 65311, 'Jawa Timur',         'Diproses', 11005,  '2025-04-17'),
('RESI032', 32, 'J&T',           'Jl. Kenangan No. 10',      'Noval Zakky',       'Medan',     '143231241210', 20111, 'Sumatera Utara',     'Selesai',  14903,  '2025-04-21'),
('RESI033', 33, 'Anteraja',      'Jl. Papua No. 12',         'Reza Firmansyah',   'Makassar',  '143298765430', 90111, 'Sulawesi Selatan',   'Dikirim',  36049,  '2025-04-23');
GO

-- 17. RETUR
SET IDENTITY_INSERT retur ON;
INSERT INTO retur (id_retur, id_transaksi, tanggal_pengembalian, status, id_validator) VALUES
( 1, 16, '2025-01-25', 'ditolak',   7),
( 2,  6, '2025-01-30', 'ditolak',   7),
( 3, 10, '2025-02-04', 'selesai',   4),
( 4, 17, '2025-02-09', 'selesai',   1),
( 5,  5, '2025-02-14', 'disetujui', 8),
( 6, 17, '2025-02-19', 'disetujui', 8),
( 7,  7, '2025-02-24', 'selesai',   4),
( 8, 19, '2025-03-01', 'selesai',   1),
( 9, 39, '2025-03-06', 'disetujui', 8),
(10, 15, '2025-03-11', 'ditolak',   4);
SET IDENTITY_INSERT retur OFF;
GO

-- 18. DETAIL_RETUR
INSERT INTO detail_retur (id_transaksi, id_retur, id_produk, sku, kuantitas, alasan) VALUES
(16,  1, 11, 'SKU063', 1, 'Barang rusak'),
( 6,  2,  9, 'SKU051', 3, 'Salah kirim'),
(10,  3, 22, 'SKU112', 2, 'Tidak sesuai deskripsi'),
(17,  4, 16, 'SKU091', 1, 'Warna berbeda'),
( 5,  5,  2, 'SKU007', 2, 'Warna berbeda'),
(17,  6, 12, 'SKU070', 1, 'Salah kirim'),
( 7,  7,  2, 'SKU011', 2, 'Kualitas buruk'),
(19,  8,  8, 'SKU048', 1, 'Ukuran tidak sesuai'),
(39,  9, 12, 'SKU069', 2, 'Kualitas buruk'),
(15, 10, 11, 'SKU062', 1, 'Warna berbeda');
GO