package rmi_waste_disposal.houseLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class HouseApp extends JFrame {
    private PHouse pHouse;  // Instancja klasy PHouse
    private JLabel levelLabel;
    private JTextField tailorPortField;
    private JTextField tailorHostField;
    private JTextField houseNameField;
    private JTextField officeNameField;
    private JTextField capacityField;  // Pole na objętość zbiornika
    private JProgressBar progressBar;  // Pasek postępu do wizualizacji zapełnienia zbiornika
    private JTextArea logArea;
    private final int fillAmount = 10;

    private Thread autoFillThread; // Wątek do automatycznego napełniania
    private boolean runningAutoFill = false; // Flaga do kontrolowania wątku

    public HouseApp() {
        setTitle("House App");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI komponenty
        levelLabel = new JLabel("Zapełnienie: 0 / 1000");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(levelLabel, BorderLayout.NORTH);

        // Panel z formularzem do wpisywania danych
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10)); // 6 wierszy, 2 kolumny, odstępy 10px

        inputPanel.add(new JLabel("Port serwera tailor:"));
        tailorPortField = new JTextField(10);
        tailorPortField.setText(String.valueOf(9000)); // Domyślny port
        inputPanel.add(tailorPortField);

        inputPanel.add(new JLabel("Host sererwa tailor:"));
        tailorHostField = new JTextField(10);
        tailorHostField.setText("localhost"); // Domyślny host
        inputPanel.add(tailorHostField);

        inputPanel.add(new JLabel("Nazwa domu:"));
        houseNameField = new JTextField(5);
        houseNameField.setText("house1"); // Domyślny port
        inputPanel.add(houseNameField);

        inputPanel.add(new JLabel("Objętość zbiornika:"));
        capacityField = new JTextField(5);
        capacityField.setText(String.valueOf(1000)); // Domyślna objętość
        inputPanel.add(capacityField);

        inputPanel.add(new JLabel("Nazwa biura:"));
        officeNameField = new JTextField(10);
        officeNameField.setText("Office");
        inputPanel.add(officeNameField);

        // Przycisk do utworzenia Domu
        JButton createHouseButton = new JButton("Utwórz Dom");
        createHouseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    createHouse();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                createHouseButton.setEnabled(false);
                createHouseButton.setVisible(false);
            }
        });
        inputPanel.add(createHouseButton);

        add(inputPanel, BorderLayout.CENTER);

        // Pasek postępu do wizualizacji poziomu
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 30));
        add(progressBar, BorderLayout.SOUTH);

        // Dodanie panelu logowania
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        add(scrollPane, BorderLayout.EAST);
    }

    private void createHouse() throws RemoteException {
        // Pobranie danych z pól tekstowych
        int tailorPort = Integer.parseInt(tailorPortField.getText());
        String tailorHost = tailorHostField.getText();
        String houseName = houseNameField.getText();
        int capacity = Integer.parseInt(capacityField.getText());
        String officeName = officeNameField.getText();

        try {
            //tworzenie domu
            pHouse = new PHouse(houseName, capacity, tailorHost, tailorPort, officeName);
            pHouse.bindToRegistry();

        }catch (RemoteException e){
            System.err.println("Błąd podczas rejestrowania domu: " + e.getMessage());
        }

        // Zresetowanie logów
        logArea.setText("");

        // Start procesu napełniania po utworzeniu domu
        startAutoFill();

        // Wyświetlenie początkowego poziomu
        levelLabel.setText("Zapełnienie: " + pHouse.getCurrentLevel() + " / " + pHouse.getCapacity());
        progressBar.setValue(0);
    }

    private void startAutoFill() {
        // Uruchomienie wątku do automatycznego napełniania
        runningAutoFill = true;
        autoFillThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (runningAutoFill) {
                    try {
                        Thread.sleep(1000); // Czekaj x sekund przed napełnieniem
                        pHouse.fill(fillAmount); // Napełnij cysternę o 10 jednostek

                        // Synchronizacja z głównym wątkiem GUI
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                levelLabel.setText("Zapełnienie: " + pHouse.getCurrentLevel() + " / " + pHouse.getCapacity());
                                progressBar.setValue(pHouse.getCurrentLevel()*100/pHouse.getCapacity()); // Aktualizuj pasek postępu

                                // Jeżeli poziom osiągnął krytyczny stan
                                if (pHouse.getCurrentLevel() >= 0.95 * pHouse.getCapacity() - fillAmount) {
                                    logArea.append("Poziom krytyczny osiągnięty!\n");
                                }
                                if(pHouse.getCurrentLevel() == pHouse.getCapacity()) {
                                    logArea.append("Zbiornik pełny!\n");
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        break; // Jeśli wątek zostanie przerwany, zakończ go
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        autoFillThread.start(); // Uruchom wątek automatycznego napełniania
    }

    // Metoda do zatrzymania automatycznego napełniania
    public void stopAutoFill() {
        runningAutoFill = false; // Zatrzymaj wątek napełniania
        if (autoFillThread != null) {
            autoFillThread.interrupt(); // Przerwij wątek, jeśli jest uruchomiony
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HouseApp app = new HouseApp();
                app.setVisible(true);
            }
        });
    }
}