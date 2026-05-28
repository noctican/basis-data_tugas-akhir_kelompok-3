package ui;

import helpers.GUIHelper;
import session.UserSession;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel sidebar;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private int loggedInUserId;

    public MainFrame(int userId) {
        this.loggedInUserId = userId;
        setTitle("Eiger Store - " + (UserSession.getRole() == UserSession.Role.KARYAWAN ? "Admin Dashboard" : "Customer Portal"));
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setupSidebar();
        setupContentArea();

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    private void setupSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel brandLabel = new JLabel("EIGER STORE");
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setFont(new Font("Arial", Font.BOLD, 20));
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(brandLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        if (UserSession.getRole() == UserSession.Role.KARYAWAN) {
            setupAdminSidebar();
        } else {
            setupCustomerSidebar();
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            if (GUIHelper.confirm(this, "Are you sure you want to logout?")) {
                UserSession.logout();
                this.dispose();
                new LoginFrame().setVisible(true);
            }
        });
        sidebar.add(logoutBtn);
    }

    private void setupAdminSidebar() {
        addNavButton("Dashboard", "DASHBOARD");
        addNavButton("Inventory (Catalog)", "CATALOG");
        addNavButton("Human Resources", "HR");
        addNavButton("Customers & Members", "CUSTOMERS");
        addNavButton("Order Management", "ORDERS_ADMIN");
        addNavButton("Global Transactions", "ALL_TRANS");
        addNavButton("Returns & Returs", "RETURNS_ADMIN");
        addNavButton("My Profile", "PROFILE");
    }

    private void setupCustomerSidebar() {
        addNavButton("Dashboard & Analytics", "DASHBOARD");
        addNavButton("Shop Products", "SHOP");
        addNavButton("My Cart", "CART");
        addNavButton("My Orders", "MY_ORDERS");
        addNavButton("Returns & Help", "MY_RETURNS");
        addNavButton("My Profile & Address", "PROFILE");
    }

    private void addNavButton(String text, String cardName) {
        JButton btn = GUIHelper.createSidebarButton(text);
        btn.addActionListener(e -> {
            cardLayout.show(contentArea, cardName);
            refreshTargetPanel(cardName);
        });
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void refreshTargetPanel(String cardName) {
        if ("CART".equals(cardName)) {
            for (Component comp : contentArea.getComponents()) {
                if (comp instanceof CartPanel) ((CartPanel) comp).refreshCart();
            }
        }
    }

    private void setupContentArea() {
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        if (UserSession.getRole() == UserSession.Role.KARYAWAN) {
            contentArea.add(new DashboardPanel(), "DASHBOARD");
            contentArea.add(new CatalogPanel(), "CATALOG");
            contentArea.add(new HRPanel(this.loggedInUserId), "HR");
            contentArea.add(new CustomerPanel(), "CUSTOMERS");
            contentArea.add(new OrderAdminPanel(), "ORDERS_ADMIN");
            contentArea.add(new AllTransactionsPanel(), "ALL_TRANS");
            contentArea.add(new ReturnAdminPanel(), "RETURNS_ADMIN");
            contentArea.add(new ProfilePanel(), "PROFILE");
        } else {
            contentArea.add(new DashboardPelanggan(), "DASHBOARD");
            contentArea.add(new ShopPanel(), "SHOP");
            contentArea.add(new CartPanel(), "CART");
            contentArea.add(createPlaceholder("Your Order History"), "MY_ORDERS");
            contentArea.add(new ProfilePanel(), "PROFILE");
            contentArea.add(createPlaceholder("Initiate Returns / Help"), "MY_RETURNS");
        }
    }

    private JPanel createPlaceholder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(GUIHelper.createContentHeader(text), BorderLayout.NORTH);
        panel.add(new JLabel("Implementation view for this specific module is in Progress...", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }
}
