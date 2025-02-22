package student_canteen.gui;

import student_canteen.sharedRes.QueueManager;
import student_canteen.sharedRes.Tables;
import student_canteen.threads.Cashier;
import student_canteen.threads.Dispenser;
import student_canteen.threads.Student;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MyFrame extends JFrame implements UpdateInterface {
    private final JTextArea textArea;
    private JButton CashierButton1, CashierButton2, CashierButton3;
    private final Tables tables;
    private final List<Queue<Student>> foodQueues;
    private final List<Queue<Student>> cashierQueues;
    private final Queue<Student> entranceQueue;  // Zbiór studentów, którzy jeszcze nie weszli do kolejki
    private final Object lock = new Object();
    private List<Cashier> cashiers;
    private final QueueManager queueManager;

    private final int numStudents;
    private final int numOfTables;
    private final int numOfSeats; //per one side of the table

    public MyFrame(String title) {
        super(title);
        numStudents = 26; //max 26
        numOfTables = 2;
        numOfSeats = 4;

        // Inicjalizacja GUI
        setLayout(new BorderLayout());
        textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        CashierButton1 = new JButton("kasa 1");
        CashierButton2 = new JButton("kasa 2");
        CashierButton3 = new JButton("kasa 3");

        // Inicjalizacja obiektów
        tables = new Tables(numOfTables, 2*numOfSeats);  // 2 stoły, 4 miejsca przy każdym stole
        foodQueues = List.of(new LinkedList<>(), new LinkedList<>());
        cashierQueues = List.of(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        entranceQueue = new LinkedList<>(); // Przechowywanie studentów, którzy jeszcze nie weszli do kolejki
        cashiers = new ArrayList<>();

        queueManager = new QueueManager(cashierQueues, cashiers,entranceQueue);

        CashierButton1.addActionListener(e -> queueManager.setCashierActive(0, !cashiers.get(0).isActive()));
        CashierButton2.addActionListener(e -> queueManager.setCashierActive(1, !cashiers.get(1).isActive()));
        CashierButton3.addActionListener(e -> queueManager.setCashierActive(2, !cashiers.get(2).isActive()));

        JLabel titleLabel = new JLabel("Wyłączanie/Wyłączanie kas", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonSubPanel = new JPanel();
        buttonSubPanel.setLayout(new GridLayout(1, 3));

        buttonSubPanel.add(CashierButton1);
        buttonSubPanel.add(CashierButton2);
        buttonSubPanel.add(CashierButton3);
        buttonPanel.add(buttonSubPanel, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.SOUTH);



        // Uruchomienie wątków dyspensera (2 punkty wydawania)
        for (int i = 0; i < foodQueues.size(); i++) {
            Dispenser dispenser = new Dispenser("Dispenser " + (i + 1), foodQueues.get(i));
            dispenser.setUI(this);
            dispenser.start();
        }

        // Uruchomienie wątków kasjerów (3 kasy)
        for (int i = 0; i < cashierQueues.size(); i++) {
            Cashier cashier = new Cashier("Cashier " + (i + 1), cashierQueues.get(i));
            cashier.setUI(this);
            cashiers.add(cashier);
            cashier.start();
        }

        // Domyślna operacja zamknięcia
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Uruchomienie wątków studentów (symulacja)
        new Thread(() -> simulateStudents()).start();
    }


    // Funkcja, która będzie symulować przybywanie nowych studentów
    private void simulateStudents() {
        char studentChar = 'a';
        for (int i = 0; i < numStudents; i++) {
            // tworzenie nowego studenta
            String studentId = String.valueOf(studentChar);
            Student student = new Student(studentId, foodQueues, cashierQueues, tables, entranceQueue, cashiers);

            // ustawienie refenencji UI dla stworzonego studenta
            student.setUI(this);


            entranceQueue.add(student);
            showUpdate();
            student.start();  // Start the student's thread
            showUpdate();


            // nazewnictwo studentów
            studentChar = (studentChar == 'z') ? 'a' : (char) (studentChar + 1);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    // Funkcja wywoływana przez studenta, by zaktualizować GUI
    @Override
    public void showUpdate() {
        synchronized (lock) {
            // Usuwamy poprzednią zawartość tekstową
            textArea.setText("");

            // Wyświetlamy wszystkich studentów, którzy jeszcze nie weszli do kolejki
            textArea.append("Studenci na wejściu do stołówki:\n");

            for (Student student : entranceQueue) {
                textArea.append(student.getName() + " ");
            }

            textArea.append("\n\n");

            // Wyświetlamy stan kolejek
            textArea.append("Kolejka do wydania jedzenia:\n");

            for (int i = 0; i < foodQueues.size(); i++) {
                textArea.append("Punkt " + (i + 1) + ": ");
                synchronized (foodQueues) {
                    for (Student student : foodQueues.get(i)) {
                        textArea.append(student.getName() + " ");
                    }
                }
                textArea.append("\n");
            }


            // Wyświetlamy stan kolejek do kas
            textArea.append("\nKolejka do kas:\n");

            for (int i = 0; i < cashierQueues.size(); i++) {
                textArea.append("Kasa " + (i + 1) + ": ");
                synchronized (cashierQueues) {
                    for (Student student : cashierQueues.get(i)) {
                        textArea.append(student.getName() + " ");
                    }
                }
                textArea.append("\n");
            }


            textArea.append("\n");

            synchronized (tables.getSearchSeat()) {
                for (Student student : tables.getSearchSeat()) {
                    textArea.append(student.getName() + " ");
                }
            }
            textArea.append("\n");

            // Wyświetlamy stan stolików
            textArea.append("\nStoliki:\n");
            for (int i = 0; i < numOfTables; i++) {
                textArea.append("\n");

                for (int j = 0; j < numOfSeats; j++) {
                    if (tables.isSeatOccupied(i, j)) {
                        textArea.append(tables.getStudentName(i, j) + " ");  // Zajęte miejsce
                    } else {
                        textArea.append("  ");  // Wolne miejsce
                    }
                }
                textArea.append("\n");

                // Druga linia: stół (przedstawiamy to jako '=')
                for (int j = 0; j < numOfSeats; j++) {
                    textArea.append("=");
                }
                textArea.append("\n");

                for (int j = numOfSeats; j < numOfSeats * 2; j++) {
                    if (tables.isSeatOccupied(i, j)) {
                        textArea.append(tables.getStudentName(i, j) + " ");  // Zajęte miejsce
                    } else {
                        textArea.append("  ");  // Wolne miejsce
                    }
                }
            }
            // Odświeżenie GUI
            repaint();
        }
    }
}
