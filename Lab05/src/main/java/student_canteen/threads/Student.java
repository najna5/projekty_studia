package student_canteen.threads;

import student_canteen.sharedRes.QueueManager;
import student_canteen.sharedRes.Tables;

import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Student extends MyThread {
    private final List<Queue<Student>> foodQueues;
    private final List<Queue<Student>> cashierQueues;
    private final Tables tables;
    private final Random random = new Random();
    int dinner;
    private final Queue<Student> entranceQueue;
    private final QueueManager queueManager;
    private final List<Cashier> cashiers;


    public Student(String name, List<Queue<Student>> foodQueues, List<Queue<Student>> cashierQueues, Tables tables, Queue<Student> entranceQueue, List<Cashier> cashiers){
        super(name);
        this.foodQueues = foodQueues;
        this.cashierQueues = cashierQueues;
        this.tables = tables;
        this.cashiers = cashiers;
        this.dinner = 0;
        this.entranceQueue = entranceQueue;
        this.queueManager = new QueueManager(cashierQueues, cashiers, entranceQueue);
    }

    @Override
    public void run(){
        while(!end){
            try{
                setDinner(0);

                synchronized (entranceQueue) {
                    if (!entranceQueue.contains(this)) {
                        entranceQueue.add(this);
                        ui.showUpdate();
                    }
                }

                sleep(2000);

                synchronized (this) {
                    notify();
                }

                // Kolejka do dyspensera
                Queue<Student> dispenserQueue;
                synchronized (foodQueues) {
                    dispenserQueue = chooseShortestQueue(foodQueues);
                }
                // (synchronizacja wątku w tym miejscu - można poprawić)
                synchronized (dispenserQueue) {
                    dispenserQueue.add(this);
                    queueManager.removeFromEntranceQueue(this);
                    ui.showUpdate();
                    dispenserQueue.notify();
                }
                synchronized(this){
                    wait();
                }

                // Kolejka do kasy
                Queue<Student> cashierQueue;
                synchronized (cashierQueues) {
                    List<Queue<Student>> activeCashierQueues = queueManager.getActiveCashierQueues();
                    cashierQueue = chooseShortestQueue(activeCashierQueues);
                }

                synchronized (cashierQueue) {
                    cashierQueue.add(this);
                    cashierQueue.notify();
                    ui.showUpdate();
                }

                synchronized(this){
                    wait();
                }

                tables.addToSearchArea(this);
                sleep(1500);
                ui.showUpdate();

                // Szukanie miejsca przy stole
                int seat = tables.findSeat(this);
                if (seat == -1) {
                    synchronized (this) {
                        wait();
                    }
                    seat = tables.findSeat(this);
                }
                tables.removeFromSearchArea(this);
                consumeMeal(seat);

                sleep((int) (Math.random() * 9 + 2) * 1000);

            }catch(InterruptedException e){
                System.err.println("Przerwano wątek");
            }
        }
    }


    private Queue<Student> chooseShortestQueue(List<Queue<Student>> queues) {
        int minIndex = -1;
        int minSize = Integer.MAX_VALUE;

        for (int i = 0; i < queues.size(); i++) {
            Queue<Student> queue = queues.get(i);

            // Porównaj długości kolejek
            if (queue.size() < minSize) {
                minSize = queue.size();
                minIndex = i;
            }
        }

        if (minIndex == -1) {
            throw new IllegalStateException("Nie znaleziono żadnych aktywnych kolejek!");
        }
        return queues.get(minIndex);
    }

    private void consumeMeal(int seat) throws InterruptedException {
        System.out.println(getName() + " zajmuje miejsce " + seat + " i spożywa posiłek.");
        ui.showUpdate();
        Thread.sleep((int) (Math.random() * 14 + 5) * 1000); // Symulacja jedzenia
        tables.releaseSeat(seat);
        System.out.println(getName() + " zwolnił miejsce " + seat + ".");
        ui.showUpdate();
    }
}
