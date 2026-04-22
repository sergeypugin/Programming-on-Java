package client.gui;

import client.locale.LocaleManager;
import client.network.NetworkClient;

import javax.swing.*;
import java.awt.*;

import static common.forCommunicate.HashUtils.hashPassword;

/**
 * Окно авторизации/регистрации.<p>
 * Сначала проверяет доступность сервера,<p>
 * Потом даёт вводить логин/пароль.
 */
public class LoginFrame extends JFrame {
    // Константы валидации (должны совпадать с UserManager на сервере)
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel titleLabel;
    private JLabel userLabel;
    private JLabel passLabel;
    private JLabel langLabel;
    private JLabel hintLabel;
    private JLabel statusLabel;

    /**
     * true = кнопка loginButton сейчас работает как «Повторить пинг»,
     * false = кнопка loginButton сейчас работает как «Войти».
     * Один ActionListener смотрит на этот флаг  никаких add/remove listeners.
     */
    private boolean isRetryMode = false;
    private String currentStatusKey = "msg.server_check"; // Запоминаем статус перевода
    private Color currentStatusColor = Color.GRAY;        // Запоминаем цвет

    public LoginFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 370);
        setLocationRelativeTo(null);
//        setResizable(false);// Камон, афигенная фича же
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

        // Форма по центру
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        userLabel = new JLabel();
        c.gridx = 0; c.gridy = 0; c.weightx = 0; c.gridwidth = 1;
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

        // Подсказка о требованиях к логину/паролю
        hintLabel = new JLabel();
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setFont(hintLabel.getFont().deriveFont(Font.PLAIN, 10f));
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.insets = new Insets(0, 6, 4, 6);
        form.add(hintLabel, c);
        c.gridwidth = 1; c.insets = new Insets(6, 6, 6, 6);

        langLabel = new JLabel();
        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        form.add(langLabel, c);
        JComboBox<String> langCombo = new JComboBox<>(LocaleManager.LOCALE_NAMES);
        // Синхронизируем кнопку с текущей локалью
        for (int i = 0; i < LocaleManager.AVAILABLE_LOCALES.length; i++) {
            if (LocaleManager.AVAILABLE_LOCALES[i].equals(LocaleManager.get().getCurrentLocale())) {
                langCombo.setSelectedIndex(i);
                break;
            }
        }
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

        loginButton = new JButton();
        registerButton = new JButton();
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        registerButton.setVisible(false);  // скрыта до успешного пинга

        loginButton.addActionListener(e -> {
            if (isRetryMode) onRetry();
            else onLogin();
        });
        registerButton.addActionListener(e -> onRegister());

//        getRootPane().setDefaultButton(loginButton);

        buttons.add(loginButton);
        buttons.add(registerButton);
        south.add(buttons, BorderLayout.CENTER);

        // Многострочный статус через HTML
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
        hintLabel.setText(LocaleManager.s("msg.login_hint"));
        registerButton.setText(LocaleManager.s("button.register"));

        if (isRetryMode) {
            // Состояние 1: Сервер не доступен
            loginButton.setText("↻ " + LocaleManager.s("button.retry"));
            setStatusByKey("msg.server_down", Color.RED);
        } else {
            loginButton.setText(LocaleManager.s("button.login"));
            if (!loginButton.isEnabled()) {
                // Состояние 2: Кнопка выключена -> Идет проверка или попытка входа
                setStatusByKey("msg.server_check", Color.GRAY);
            } else {
                // Состояние 3: Кнопка включена -> Сервер доступен, ждем действий пользователя
                setStatusByKey("msg.server_ok", Color.GREEN);
            }
        }

        if (currentStatusKey != null) {
            setStatusByKey(currentStatusKey, currentStatusColor);
        }
    }

    private void setStatusByKey(String key, Color color) {
        this.currentStatusKey = key;
        this.currentStatusColor = color;

        String text = LocaleManager.s(key);
        setStatus(text, color);
    }

    private void setStatus(String text, Color color) {
        // Экранируем HTML-символы
        String safe = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        statusLabel.setText("<html><div style='text-align:center'>" + safe + "</div></html>");
        statusLabel.setForeground(color);
    }

    /** Проверяем сервер в фоне, чтобы не блокировать EDT */
    private void checkServerAndEnable() {
        setStatusByKey("msg.server_check", Color.GRAY);
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        registerButton.setVisible(false);

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
                        isRetryMode = false;
                        setStatusByKey("msg.server_ok", Color.GREEN);
                        loginButton.setText(LocaleManager.s("button.login"));
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                        registerButton.setVisible(true);
                    } else {
                        isRetryMode = true;
                        setStatusByKey("msg.server_down", Color.RED);
                        loginButton.setText("↻ " + LocaleManager.s("button.retry"));
                        loginButton.setEnabled(true);
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    /** Нажали «Повторить» */
    private void onRetry() {
        isRetryMode = false;
        loginButton.setEnabled(false);
        checkServerAndEnable();
    }

    private void onLogin() {
        String user = userField.getText().trim();
        String pass = passField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            setStatusByKey("msg.fill_fields", Color.RED);
            return;
        }
        if (user.length() < MIN_USERNAME_LENGTH) {
            setStatusByKey("msg.login_short", Color.RED);
            return;
        }
        if (pass.length() < MIN_PASSWORD_LENGTH) {
            setStatusByKey("msg.pass_short", Color.RED);
            return;
        }
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        setStatusByKey("msg.server_check", Color.GRAY);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                var resp = NetworkClient.get().sendAuth("login", user, pass);
                return resp.isSuccess();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        NetworkClient.get().setCurrentUser(user);
                        NetworkClient.get().setUserPassword(pass);
                        openMain();
                    } else {
                        setStatusByKey("msg.auth_fail", Color.RED);
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    setStatusByKey("msg.server_down", Color.RED);
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void onRegister() {
        String user = userField.getText().trim();
        String pass = passField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            setStatusByKey("msg.fill_fields", Color.RED);
            return;
        }
        if (user.length() < MIN_USERNAME_LENGTH) {
            setStatusByKey("msg.login_short", Color.RED);
            return;
        }
        if (pass.length() < MIN_PASSWORD_LENGTH) {
            setStatusByKey("msg.pass_short", Color.RED);
            return;
        }

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        setStatusByKey("msg.server_check", Color.GRAY);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            private boolean ok;

            @Override
            protected String doInBackground() {
                String hashedPass = hashPassword(pass);
                var resp = NetworkClient.get().sendAuth("register", user, hashedPass);
                ok = resp.isSuccess();
                return resp.getMessage();
            }

            @Override
            protected void done() {
                try {
                    String msg = get();
                    if (ok) {
                        NetworkClient.get().setCurrentUser(user);
                        NetworkClient.get().setUserPassword(pass);
                        setStatusByKey("msg.register_ok", Color.GREEN);
                        openMain();
                    } else {
                        if (msg.contains("уже существует")) {
                            setStatusByKey("msg.user_exists", Color.RED);
                        } else {
                            setStatusByKey("msg.auth_fail", Color.RED);
                        }
                    }
                } catch (Exception e) {
                    setStatusByKey("msg.server_down", Color.RED);
                }
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
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
