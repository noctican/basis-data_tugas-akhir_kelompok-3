package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    public MainFrame() {
        setTitle("E-Commerce Management System - db_eiger");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Kategori", new KategoriPanel());
        tabbedPane.addTab("Sub Kategori", new SubKategoriPanel());
        tabbedPane.addTab("Produk", new ProdukPanel());
        tabbedPane.addTab("Varian Produk", new VarianProdukPanel());
        tabbedPane.addTab("Pengguna", new PenggunaPanel());
        tabbedPane.addTab("Departemen", new DepartemenPanel());
        tabbedPane.addTab("Karyawan", new KaryawanPanel());
        tabbedPane.addTab("Member", new MemberPanel());
        tabbedPane.addTab("Pelanggan", new PelangganPanel());
        tabbedPane.addTab("Alamat Pelanggan", new AlamatPelangganPanel());
        tabbedPane.addTab("Keranjang", new KeranjangPanel());
        tabbedPane.addTab("Detail Keranjang", new DetailKeranjangPanel());
        tabbedPane.addTab("Transaksi", new TransaksiPanel());
        tabbedPane.addTab("Pelanggan Transaksi", new PelangganTransaksiPanel());
        tabbedPane.addTab("Detail Transaksi", new DetailTransaksiPanel());
        tabbedPane.addTab("Retur", new ReturPanel());
        tabbedPane.addTab("Detail Retur", new DetailReturPanel());
        tabbedPane.addTab("Pengiriman", new PengirimanPanel());

        setVisible(true);
    }

    public void addPanel(String title, JPanel panel) {
        tabbedPane.addTab(title, panel);
    }
}
