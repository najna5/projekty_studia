package waste_disposal.houseLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

public class HouseApp extends JFrame {
    private PHouse pHouse;  // Instancja klasy PHouse
    private JLabel levelLabel;
    private JTextField housePortField;
    private JTextField officeHostField;
    private JTextField officePortField;
    private JTextField capacityField;  // Pole na objętość zbiornika
    private JProgressBar progressBar;  // Pasek postępu do wizualizacji zapełnienia zbiornika
    private JTextArea logArea;

    private Thread autoFillThread; // Wątek do automatycznego napełniania
    private boolean runningAutoFill = false; // Flaga do kontrolowania wątku

    // Domyślne porty i hosty
    private static final int DEFAULT_HOUSE_PORT = 6000;
    private static final String DEFAULT_OFFICE_HOST = "localhost";
    private static final int DEFAULT_OFFICE_PORT = 9090;
    private static final int DEFAULT_CAPACITY = 1000; // Domyślna objętość zbiornika

    public HouseApp() {
        setTitle("House App");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI komponenty
        levelLabel = new JLabel("Zapełnienie: 0 / 1000");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(levelLabel, BorderLayout.NORTH);

        // Panel z formularzem do wpisywania danych
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10)); // 5 wierszy, 2 kolumny, odstępy 10px

        inputPanel.add(new JLabel("Port Domu:"));
        housePortField = new JTextField(5);
        housePortField.setText(String.valueOf(DEFAULT_HOUSE_PORT)); // Domyślny port
        inputPanel.add(housePortField);

        inputPanel.add(new JLabel("Host Biura:"));
        officeHostField = new JTextField(10);
        officeHostField.setText(DEFAULT_OFFICE_HOST); // Domyślny host
        inputPanel.add(officeHostField);

        inputPanel.add(new JLabel("Port Biura:"));
        officePortField = new JTextField(5);
        officePortField.setText(String.valueOf(DEFAULT_OFFICE_PORT)); // Domyślny port
        inputPanel.add(officePortField);

        inputPanel.add(new JLabel("Objętość zbiornika:"));
        capacityField = new JTextField(5);
        capacityField.setText(String.valueOf(DEFAULT_CAPACITY)); // Domyślna objętość
        inputPanel.add(capacityField);

        // Przycisk do utworzenia Domu
        JButton createHouseButton = new JButton("Utwórz Dom");
        createHouseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createHouse();
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

        // Dodanie panelu na logi
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        add(scrollPane, BorderLayout.EAST);
    }

    private void createHouse() {
        // Pobranie danych z pól tekstowych
        int housePort = Integer.parseInt(housePortField.getText());
        String officeHost = officeHostField.getText();
        int officePort = Integer.parseInt(officePortField.getText());
        int capacity = Integer.parseInt(capacityField.getText());

        // Tworzenie instancji PHouse z ustawionymi wartościami
        pHouse = new PHouse(housePort, capacity);
        pHouse.setOfficeHost(officeHost);
        pHouse.setOfficePort(officePort);

        pHouse.listen();

        // Zresetowanie logów
        logArea.setText("");

        // Start procesu napełniania po utworzeniu domu
        startAutoFill();

        // Wyświetlenie początkowego poziomu
        levelLabel.setText("Zapełnienie: " + pHouse.getCurrentLevel() + " / " + pHouse.getCapacity());

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
                        pHouse.fill(10); // Napełnij cysternę o 10 jednostek

                        // Synchronizacja z głównym wątkiem GUI
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                levelLabel.setText("Zapełnienie: " + pHouse.getCurrentLevel() + " / " + pHouse.getCapacity());
                                progressBar.setValue(pHouse.getCurrentLevel()*100/pHouse.getCapacity()); // Aktualizuj pasek postępu

                                // Jeżeli poziom osiągnął krytyczny stan
                                if (pHouse.getCurrentLevel() >= 0.95 * pHouse.getCapacity()) {
                                    logArea.append("Poziom krytyczny osiągnięty!\n");
                                }
                                if(pHouse.getCurrentLevel() == pHouse.getCapacity()) {
                                    logArea.append("Zbiornik pełny!\n");
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        break; // Jeśli wątek zostanie przerwany, zakończ go
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        autoFillThread.start(); // Uruchom wątek automatycznego napełniania
    }

    // Metoda do zatrzymania automatycznego napełniania - nie wykorzystywane w obecnej formie
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