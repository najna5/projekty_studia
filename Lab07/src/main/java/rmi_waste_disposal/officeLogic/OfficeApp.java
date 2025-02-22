package rmi_waste_disposal.officeLogic;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.Map;

public class OfficeApp extends JFrame {
    private JTextField sewageNameField,tailorHostField, tailorPortField, tankerIdField, officeNameField;
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
        setSize(520, 500);
    }

    private void createAndShowGUI() {
        // Panel do wprowadzania danych biura
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5)); // 5 wierszy, 2 kolumny, odstępy 5px
        add(inputPanel, BorderLayout.NORTH);

        inputPanel.add(new JLabel("Port serwera tailor:"));
        tailorPortField = new JTextField("9000");
        inputPanel.add(tailorPortField);

        inputPanel.add(new JLabel("Host serwera tailor:"));
        tailorHostField = new JTextField("localhost");
        inputPanel.add(tailorHostField);

        inputPanel.add(new JLabel("nazwa oczyszczalni:"));
        sewageNameField = new JTextField("SewagePlant");
        inputPanel.add(sewageNameField);

        inputPanel.add(new JLabel("Nazwa biura:"));
        officeNameField = new JTextField("Office");
        inputPanel.add(officeNameField);

        createOfficeButton = new JButton("Utwórz Biuro");
        createOfficeButton.addActionListener(e -> {
            try {
                createOffice();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            createOfficeButton.setEnabled(false);
            createOfficeButton.setVisible(false);
        });
        inputPanel.add(createOfficeButton);

        // Panel do obsługi cystern
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        add(actionPanel, BorderLayout.CENTER);

        actionPanel.add(new JLabel("ID cysterny:"));
        tankerIdField = new JTextField(20); // Pole na ID cysterny, szerokość 20 znaków
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

    private void createOffice() throws RemoteException {
        int tailorPort = Integer.parseInt(tailorPortField.getText());
        String tailorHost = tailorHostField.getText();
        String sewageName = sewageNameField.getText();
        String officeName = officeNameField.getText();

        try {
            pOffice = new POffice(tailorHost, tailorPort, sewageName);
            pOffice.bindToRegistry(officeName);
        } catch (Exception e) {
            messageArea.append("Błąd podczas tworzenia biura: " + e.getMessage() + "\n");
        }

        updateTankers();
        displayArea.append("Biuro uruchomione \n");

        new Timer(2000, e -> updateTankers()).start();
    }

    private void updateTankers() {
        if (pOffice == null) return;

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, POffice.TankerStatus> entry : pOffice.getTankers().entrySet()) {
            POffice.TankerStatus tankerStatus = entry.getValue();
            sb.append("Cysterna ID: ").append(entry.getKey())
                    .append(" (").append(tankerStatus.getName()).append(") - ")
                    .append(tankerStatus.isReady()  ? "Dostępna" : "Niedostępna").append("\n");
        }
        displayArea.setText(sb.toString());
    }

    private void handlePayOff() {
        try {
            int tankerId = Integer.parseInt(tankerIdField.getText());
            pOffice.payOff(tankerId);
            messageArea.append("Cysterna o ID " + tankerId + " została spłacona.\n");
        } catch (NumberFormatException | RemoteException ex) {
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
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
