package gui_package;

import client_Package.NfzApiClient;

public class AppLauncher {
    public static void main(String[] args) {
        NfzApiClient apiClient = new NfzApiClient();

        javax.swing.SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI(apiClient);
            gui.show();
        });
    }
}
