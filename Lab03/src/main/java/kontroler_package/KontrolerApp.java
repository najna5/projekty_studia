package kontroler_package;

import java.util.List;
import java.util.Scanner;


public class KontrolerApp {

    public static void main(String[] args) {
        KontrolerDaoImp kontrolerDaoImp = new KontrolerDaoImp();
        Scanner scanner = new Scanner(System.in);

        int id = kontrolerDaoImp.username();
        if (id == 0) {
            System.out.println("brak dostepu.");
            return;
        }

        boolean state = true;
        while(state){
        System.out.println("1 - sprawdz dostepne zlecenia" +
                "\n2 - dokonaj odczytow" +
                "\n3 - koniec");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                List<String> zlecenia = kontrolerDaoImp.sprawdzZlecenia(id);
                for (String zlecenie : zlecenia) {
                    System.out.println(zlecenie);
                }
                break;
            case 2:
                boolean result = kontrolerDaoImp.odczytajLiczniki(id);
                if (result) {
                    System.out.println("Odczyty dla danego zlecenia zostały zapisane.");
                }else{
                    System.out.println("wystapił błąd");
                }
                break;
            case 3:
                state = false;
                break;
            default:
                System.out.println("bledna liczba");
                break;
        }}
    }
}
