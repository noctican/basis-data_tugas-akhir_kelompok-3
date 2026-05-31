# Prerequisite

- Telah menginstall Java / JDK versi terbaru
- Telah menginstall SQL Server
- Menjalankan file SQL `./Database/DDL/Basdat_Tugas 4_Kelompok 3_DDL.sql` untuk pembuatan struktur database
- Menjalankan file SQL `./Database/Operasi/AllOperasi.sql` untuk penambahan view dan stored procedure

# Cara Menjalankan Program

Untuk menjalankan program dapat dilakukan dengan 2 cara:

## Cara 1 

Pada terminal, jalankan perintah berikut:

```bash
java -jar Program.jar
```

Selain itu, untuk dapat terhubung dengan database SQL Server, koneksi harus dibuat dengan aturan sebagai berikut:
- host = localhost
- port = 1433
- user = sa
- password = password
- database = db_eiger


## Cara 2

Buka file Main.java melalui VS Code, kemudian tekan tombol run java.

Untuk dapat terhubung dengan database SQL Server, koneksi dapat diatur pada file .env yang telah tersedia