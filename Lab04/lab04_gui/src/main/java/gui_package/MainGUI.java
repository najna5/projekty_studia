package gui_package;

import client_Package.NfzApiClient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainGUI {
    private NfzApiClient apiClient;
    private JTable table;
    private JFrame frame;
    private JTextField pageField;
    private JLabel totalPagesLabel;
    private DefaultTableModel tableModel;

    public MainGUI(NfzApiClient apiClient) {
        this.apiClient = apiClient;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("NFZ API - Dostępne Terminy Leczenia");
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

/**********************inputPanel************************/
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));


        JLabel provinceLabel = new JLabel("Województwo:");
        Map<String, String> provinceMap = new HashMap<>();
        provinceMap.put("dolnośląskie", "01");
        provinceMap.put("kujawsko-pomorskie", "02");
        provinceMap.put("lubelskie", "03");
        provinceMap.put("lubuskie", "04");
        provinceMap.put("łódzkie", "05");
        provinceMap.put("małopolskie", "06");
        provinceMap.put("mazowieckie", "07");
        provinceMap.put("opolskie", "08");
        provinceMap.put("podkarpackie", "09");
        provinceMap.put("podlaskie", "10");
        provinceMap.put("pomorskie", "11");
        provinceMap.put("śląskie", "12");
        provinceMap.put("świętokrzyskie", "13");
        provinceMap.put("warmińsko-mazurskie", "14");
        provinceMap.put("wielkopolskie", "15");
        provinceMap.put("zachodniopomorskie", "16");

        JComboBox<String> provincesBox = new JComboBox<>(provinceMap.keySet().toArray(new String[0]));

        JLabel cityLabel = new JLabel("Miasto:");
        JTextField cityField = new JTextField();


        JLabel benefitLabel = new JLabel("Świadczenia:");
        Map<String, String> benefitMap = new LinkedHashMap<>();
        benefitMap.put("pokaż wszystkie", "");
        benefitMap.put("kardiologia", "Kardio");
        benefitMap.put("ortopedia", "Ortop");
        benefitMap.put("fizjoterapia", "Fizjo");
        benefitMap.put("stomatologia", "Stoma");
        benefitMap.put("chirurgia", "Chiru");
        benefitMap.put("onkologia", "Rak");
        JComboBox<String> benefitsBox = new JComboBox<>(benefitMap.keySet().toArray(new String[0]));
        benefitsBox.setSelectedIndex(0);


        JLabel kidsLabel = new JLabel("Świadczenia tylko dla dzieci:");
        JCheckBox kidsCheckBox = new JCheckBox("tak");

        inputPanel.add(provinceLabel);
        inputPanel.add(provincesBox);
        inputPanel.add(cityLabel);
        inputPanel.add(cityField);
        inputPanel.add(benefitLabel);
        inputPanel.add(benefitsBox);
        inputPanel.add(kidsLabel);
        inputPanel.add(kidsCheckBox);

        frame.add(inputPanel, BorderLayout.NORTH);
/*******************navigation Panel**********************************/

        JPanel navigationPanel = new JPanel();
        JButton prevButton = new JButton("Poprzednia");
        JButton nextButton = new JButton("Następna");
        pageField = new JTextField(3);
        totalPagesLabel = new JLabel("z 1");


        navigationPanel.add(prevButton);
        navigationPanel.add(new JLabel("Strona:"));
        navigationPanel.add(pageField);
        navigationPanel.add(totalPagesLabel);
        navigationPanel.add(nextButton);


/*******************results**********************************/

        String[] columnNames = {"NAZWA ŚWIADCZENIA", "ADRES", "MIASTO", "DATA"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(400);  // NAZWA ŚWIADCZENIA
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // ADRES
        table.getColumnModel().getColumn(2).setPreferredWidth(150);  // MIASTO
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // DATA

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(header.getFont().getName(), Font.BOLD, header.getFont().getSize()+2));  // Tylko pogrubienie


        JScrollPane scrollPane = new JScrollPane(table);

        frame.add(scrollPane, BorderLayout.CENTER);

/*****************button to show results*********************************/

        JButton fetchButton = new JButton("Pokaż wyniki");
        fetchButton.setPreferredSize(new Dimension(150, 40));
        fetchButton.setBackground(Color.BLUE);
        fetchButton.setOpaque(true);

        JPanel fetchButtonPanel = new JPanel();
        fetchButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        fetchButtonPanel.add(fetchButton);

/*****************bottom panel*********************************/

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(fetchButtonPanel);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(navigationPanel);

        frame.add(bottomPanel, BorderLayout.SOUTH);

/*****************Button Listeners*****************************************/

        prevButton.addActionListener(e -> {
            if(apiClient.getCurrentPage()>1) {
                apiClient.setCurrentPage(apiClient.getCurrentPage() - 1);
                refreshTable();
            }
        });

        nextButton.addActionListener(e -> {
            if (apiClient.getCurrentPage() < apiClient.getTotalPages()) {
                apiClient.setCurrentPage(apiClient.getCurrentPage() + 1);
                refreshTable();
            }
        });

        pageField.addActionListener(e -> {
            try {
                int page = Integer.parseInt(pageField.getText());
                if (page >= 1 && page <= apiClient.getTotalPages()) {
                    apiClient.setCurrentPage(page);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(frame, "Niepoprawny numer strony. Wpisz liczbę od 1 do " + apiClient.getTotalPages());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Wprowadź poprawny numer strony.");
            }
        });


        fetchButton.addActionListener(e -> {
            String selectedProvince = (String) provincesBox.getSelectedItem();
            String provinceId = provinceMap.get(selectedProvince);

            String city = cityField.getText();

            String selectedBenefit = (String) benefitsBox.getSelectedItem();
            String benefit = benefitMap.get(selectedBenefit);

            Boolean forKids = kidsCheckBox.isSelected();

            apiClient.updateParams(provinceId, city, benefit, forKids);
            apiClient.setCurrentPage(1);

            refreshTable();
        });
    }


    private void refreshTable() {
        List<String[]> data = apiClient.fetchCurrentPage();

        tableModel.setRowCount(0);
        if (data.isEmpty()) {
            tableModel.addRow(new String[]{"Brak wyników", "-", "-", "-"});
        }else {
            for (String[] row : data) {
                tableModel.addRow(row);
            }
        }

        pageField.setText(String.valueOf(apiClient.getCurrentPage()));
        totalPagesLabel.setText("z " + apiClient.getTotalPages());
    }

    public void show() {
        frame.setVisible(true);
    }

}
