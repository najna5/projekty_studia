package student_canteen.threads;

import java.util.Queue;
import java.util.Random;

public class Cashier extends MyThread {
    private final Queue<Student> cashierQueue;
    private final Random random = new Random();
    private boolean isActive = true;

    public Cashier(String name, Queue<Student> cashierQueue) {
        super(name);
        this.cashierQueue = cashierQueue;
    }

    public synchronized boolean isActive() {
        return isActive;
    }

    public synchronized void setActive(boolean active) {
        this.isActive = active;
    }


    @Override
    public void run() {
        while (!end) {
            try {
                Student student;
                synchronized (cashierQueue) {
                    while (cashierQueue.isEmpty()) {
                        cashierQueue.wait();
                    }
                    student = cashierQueue.peek();
                }
                sleep((int) (Math.random() * 4 + 2) * 1000);
                student.setDinner(2);//sprawdzam, czy zaplacil
                System.out.println(student.getName()+" zaplacil "+ student.getDinner());


                synchronized (student) {
                    student.notify();
                }

                synchronized (cashierQueue) {
                    cashierQueue.poll();
                }

                ui.showUpdate();

            } catch (InterruptedException e) {
                System.err.println("Przerwano wątek");
            }

        }
    }
}