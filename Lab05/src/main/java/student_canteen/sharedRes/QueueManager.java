package student_canteen.sharedRes;
import student_canteen.threads.Student;
import student_canteen.threads.Cashier;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class QueueManager {
    private final List<Queue<Student>> cashierQueues;
    private final List<Cashier> cashiers;
    private final Queue<Student> entranceQueue;

    public QueueManager(List<Queue<Student>> cashierQueues, List<Cashier> cashiers, Queue<Student> entranceQueue) {
        this.cashierQueues = cashierQueues;
        this.cashiers = cashiers;
        this.entranceQueue = entranceQueue;
    }

    // Zwróć listę aktywnych kolejek
    public List<Queue<Student>> getActiveCashierQueues() {
        synchronized (cashierQueues) {
            List<Queue<Student>> activeQueues = new ArrayList<>();
            for (int i = 0; i < cashierQueues.size(); i++) {
                if (cashiers.get(i).isActive()) {
                    activeQueues.add(cashierQueues.get(i)); // Dodajemy tylko aktywne kolejki
                }
            }
            return activeQueues;
        }
    }

    // Zaktualizuj stan kasjera (włącz lub wyłącz)
    public void setCashierActive(int cashierIndex, boolean active) {
        if (cashierIndex < 0 || cashierIndex >= cashiers.size()) {
            throw new IllegalArgumentException("Nieprawidłowy indeks kasjera: " + cashierIndex);
        }
        Cashier cashier = cashiers.get(cashierIndex);

        synchronized (cashierQueues) {
            if (!active) {
                // Sprawdź, czy próba wyłączenia spowoduje brak aktywnych kas
                long activeCashiersCount = cashiers.stream().filter(Cashier::isActive).count();
                if (activeCashiersCount <= 1) {
                    // Jeśli to ostatnia aktywna kasa, blokujemy wyłączenie
                    System.out.println("Nie można wyłączyć ostatniej aktywnej kasy!");
                    return;
                }
            }

            // Ustaw stan kasjera
            cashier.setActive(active);
            cashierQueues.notifyAll();
        }
    }


    // Usuwanie studenta z listy oczekujących
    public void removeFromEntranceQueue(Student student) {
        synchronized (entranceQueue) {
            entranceQueue.remove(student);  // Usuwamy studenta z listy oczekujących
        }
    }

}