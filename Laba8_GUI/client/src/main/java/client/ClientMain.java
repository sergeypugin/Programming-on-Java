package client;

import client.gui.LoginFrame;
import client.locale.LocaleManager;
import client.network.NetworkClient;
import client.theme.ThemeManager;

import javax.swing.*;

/**
 * Точка входа GUI-клиента.
 * Инициализирует NetworkClient и запускает LoginFrame.
 */
public class ClientMain {
    static {
        System.setProperty("log4j.configurationFile", "log4j2-client.xml");
    }

    public static void main(String[] args) {
        int serverPort = 1024 + 500;
        String serverHost = "localhost";

        if (args.length > 0) serverHost = args[0];
        if (args.length > 1) {
            try { serverPort = Integer.parseInt(args[1]); }
            catch (NumberFormatException e) { /* оставляем дефолт */ }
        }

        NetworkClient.initialize(serverHost, serverPort);
        LocaleManager.get();
        ThemeManager.get();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
