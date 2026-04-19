package client.gui;

import client.locale.LocaleManager;
import client.network.NetworkClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

/**
 * Окно авторизации/регистрации.
 * Сначала проверяет доступность сервера, ПОТОМ даёт вводить логин/пароль.
 */
public class LoginFrame extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JComboBox<String> langCombo;
    private JLabel titleLabel;
    private JLabel userLabel;
    private JLabel passLabel;
    private JLabel langLabel;
    private JLabel statusLabel;

    public LoginFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        applyLocale();
        checkServerAndEnable();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Заголовок
        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(titleLabel, BorderLayout.NORTH);

        // Центр: форма
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        userLabel = new JLabel();
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        form.add(userLabel, c);
        userField = new JTextField(18);
        c.gridx = 1; c.weightx = 1;
        form.add(userField, c);

        passLabel = new JLabel();
        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        form.add(passLabel, c);
        passField = new JPasswordField(18);
        c.gridx = 1; c.weightx = 1;
        form.add(passField, c);

        langLabel = new JLabel();
        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        form.add(langLabel, c);
        langCombo = new JComboBox<>(LocaleManager.LOCALE_NAMES);
        langCombo.addActionListener(e -> {
            LocaleManager.get().setLocale(LocaleManager.AVAILABLE_LOCALES[langCombo.getSelectedIndex()]);
            applyLocale();
        });
        c.gridx = 1; c.weightx = 1;
        form.add(langCombo, c);

        root.add(form, BorderLayout.CENTER);

        // Кнопки + статус
        JPanel south = new JPanel(new BorderLayout(5, 5));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginBtn = new JButton();
        registerBtn = new JButton();
        loginBtn.setEnabled(false);
        registerBtn.setEnabled(false);
        loginBtn.addActionListener(this::onLogin);
        registerBtn.addActionListener(this::onRegister);
        // Enter = логин
        getRootPane().setDefaultButton(loginBtn);
        buttons.add(loginBtn);
        buttons.add(registerBtn);
        south.add(buttons, BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.GRAY);
        south.add(statusLabel, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void applyLocale() {
        setTitle(LocaleManager.s("app.title"));
        titleLabel.setText(LocaleManager.s("login.title"));
        userLabel.setText(LocaleManager.s("login.user"));
        passLabel.setText(LocaleManager.s("login.pass"));
        langLabel.setText(LocaleManager.s("label.language"));
        loginBtn.setText(LocaleManager.s("btn.login"));
        registerBtn.setText(LocaleManager.s("btn.register"));
    }

    /** Проверяем сервер в фоне, чтобы не блокировать EDT */
    private void checkServerAndEnable() {
        statusLabel.setText(LocaleManager.s("msg.server_check"));
        statusLabel.setForeground(Color.GRAY);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return NetworkClient.get().isServerAvailable();
            }
            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        statusLabel.setText("✓ Сервер доступен");
                        statusLabel.setForeground(new Color(0, 140, 0));
                        loginBtn.setEnabled(true);
                        registerBtn.setEnabled(true);
                    } else {
                        statusLabel.setText(LocaleManager.s("msg.server_down"));
                        statusLabel.setForeground(Color.RED);
                        // Retry button
                        loginBtn.setText("↻ Повторить");
                        loginBtn.setEnabled(true);
                        loginBtn.removeActionListener(loginBtn.getActionListeners()[0]);
                        loginBtn.addActionListener(e -> {
                            loginBtn.setEnabled(false);
                            loginBtn.setText(LocaleManager.s("btn.login"));
                            loginBtn.addActionListener(LoginFrame.this::onLogin);
                            checkServerAndEnable();
                        });
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    private void onLogin(ActionEvent e) {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Введите логин и пароль");
            statusLabel.setForeground(Color.RED);
            return;
        }
        loginBtn.setEnabled(false);
        registerBtn.setEnabled(false);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            private String msg;
            @Override
            protected Boolean doInBackground() {
                var resp = NetworkClient.get().sendAuth("login", user, pass);
                msg = resp.getMessage();
                return resp.isSuccess();
            }
            @Override
            protected void done() {
                try {
                    if (get()) {
                        NetworkClient.get().setCurrentUser(user);
                        openMain();
                    } else {
                        statusLabel.setText(msg);
                        statusLabel.setForeground(Color.RED);
                        loginBtn.setEnabled(true);
                        registerBtn.setEnabled(true);
                    }
                } catch (Exception ex) {
                    statusLabel.setText(LocaleManager.s("msg.server_down"));
                    statusLabel.setForeground(Color.RED);
                    loginBtn.setEnabled(true);
                    registerBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void onRegister(ActionEvent e) {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Введите логин и пароль");
            statusLabel.setForeground(Color.RED);
            return;
        }
        loginBtn.setEnabled(false);
        registerBtn.setEnabled(false);
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            private boolean ok;
            @Override
            protected String doInBackground() {
                var resp = NetworkClient.get().sendAuth("register", user, pass);
                ok = resp.isSuccess();
                return resp.getMessage();
            }
            @Override
            protected void done() {
                try {
                    String msg = get();
                    if (ok) {
                        statusLabel.setText(msg);
                        statusLabel.setForeground(new Color(0, 140, 0));
                        try (java.io.FileWriter writer = new java.io.FileWriter("my_accounts.txt", true)) {
                            writer.write("Логин: " + user + " | Пароль: " + pass + "\n"); writer.flush();
                        } catch (java.io.IOException ex) {
                            System.err.println("Не удалось сохранить аккаунт в файл"); System.err.flush();
                        }
                    } else {
                        statusLabel.setText(msg);
                        statusLabel.setForeground(Color.RED);
                    }
                } catch (Exception ex) {
                    statusLabel.setText(LocaleManager.s("msg.server_down"));
                    statusLabel.setForeground(Color.RED);
                }
                loginBtn.setEnabled(true);
                registerBtn.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void openMain() {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
            dispose();
        });
    }
}
