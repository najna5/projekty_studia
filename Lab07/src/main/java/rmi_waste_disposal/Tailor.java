package rmi_waste_disposal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Tailor extends JFrame {
    private JTextField tailorPortField;
    private JButton startButton;
    private JTextArea statusTextArea;

    public Tailor() {
        setTitle("RMI Tailor Server");
        setSize(300, 150);  // Ustawienie rozmiaru okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Używamy GridLayout, żeby upewnić się, że wszystkie komponenty są dobrze rozmieszczone
        setLayout(new GridLayout(3, 1, 10, 10)); // 3 wiersze, 1 kolumna, odstęp 10px

        // Tworzymy panele
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel portLabel = new JLabel("Wpisz port RMI:");
        tailorPortField = new JTextField("9000", 10);  // Domyślny port
        panel1.add(portLabel);
        panel1.add(tailorPortField);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("Uruchom RMI Registry");
        panel2.add(startButton);

        JPanel panel3 = new JPanel(new BorderLayout());
        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        panel3.add(scrollPane, BorderLayout.CENTER);

        // Obsługa kliknięcia przycisku
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(tailorPortField.getText());
                    startRMIRegistry(port);
                    startButton.setEnabled(false);
                    startButton.setVisible(false);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Tailor.this, "Niepoprawna wartość portu", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Dodanie paneli do okna
        add(panel1);
        add(panel2);
        add(panel3);
    }

    // Metoda uruchamiająca RMI Registry na wybranym porcie
    private void startRMIRegistry(int port) {
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            statusTextArea.append("RMI Registry uruchomiony na porcie " + port + "\n");
        } catch (RemoteException e) {
            statusTextArea.append("Błąd podczas uruchamiania RMI Registry: " + e.getMessage() + "\n");
        }
    }

    // Główna metoda uruchamiająca aplikację
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Tailor().setVisible(true);
            }
        });
    }
}
