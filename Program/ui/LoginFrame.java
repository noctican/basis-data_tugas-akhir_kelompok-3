package ui;

import helpers.GUIHelper;
import models.AuthModel;
import session.UserSession;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AuthModel authModel;

    public LoginFrame() {
        authModel = new AuthModel();
        
        setTitle("Eiger Store - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Welcome to Eiger Store", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.add(new JLabel("Email:"), BorderLayout.NORTH);
        emailField = new JTextField();
        emailPanel.add(emailField, BorderLayout.CENTER);
        
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.add(new JLabel("Password:"), BorderLayout.NORTH);
        passwordField = new JPasswordField();
        passPanel.add(passwordField, BorderLayout.CENTER);

        formPanel.add(emailPanel);
        formPanel.add(passPanel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(e -> handleLogin());
        mainPanel.add(loginButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            GUIHelper.showError(this, "Email and Password cannot be empty!");
            return;
        }

        if (authModel.login(email, password)) {
            GUIHelper.showInfo(this, "Login successful! Welcome, " + UserSession.getCurrentUser().getNamaDepan());
            this.dispose();
            SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        } else {
            GUIHelper.showError(this, "Invalid credentials or user role not found.");
        }
    }
}
