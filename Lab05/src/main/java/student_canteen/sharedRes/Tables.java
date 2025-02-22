package student_canteen.sharedRes;

import student_canteen.threads.Student;

import java.util.LinkedList;
import java.util.Queue;

public class Tables {
    private final int numTables;  // Liczba stołów
    private final int numSeatsPerTable;  // Liczba miejsc przy każdym stole
    private final Student[][] seats;  // Tablica reprezentująca miejsca przy stołach (true = zajęte, false = wolne)
    private final Queue<Student> searchSeat;


    public Tables(int numTables, int numSeatsPerTable) {
        this.numTables = numTables;
        this.numSeatsPerTable = numSeatsPerTable;
        this.seats = new Student[numTables][numSeatsPerTable];// Inicjalizacja tablicy miejsc
        for (int i = 0; i < numTables; i++) {
            for (int j = 0; j < numSeatsPerTable; j++) {
                seats[i][j] = null;  // Początkowo wszystkie miejsca są wolne
            }
        }
        this.searchSeat = new LinkedList<>();
    }


    // Metoda zwracająca stan konkretnego miejsca przy stole (czy jest zajęte)
    public boolean isSeatOccupied(int tableIndex, int seatIndex) {
        return seats[tableIndex][seatIndex] != null;  // Jeśli miejsce jest zajęte przez studenta
    }

    public String getStudentName(int tableIndex, int seatIndex) {
        if (seats[tableIndex][seatIndex] != null) {
            return seats[tableIndex][seatIndex].getName();
        }else{
            return "O";
        }
    }

    // Metoda do zajmowania miejsca przy stole (jeśli jest wolne)
    public synchronized int findSeat(Student student) {
        for (int i = 0; i < numTables; i++) {
            for (int j = 0; j < numSeatsPerTable; j++) {
                if (seats[i][j] == null) {  // Jeśli miejsce jest wolne
                    seats[i][j] = student;  // Zajmujemy miejsce (przypisujemy referencję studenta)
                    return i * numSeatsPerTable + j;  // Zwróć numer miejsca
                }
            }
        }
        return -1;  // Brak wolnych miejsc

    }

    // Zwalnianie miejsca przez studenta
    public synchronized void releaseSeat(int seatIndex) {
        int tableIndex = seatIndex / numSeatsPerTable;
        int seatPosition = seatIndex % numSeatsPerTable;
        seats[tableIndex][seatPosition] = null;  // Zwalniamy miejsce

        if (!searchSeat.isEmpty()) {
            Student nextStudent = searchSeat.poll();
            synchronized (nextStudent) {
                nextStudent.notify();
            }
        }
    }

    public synchronized Queue<Student> getSearchSeat() {
        return searchSeat;
    }

    public synchronized void addToSearchArea(Student student) {
        if (!searchSeat.contains(student)) {
            searchSeat.add(student);
            notifyAll();
        }
    }
    public synchronized void removeFromSearchArea(Student student) {
        searchSeat.remove(student);
    }
}
