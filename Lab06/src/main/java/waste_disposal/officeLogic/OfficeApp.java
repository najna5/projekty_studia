package waste_disposal.officeLogic;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class OfficeApp extends JFrame {
    private JTextField officePortField, sewageHostField, sewagePortField, tankerIdField;
    private JButton createOfficeButton, payOffButton, statusButton;
    private JTextArea displayArea;
    private JTextArea messageArea;
    private POffice pOffice;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OfficeApp().createAndShowGUI());
    }

    public OfficeApp() {
        setTitle("Office App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 500);
    }

    private void createAndShowGUI() {
        // Panel do wprowadzania danych biura
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // 4 wiersze, 2 kolumny, odstępy 5px
        add(inputPanel, BorderLayout.NORTH);

        inputPanel.add(new JLabel("Port Biura:"));
        officePortField = new JTextField("9090");
        inputPanel.add(officePortField);

        inputPanel.add(new JLabel("Host Oczyszczalni:"));
        sewageHostField = new JTextField("localhost");
        inputPanel.add(sewageHostField);

        inputPanel.add(new JLabel("Port Oczyszczalni:"));
        sewagePortField = new JTextField("9091");
        inputPanel.add(sewagePortField);

        createOfficeButton = new JButton("Utwórz Biuro");
        createOfficeButton.addActionListener(e -> {
            createOffice();
            createOfficeButton.setEnabled(false);
            createOfficeButton.setVisible(false);
        });
        inputPanel.add(createOfficeButton);

        // Panel do obsługi cystern
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        add(actionPanel, BorderLayout.CENTER);

        actionPanel.add(new JLabel("ID cysterny:"));
        tankerIdField = new JTextField(15); // Pole na ID cysterny, szerokość 15 znaków
        actionPanel.add(tankerIdField);

        payOffButton = new JButton("Spłać cysternę");
        payOffButton.setPreferredSize(new Dimension(250, 30));
        payOffButton.addActionListener(e -> handlePayOff());
        actionPanel.add(payOffButton);

        statusButton = new JButton("Sprawdź status cysterny");
        statusButton.setPreferredSize(new Dimension(250, 30));
        statusButton.addActionListener(e -> handleStatus());
        actionPanel.add(statusButton);

        // Panel wyświetlania i komunikatów
        JPanel displayMessagePanel = new JPanel(new BorderLayout());
        add(displayMessagePanel, BorderLayout.SOUTH);

        // Panel wyświetlania cystern
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane displayScrollPane = new JScrollPane(displayArea);
        displayScrollPane.setPreferredSize(new Dimension(250, 70));
        displayPanel.add(displayScrollPane, BorderLayout.CENTER);

        // Panel wyświetlania komunikatów
        JPanel messagePanel = new JPanel(new BorderLayout());
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setPreferredSize(new Dimension(250, 150));
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);

        // Dodanie paneli wyświetlania do głównego panelu
        displayMessagePanel.add(displayPanel, BorderLayout.NORTH);
        displayMessagePanel.add(messagePanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void createOffice() {
        int officePort = Integer.parseInt(officePortField.getText());
        String sewageHost = sewageHostField.getText();
        int sewagePort = Integer.parseInt(sewagePortField.getText());

        pOffice = new POffice(officePort, sewageHost, sewagePort);
        pOffice.listen();

        updateTankers();
        displayArea.append("Biuro uruchomione na porcie: " + officePort + "\n");

        new Timer(2000, e -> updateTankers()).start();
    }

    private void updateTankers() {
        if (pOffice == null) return;

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : pOffice.getTankers().entrySet()) {
            String[] parts = entry.getKey().split(";");
            sb.append("Cysterna ID: ").append(parts[0])
                    .append(" (").append(parts[1]).append(":").append(parts[2]).append(") - ")
                    .append(entry.getValue() ? "Dostępna" : "Niedostępna").append("\n");
        }
        displayArea.setText(sb.toString());
    }

    private void handlePayOff() {
        try {
            int tankerId = Integer.parseInt(tankerIdField.getText());
            pOffice.payOff(tankerId);
            messageArea.append("Cysterna o ID " + tankerId + " została spłacona.\n");
        } catch (NumberFormatException ex) {
            messageArea.append("Niepoprawny numer ID cysterny.\n");
        }
    }

    private void handleStatus() {
        try {
            int tankerId = Integer.parseInt(tankerIdField.getText());
            int status = pOffice.status(tankerId);
            if (status >= 0) {
                messageArea.append("Status cysterny o ID " + tankerId + ": " + status + " jednostek.\n");
            } else {
                messageArea.append("Nie znaleziono cysterny o ID " + tankerId + ".\n");
            }
        } catch (NumberFormatException ex) {
            messageArea.append("Niepoprawny numer ID cysterny.\n");
        }
    }
}
