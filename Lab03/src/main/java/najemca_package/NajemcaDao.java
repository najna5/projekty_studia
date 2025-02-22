package najemca_package;

import java.util.List;

public interface NajemcaDao {
    public int username();

    //pending bills
    public List<String> oczekujaceRachunki(int id);

    //pay bills
    public int zaplac(int id);

    //payment history
    public List<String> historiaPlatnosci(int id);
}
