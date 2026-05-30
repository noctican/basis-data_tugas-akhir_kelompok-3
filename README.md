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
- user=sa
- password=P@ssw0rd
- database=db_eiger


## Cara 2

Buka file Main.java melalui VS Code, kemudian tekan tombol run java.

Untuk dapat terhubung dengan database SQL Server, koneksi dapat diatur pada file .env yang telah tersedia