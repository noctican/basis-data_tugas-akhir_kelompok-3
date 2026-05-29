package ui;

import helpers.GUIHelper;
import helpers.NumberHelper; // <-- IMPORT JURUSNYA DI SINI
import models.PaymentModel;
import models.PaymentModel.HasilPembayaran;
import session.UserSession;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PaymentPanel extends JPanel {

    private static final int BATAS_MENIT    = 10;
    private static final int BATAS_DETIK    = BATAS_MENIT * 60;
    private static final int REFRESH_MS     = 5_000;

    private static final Color WARNA_PENDING = new Color(255, 165, 0);
    private static final Color WARNA_PAID    = new Color(34, 139, 34);
    private static final Color WARNA_FAILED  = new Color(178, 34, 34);
    private static final Color WARNA_BG      = new Color(245, 245, 250);

    private final PaymentModel paymentModel = new PaymentModel();
    private final NumberFormat rupiahFmt =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final SimpleDateFormat dateFmt =
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private JTable tabel;
    private DefaultTableModel tabelModel;

    private JLabel lblWallet;
    private JLabel lblStatusDetail;

    private JButton btnBayar;
    private JButton btnBatal;
    private JButton btnRefresh;

    private Timer autoRefreshTimer;

    private static final String[] KOLOM = {
        "ID Transaksi", "Tanggal", "Total (Rp)", "Metode", "Status", "Sisa Waktu"
    };

    public PaymentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(WARNA_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buatHeader(), BorderLayout.NORTH);
        add(buatTengah(), BorderLayout.CENTER);
        add(buatBawah(), BorderLayout.SOUTH);

        mulaiAutoRefresh();
        refresh();
    }

    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WARNA_BG);

        JLabel judul = new JLabel("Pembayaran Wallet");
        judul.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(judul, BorderLayout.WEST);

        JPanel kananPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        kananPanel.setBackground(WARNA_BG);

        lblWallet = new JLabel("Saldo Wallet: memuat...");
        lblWallet.setFont(new Font("Arial", Font.BOLD, 14));
        lblWallet.setForeground(new Color(0, 100, 180));

        btnRefresh = new JButton("↺ Refresh");
        btnRefresh.addActionListener(e -> refresh());

        kananPanel.add(lblWallet);
        kananPanel.add(btnRefresh);
        header.add(kananPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buatTengah() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(WARNA_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Daftar Transaksi Saya",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13)));

        tabelModel = new DefaultTableModel(KOLOM, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(tabelModel);
        tabel.setRowHeight(28);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getTableHeader().setReorderingAllowed(false);

        NumberHelper.setRupiah(tabel, 2);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateDetailBar();
        });

        tabel.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                String status = value == null ? "" : value.toString();
                if (!isSelected) {
                    switch (status) {
                        case "Pending": setBackground(new Color(255, 237, 180)); setForeground(Color.DARK_GRAY); break;
                        case "Paid":    setBackground(new Color(200, 240, 200)); setForeground(new Color(0, 100, 0)); break;
                        case "Failed":  setBackground(new Color(255, 200, 200)); setForeground(new Color(150, 0, 0)); break;
                        default:        setBackground(Color.WHITE); setForeground(Color.BLACK);
                    }
                }
                return this;
            }
        });

        tabel.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                String txt = value == null ? "" : value.toString();
                if (!isSelected) {
                    if (txt.equals("LUNAS") || txt.equals("GAGAL")) {
                        setForeground(Color.GRAY);
                    } else if (txt.startsWith("0:")) {
                        setForeground(Color.RED); setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(new Color(0, 120, 0)); setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                return this;
            }
        });

        int[] lebar = {100, 150, 140, 120, 90, 110};
        for (int i = 0; i < lebar.length; i++)
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);

        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatBawah() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(WARNA_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        lblStatusDetail = new JLabel("Pilih transaksi untuk membayar atau membatalkan.");
        lblStatusDetail.setFont(new Font("Arial", Font.ITALIC, 12));
        lblStatusDetail.setForeground(Color.DARK_GRAY);
        panel.add(lblStatusDetail, BorderLayout.CENTER);

        JPanel tombolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tombolPanel.setBackground(WARNA_BG);

        btnBayar = new JButton("💳  Bayar Sekarang");
        btnBayar.setBackground(new Color(0, 150, 0));
        btnBayar.setForeground(Color.WHITE);
        btnBayar.setFocusPainted(false);
        btnBayar.setFont(new Font("Arial", Font.BOLD, 13));
        btnBayar.setEnabled(false);
        btnBayar.addActionListener(e -> handleBayar());

        btnBatal = new JButton("✖  Batalkan Transaksi");
        btnBatal.setBackground(new Color(180, 0, 0));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 13));
        btnBatal.setEnabled(false);
        btnBatal.addActionListener(e -> handleBatal());

        tombolPanel.add(btnBatal);
        tombolPanel.add(btnBayar);
        panel.add(tombolPanel, BorderLayout.EAST);

        return panel;
    }

    private void refresh() {
        int expired = paymentModel.cekExpiredPembayaran();
        int idPengguna = UserSession.getCurrentUser().getIdPengguna();
        List<Object[]> rows = paymentModel.getTransaksiPelanggan(idPengguna);

        tabelModel.setRowCount(0);
        BigDecimal walletTerbaru = BigDecimal.ZERO;

        for (Object[] row : rows) {
            int    id          = (int)       row[0];
            Timestamp tgl      = (Timestamp) row[1];
            BigDecimal total   = (BigDecimal)row[2];
            String status      = (String)    row[3];
            String metode      = (String)    row[4];
            int    detikBerlalu = (int)      row[5];

            String sisaWaktu;
            if ("Pending".equals(status)) {
                int sisa = BATAS_DETIK - detikBerlalu;
                if (sisa <= 0) {
                    sisaWaktu = "Habis";
                } else {
                    int m = sisa / 60;
                    int s = sisa % 60;
                    sisaWaktu = String.format("%d:%02d", m, s);
                }
            } else if ("Paid".equals(status)) {
                sisaWaktu = "LUNAS";
            } else {
                sisaWaktu = "GAGAL";
            }

            tabelModel.addRow(new Object[]{
                id,
                dateFmt.format(tgl),
                total,
                metode,
                status,
                sisaWaktu
            });
        }

        if (!rows.isEmpty()) {
            Object[] info = paymentModel.getInfoTransaksi(
                    (int) rows.get(0)[0], idPengguna);
            if (info != null && info[6] != null) {
                walletTerbaru = (BigDecimal) info[6];
            }
        }
        lblWallet.setText("Saldo Wallet: " + rupiahFmt.format(walletTerbaru));

        updateDetailBar();

        if (expired > 0) {
            JOptionPane.showMessageDialog(this,
                expired + " transaksi PENDING yang melebihi batas waktu 10 menit\n" +
                "telah otomatis dibatalkan dan stok dikembalikan.",
                "Transaksi Expired", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateDetailBar() {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            lblStatusDetail.setText("Pilih transaksi untuk membayar atau membatalkan.");
            btnBayar.setEnabled(false);
            btnBatal.setEnabled(false);
            return;
        }

        String status   = (String) tabelModel.getValueAt(row, 4);
        String sisaWaktu = (String) tabelModel.getValueAt(row, 5);
        BigDecimal totalVal = (BigDecimal) tabelModel.getValueAt(row, 2);
        String total    = rupiahFmt.format(totalVal); 
        int    id       = (int)    tabelModel.getValueAt(row, 0);

        boolean isPending   = "PENDING".equals(status);
        boolean masihWaktu  = isPending && !sisaWaktu.equals("Habis");

        btnBayar.setEnabled(masihWaktu);
        btnBatal.setEnabled(isPending);

        if (isPending && masihWaktu) {
            lblStatusDetail.setText("Transaksi #" + id + " | Total: " + total + " | Sisa Waktu: " + sisaWaktu);
        } else if (isPending) {
            lblStatusDetail.setText("Waktu pembayaran HABIS — klik Refresh atau tunggu auto-expire.");
        } else {
            lblStatusDetail.setText("Transaksi #" + id + " — Status: " + status);
        }
    }

    private void handleBayar() {
        int row = tabel.getSelectedRow();
        if (row == -1) return;

        int    idTransaksi = (int)    tabelModel.getValueAt(row, 0);
        BigDecimal totalVal   = (BigDecimal) tabelModel.getValueAt(row, 2);
        String total       = rupiahFmt.format(totalVal);
        int    idPengguna  = UserSession.getCurrentUser().getIdPengguna();

        boolean konfirmasi = GUIHelper.confirm(this,
                "Konfirmasi Pembayaran\n\n" +
                "ID Transaksi : #" + idTransaksi + "\n" +
                "Total        : " + total + "\n\n" +
                "Bayar menggunakan saldo wallet?");

        if (!konfirmasi) return;

        HasilPembayaran hasil = paymentModel.bayarTransaksi(idPengguna, idTransaksi);

        if (hasil.sukses) {
            JOptionPane.showMessageDialog(this,
                    hasil.pesan, "Pembayaran Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    hasil.pesan, "Pembayaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
        refresh();
    }

    private void handleBatal() {
        int row = tabel.getSelectedRow();
        if (row == -1) return;

        int idTransaksi = (int) tabelModel.getValueAt(row, 0);

        boolean konfirmasi = GUIHelper.confirm(this,
                "Batalkan transaksi #" + idTransaksi + "?\n" +
                "Stok produk akan dikembalikan dan transaksi menjadi FAILED.");
        if (!konfirmasi) return;

        HasilPembayaran hasil = paymentModel.gagalkanPembayaran(idTransaksi);

        if (hasil.sukses) {
            JOptionPane.showMessageDialog(this,
                    hasil.pesan, "Transaksi Dibatalkan", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    hasil.pesan, "Gagal Membatalkan", JOptionPane.ERROR_MESSAGE);
        }
        refresh();
    }

    private void mulaiAutoRefresh() {
        autoRefreshTimer = new Timer(true);
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refresh());
            }
        }, REFRESH_MS, REFRESH_MS);
    }
    public void stopTimer() {
        if (autoRefreshTimer != null) autoRefreshTimer.cancel();
    }
}