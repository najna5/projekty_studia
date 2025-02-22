package zarzadca_package;


import java.util.List;

public interface ZarzadcaDao {

        // adding a task: in which building to check the meters, who should do it and deadline date
        public boolean dodajZlecenie();

        // Calculate heating costs based on the controller readings
        public int policzKoszty();

        //Checking if controller has completed the task
        public List<String> stanOdczytow();

        public boolean login();


}
