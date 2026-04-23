package client.gui;

import client.locale.LocaleManager;
import client.network.NetworkClient;
import client.theme.Theme;
import client.theme.ThemeAware;
import client.theme.ThemeManager;
import client.theme.ThemeRole;
import client.theme.ThemeStyler;
import common.forCommunicate.AuthResponseCode;
import common.forCommunicate.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static common.forCommunicate.HashUtils.hashPassword;

/**
 * Окно авторизации/регистрации.
 * Клиент намеренно отправляет не исходный пароль, а его первый SHA-384 хэш.
 */
public class LoginFrame extends JFrame implements ThemeAware {
    private static final String SERVER_OK_MARK = "\u2713 ";

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel titleLabel;
    private JLabel userLabel;
    private JLabel passLabel;
    private JLabel langLabel;
    private JLabel themeLabel;
    private JLabel hintLabel;
    private JLabel statusLabel;
    private JPanel rootPanel;
    private JPanel formPanel;
    private JPanel southPanel;
    private JPanel buttonsPanel;
    private JComboBox<String> langCombo;
    private JComboBox<String> themeCombo;

    private boolean isRetryMode = false;
    private String currentStatusKey = "msg.server_check";
    private ThemeRole currentStatusRole = ThemeRole.MUTED_TEXT;

