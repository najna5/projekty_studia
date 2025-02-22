package waste_disposal.sewagePlantLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SewagePlantApp extends JFrame {
    private JTextField portTextField;
    private JButton createPlantButton;
    private JTextArea statusTextArea;
    private PSewagePlant sewagePlant;

    public SewagePlantApp() {
        setTitle("SewagePlant App");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel do wpisania portu i przycisku
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel portLabel = new JLabel("Port Oczyszczalni:");
        portTextField = new JTextField("9091", 10);
        createPlantButton = new JButton("Utwórz oczyszczalnię");

        JButton showStatusButton = new JButton("Pokaż stan cystern");
        showStatusButton.addActionListener(e -> {
            if (sewagePlant != null) {
                String status = sewagePlant.getSewageMap().toString();
                statusTextArea.append("Stan cystern: " + status + "\n");
            } else {
                statusTextArea.append("Brak danych.\n");
            }
        });

        topPanel.add(portLabel);
        topPanel.add(portTextField);
        topPanel.add(createPlantButton);
        topPanel.add(showStatusButton);

        // Text area do wyświetlania statusu
        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);


        // Dodaj komponenty do głównego okna
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Dodaj nasłuchiwanie zdarzeń dla przycisku
        createPlantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(portTextField.getText());
                    sewagePlant = new PSewagePlant(port);
                    sewagePlant.listen();
                    statusTextArea.append("Oczyszczalnia ścieków utworzona na porcie " + port + "\n");
                    createPlantButton.setEnabled(false);
                    createPlantButton.setVisible(false);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SewagePlantApp.this, "Niepoprawna wartość portu", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void updateStatusText(String text) {
        SwingUtilities.invokeLater(() -> statusTextArea.append(text + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SewagePlantApp().setVisible(true);
            }
        });
    }
}