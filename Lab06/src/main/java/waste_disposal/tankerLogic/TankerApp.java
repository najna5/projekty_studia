package waste_disposal.tankerLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class TankerApp extends JFrame {
    private JTextField tankerPortField, officeHostField, officePortField, sewageHostField, sewagePortField, maxCapacityField;
    private JButton createTankerButton;
    private JTextArea statusArea;
    private JLabel currentJobLabel, capacityLabel, tankerIdLabel;
    private JProgressBar progressBar;
    private PTanker pTanker;
    private JButton setReadyButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TankerApp().createAndShowGUI();
            }
        });
    }

    public TankerApp() {
        setTitle("Tanker App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(350, 500);
    }

    private void createAndShowGUI() {
        // Panel wyświetlania ID cysterny obok statusu
        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        add(statusPanel, BorderLayout.SOUTH);

        capacityLabel = new JLabel("Poziom ścieków: 0 / 0");
        capacityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusPanel.add(capacityLabel);

        currentJobLabel = new JLabel("Aktualne zlecenie: Brak");
        statusPanel.add(currentJobLabel);


        // Panel z ID cysterny
        JPanel tankerIdPanel = new JPanel();
        tankerIdLabel = new JLabel("ID cysterny: Brak");
        tankerIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tankerIdPanel.add(tankerIdLabel);
        statusPanel.add(tankerIdPanel);

        // Panel z polami tekstowymi do wprowadzenia danych
        JPanel inputPanel = new JPanel(new GridLayout(7, 2)); // 7 wierszy, 2 kolumny
        add(inputPanel, BorderLayout.CENTER);

        inputPanel.add(new JLabel("Port Cysterny:"));
        tankerPortField = new JTextField("8080"); // Domyślny port
        inputPanel.add(tankerPortField);

        inputPanel.add(new JLabel("Host Biura:"));
        officeHostField = new JTextField("localhost"); // Domyślny host biura
        inputPanel.add(officeHostField);

        inputPanel.add(new JLabel("Port Biura:"));
        officePortField = new JTextField("9090"); // Domyślny port biura
        inputPanel.add(officePortField);

        inputPanel.add(new JLabel("Host Oczyszczalni:"));
        sewageHostField = new JTextField("localhost"); // Domyślny host oczyszczalni
        inputPanel.add(sewageHostField);

        inputPanel.add(new JLabel("Port Oczyszczalni:"));
        sewagePortField = new JTextField("9091"); // Domyślny port oczyszczalni
        inputPanel.add(sewagePortField);

        inputPanel.add(new JLabel("Maksymalna Pojemność:"));
        maxCapacityField = new JTextField("1000"); // Domyślna maksymalna pojemność
        inputPanel.add(maxCapacityField);

        // Przycisk do utworzenia cysterny
        createTankerButton = new JButton("Utwórz Cysternę");
        createTankerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createTanker();
                createTankerButton.setEnabled(false);
                createTankerButton.setVisible(false);
            }
        });
        inputPanel.add(createTankerButton);

        setReadyButton = new JButton("Ustaw jako dostępną");
        setReadyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pTanker.setActive();
                setReadyButton.setEnabled(false);
                setReadyButton.setVisible(false);
            }
        });
        inputPanel.add(setReadyButton);

        // Pasek postępu do wizualizacji procentowego napełnienia
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 30));
        add(progressBar, BorderLayout.NORTH);

        setVisible(true);
    }

    private void createTanker() {
        // Pobranie danych z pól tekstowych
        int tankerPort = Integer.parseInt(tankerPortField.getText());
        String officeHost = officeHostField.getText();
        int officePort = Integer.parseInt(officePortField.getText());
        String sewageHost = sewageHostField.getText();
        int sewagePort = Integer.parseInt(sewagePortField.getText());
        int maxCapacity = Integer.parseInt(maxCapacityField.getText());

        // Tworzenie instancji PTanker z ustawionymi wartościami
        pTanker = new PTanker(tankerPort, officeHost, officePort, sewageHost, sewagePort, maxCapacity);

        // Rejestracja cysterny w biurze
        try {
            pTanker.registerInOffice();
            currentJobLabel.setText("Zarejestrowano cysternę. ID: " + pTanker.getId());
            tankerIdLabel.setText("ID cysterny: " + pTanker.getId()); // Ustawienie ID cysterny w etykiecie
        } catch (Exception ex) {
            currentJobLabel.setText("Błąd rejestracji cysterny.");
        }

        // Rozpoczęcie nasłuchiwania na porcie cysterny
        pTanker.listen();

        // Ustawienie początkowego statusu
        updateStatus();
    }

    private void updateStatus() {
        // update statusu cysterny co 2 sekundy
        new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pTanker != null) {
                    int currentCapacity = pTanker.getCapacity();
                    int maxCapacity = pTanker.getMaxCapacity();
                    double percentage = (double) currentCapacity / maxCapacity * 100;

                    // Aktualizacja etykiet
                    currentJobLabel.setText("Aktualne zlecenie: " + pTanker.getCurrentJob());
                    capacityLabel.setText("Poziom ścieków: " + currentCapacity + " / " + maxCapacity);

                    // Ustawienie paska postępu
                    progressBar.setValue((int) percentage);
                }
            }
        }).start();
    }
}
