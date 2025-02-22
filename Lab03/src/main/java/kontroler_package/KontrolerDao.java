package kontroler_package;

import java.util.List;

public interface KontrolerDao {

    public int username();

    //checking available task
    public List<String> sprawdzZlecenia(int userId);

    //completing the task
    public boolean odczytajLiczniki(int userId);

}
