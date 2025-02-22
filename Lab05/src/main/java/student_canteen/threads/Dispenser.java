package student_canteen.threads;

import java.util.Queue;
import java.util.Random;

public class Dispenser extends MyThread {
    private final Queue<Student> foodQueue;
    private final Random random = new Random();

    public Dispenser(String name, Queue<Student> foodQueue) {
        super(name);
        this.foodQueue = foodQueue;
    }



    @Override
    public void run(){
        while(!end){
            try{
                Student student;
                synchronized (foodQueue) {
                    while (foodQueue.isEmpty()) {
                        foodQueue.wait();
                    }
                    student = foodQueue.peek();
                }
                sleep((int) (Math.random() * 3 + 2) * 1000);
                ui.showUpdate();

                student.setDinner(1);
                System.out.println(student.getName()+" dostal obiad "+student.getDinner());

                synchronized (student) {
                    student.notify();
                }

                synchronized (foodQueue) {
                    foodQueue.poll();
                }

                ui.showUpdate();

            }catch(InterruptedException e){
                System.err.println("Przerwano wątek");
            }
        }
    }
}
