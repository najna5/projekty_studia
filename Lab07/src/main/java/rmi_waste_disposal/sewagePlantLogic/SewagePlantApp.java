package rmi_waste_disposal.sewagePlantLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class SewagePlantApp extends JFrame {
    private JTextField nameTextField;
    private JTextField tailorHostField;
    private JTextField tailorPortField;
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
        JPanel topPanel = new JPanel(new GridLayout(4,2,10,10));

        JLabel nameLabel = new JLabel("Nazwa Oczyszczalni:");
        nameTextField = new JTextField("SewagePlant", 10);

        JLabel hostLabel = new JLabel("Host serwera tailor:");
        tailorHostField = new JTextField("localhost", 10);

        JLabel portLabel = new JLabel("Port sererwa tailor:");
        tailorPortField = new JTextField("9000", 10);


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

        topPanel.add(nameLabel);
        topPanel.add(nameTextField);
        topPanel.add(hostLabel);
        topPanel.add(tailorHostField);
        topPanel.add(portLabel);
        topPanel.add(tailorPortField);
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
                    String name = nameTextField.getText();
                    String host = tailorHostField.getText();
                    int port = Integer.parseInt(tailorPortField.getText());
                    sewagePlant = new PSewagePlant(host, port, name);
                    sewagePlant.bindToRegistry();
                    statusTextArea.append("Oczyszczalnia ścieków utworzona \n");
                    createPlantButton.setEnabled(false);
                    createPlantButton.setVisible(false);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SewagePlantApp.this, "Niepoprawna wartość portu", "Błąd", JOptionPane.ERROR_MESSAGE);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
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