    public LoginFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 410);
        setLocationRelativeTo(null);
        buildUI();
        registerForThemeUpdates();
        applyLocale();
        applyTheme();
        checkServerAndEnable();
    }

    private void registerForThemeUpdates() {
        ThemeManager.get().registerListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ThemeManager.get().unregisterListener(LoginFrame.this);
            }
        });
    }

    private void buildUI() {
        rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        rootPanel.add(titleLabel, BorderLayout.NORTH);

        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        userLabel = new JLabel();
        c.gridx = 0; c.gridy = 0; c.weightx = 0; c.gridwidth = 1;
        formPanel.add(userLabel, c);
        userField = new JTextField(18);
        c.gridx = 1; c.weightx = 1;
        formPanel.add(userField, c);

        passLabel = new JLabel();
        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        formPanel.add(passLabel, c);
        passField = new JPasswordField(18);
        c.gridx = 1; c.weightx = 1;
        formPanel.add(passField, c);

        hintLabel = new JLabel();
        hintLabel.setFont(hintLabel.getFont().deriveFont(Font.PLAIN, 10f));
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.insets = new Insets(0, 6, 4, 6);
        formPanel.add(hintLabel, c);
        c.gridwidth = 1; c.insets = new Insets(6, 6, 6, 6);

        langLabel = new JLabel();
        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        formPanel.add(langLabel, c);
        langCombo = new JComboBox<>(LocaleManager.LOCALE_NAMES);
        for (int i = 0; i < LocaleManager.AVAILABLE_LOCALES.length; i++) {
            if (LocaleManager.AVAILABLE_LOCALES[i].equals(LocaleManager.get().getCurrentLocale())) {
                langCombo.setSelectedIndex(i);
                break;
            }
        }
        langCombo.addActionListener(e -> {
            LocaleManager.get().setLocale(LocaleManager.AVAILABLE_LOCALES[langCombo.getSelectedIndex()]);
            applyLocale();
            applyTheme();
        });
        c.gridx = 1; c.weightx = 1;
        formPanel.add(langCombo, c);

        themeLabel = new JLabel();
        c.gridx = 0; c.gridy = 4; c.weightx = 0;
        formPanel.add(themeLabel, c);
        themeCombo = new JComboBox<>();
        themeCombo.addActionListener(e -> {
            int idx = themeCombo.getSelectedIndex();
            if (idx >= 0 && idx < ThemeManager.get().getAvailableThemes().size()) {
                ThemeManager.get().setTheme(ThemeManager.get().getAvailableThemes().get(idx));
            }
        });
        c.gridx = 1; c.weightx = 1;
        formPanel.add(themeCombo, c);

        rootPanel.add(formPanel, BorderLayout.CENTER);

        southPanel = new JPanel(new BorderLayout(5, 5));
        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        loginButton = new JButton();
        registerButton = new JButton();
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        registerButton.setVisible(false);

        loginButton.addActionListener(e -> {
            if (isRetryMode) {
                onRetry();
            } else {
                onLogin();
            }
        });
        registerButton.addActionListener(e -> onRegister());

        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);
        southPanel.add(buttonsPanel, BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        rootPanel.add(southPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private void applyLocale() {
        setTitle(LocaleManager.s("app.title"));
        titleLabel.setText(LocaleManager.s("login.title"));
        userLabel.setText(LocaleManager.s("login.user"));
        passLabel.setText(LocaleManager.s("login.pass"));
        langLabel.setText(LocaleManager.s("label.language"));
        themeLabel.setText(LocaleManager.s("label.theme"));
        hintLabel.setText(LocaleManager.s("msg.login_hint"));
        registerButton.setText(LocaleManager.s("button.register"));
        rebuildThemeCombo();

        if (isRetryMode) {
            loginButton.setText("↻ " + LocaleManager.s("button.retry"));
        } else {
            loginButton.setText(LocaleManager.s("button.login"));
        }
        renderStatus();
    }

    private void renderStatus() {
        String text = LocaleManager.s(currentStatusKey);
        if ("msg.server_ok".equals(currentStatusKey)) {
            text = SERVER_OK_MARK + text;
        }
        setStatusText(text, ThemeManager.get().getTheme().color(currentStatusRole));
    }

    private void setStatusByKey(String key, ThemeRole role) {
        currentStatusKey = key;
        currentStatusRole = role;
        renderStatus();
    }

    private void setStatusText(String text, Color color) {
        String safe = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        statusLabel.setText("<html><div style='text-align:center'>" + safe + "</div></html>");
        statusLabel.setForeground(color);
    }

    private void checkServerAndEnable() {
        setStatusByKey("msg.server_check", ThemeRole.MUTED_TEXT);
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        registerButton.setVisible(false);
        applyTheme();

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
                        setStatusByKey("msg.server_ok", ThemeRole.SUCCESS);
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                        registerButton.setVisible(true);
                    } else {
                        isRetryMode = true;
                        setStatusByKey("msg.server_down", ThemeRole.ERROR);
                        loginButton.setEnabled(true);
                    }
                    applyLocale();
                    applyTheme();
                } catch (Exception ignored) {
                    setStatusByKey("msg.server_down", ThemeRole.ERROR);
                    isRetryMode = true;
                    loginButton.setEnabled(true);
                    applyLocale();
                    applyTheme();
                }
            }
        };
        worker.execute();
    }

    private void onRetry() {
        isRetryMode = false;
        loginButton.setEnabled(false);
        applyLocale();
        applyTheme();
        checkServerAndEnable();
    }

    private void onLogin() {
        String user = userField.getText().trim();
        String pass = passField.getText().trim();

        if (!isValidInput(user, pass)) {
            return;
        }

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        applyTheme();
        setStatusByKey("msg.server_check", ThemeRole.MUTED_TEXT);

        SwingWorker<AuthResponseCode, Void> worker = new SwingWorker<>() {
            final private String hashedPass = hashPassword(pass);

            @Override
            protected AuthResponseCode doInBackground() {
                Response resp = NetworkClient.get().sendAuth("login", user, hashedPass);
                if (resp.getData() instanceof AuthResponseCode code) {
                    return code;
                }
                return resp.isSuccess() ? AuthResponseCode.LOGIN_OK : AuthResponseCode.LOGIN_FAILED;
            }

            @Override
            protected void done() {
                try {
                    AuthResponseCode code = get();
                    if (code == AuthResponseCode.LOGIN_OK) {
                        NetworkClient.get().setCurrentUser(user);
                        NetworkClient.get().setUserPassword(hashedPass);
                        openMain();
                    } else {
                        setStatusByKey("msg.auth_fail", ThemeRole.ERROR);
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                        applyTheme();
                    }
                } catch (Exception e) {
                    setStatusByKey("msg.server_down", ThemeRole.ERROR);
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    applyTheme();
                }
            }
        };
        worker.execute();
    }

    private void onRegister() {
        String user = userField.getText().trim();
        String pass = passField.getText().trim();

        if (!isValidInput(user, pass)) {
            return;
        }

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        applyTheme();
        setStatusByKey("msg.server_check", ThemeRole.MUTED_TEXT);

        SwingWorker<AuthResponseCode, Void> worker = new SwingWorker<>() {
            final private String hashedPass = hashPassword(pass);

            @Override
            protected AuthResponseCode doInBackground() {
                Response resp = NetworkClient.get().sendAuth("register", user, hashedPass);
                if (resp.getData() instanceof AuthResponseCode code) {
                    return code;
                }
                return resp.isSuccess() ? AuthResponseCode.REGISTER_OK : AuthResponseCode.REGISTER_INVALID;
            }

            @Override
            protected void done() {
                try {
                    AuthResponseCode code = get();
                    if (code == AuthResponseCode.REGISTER_OK) {
                        NetworkClient.get().setCurrentUser(user);
                        NetworkClient.get().setUserPassword(hashedPass);
                        setStatusByKey("msg.register_ok", ThemeRole.SUCCESS);
                        openMain();
                    } else if (code == AuthResponseCode.REGISTER_USER_EXISTS) {
                        setStatusByKey("msg.user_exists", ThemeRole.ERROR);
                    } else {
                        setStatusByKey("msg.register_invalid", ThemeRole.ERROR);
                    }
                } catch (Exception e) {
                    setStatusByKey("msg.server_down", ThemeRole.ERROR);
                }
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                applyTheme();
            }
        };
        worker.execute();
    }

    private boolean isValidInput(String user, String pass) {
        if (user.isEmpty() || pass.isEmpty()) {
            setStatusByKey("msg.fill_fields", ThemeRole.ERROR);
            return false;
        }
        if (user.length() < 3) {
            setStatusByKey("msg.login_short", ThemeRole.ERROR);
            return false;
        }
        if (pass.length() < 6) {
            setStatusByKey("msg.pass_short", ThemeRole.ERROR);
            return false;
        }
        return true;
    }

    private void rebuildThemeCombo() {
        int previous = themeCombo.getSelectedIndex();
        themeCombo.removeAllItems();
        java.util.List<Theme> themes = ThemeManager.get().getAvailableThemes();
        for (Theme theme : themes) {
            themeCombo.addItem(LocaleManager.s(theme.displayNameKey()));
        }
        int selectedIndex = java.util.stream.IntStream.range(0, themes.size())
                .filter(i -> themes.get(i).id().equals(ThemeManager.get().getTheme().id()))
                .findFirst()
                .orElse(Math.max(previous, 0));
        if (selectedIndex >= 0 && selectedIndex < themes.size()) {
            themeCombo.setSelectedIndex(selectedIndex);
        }
    }

    private void openMain() {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
            dispose();
        });
    }

    @Override
    public void applyTheme() {
        Theme theme = ThemeManager.get().getTheme();
        ThemeStyler.stylePanel(rootPanel, theme, ThemeRole.BACKGROUND);
        ThemeStyler.stylePanel(formPanel, theme, ThemeRole.BACKGROUND);
        ThemeStyler.stylePanel(southPanel, theme, ThemeRole.BACKGROUND);
        buttonsPanel.setOpaque(false);

        ThemeStyler.styleLabel(titleLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(userLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(passLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(langLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(themeLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(hintLabel, theme, ThemeRole.MUTED_TEXT);
        ThemeStyler.styleLabelsRecursively(formPanel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleTextComponent(userField, theme);
        ThemeStyler.styleTextComponent(passField, theme);
        ThemeStyler.styleComboBox(langCombo, theme);
        ThemeStyler.styleComboBox(themeCombo, theme);
        ThemeStyler.styleButton(loginButton, theme, true);
        ThemeStyler.styleButton(registerButton, theme, false);
        ThemeStyler.refreshButtonState(loginButton, theme, true);
        ThemeStyler.refreshButtonState(registerButton, theme, false);
        renderStatus();

        getContentPane().setBackground(theme.color(ThemeRole.BACKGROUND));
        repaint();
    }
}
