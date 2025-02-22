package najemca_package;

import java.util.List;
import java.util.Scanner;

public class NajemcaApp {
    public static void main(String[] args) {
        NajemcaDaoImp najemcaDaoImp = new NajemcaDaoImp();
        Scanner scanner = new Scanner(System.in);

        int id = najemcaDaoImp.username();
        if (id == 0) {
            System.out.println("brak dostepu.");
            return;
        }

        boolean state = true;
        while(state){
            System.out.println("1 - sprawdz oczekujace rachunki" +
                    "\n2 - zaplac" +
                    "\n3 - sprawdz historie rozliczen" +
                    "\n4 - koniec");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    List<String> rachunki = najemcaDaoImp.oczekujaceRachunki(id);
                    if (rachunki.isEmpty()) {
                        System.out.println("nie ma zaleglych oplat.");
                    }else{
                        for (String rachunek : rachunki) {
                            System.out.println(rachunek);
                        }
                    }
                    break;
                case 2:
                    int result = najemcaDaoImp.zaplac(id);
                    if (result == 0) {
                        System.out.println("nie ma zaleglych oplat.");
                    } else if (result == 1) {
                        System.out.println("Zaplacono!");
                    }else{
                        System.out.println("Błąd");
                    }
                    break;
                case 3:
                    List<String> historia = najemcaDaoImp.historiaPlatnosci(id);
                    if (historia.isEmpty()) {
                        System.out.println("Nie mamy twojej historii platnosci w bazie.");
                    } else{
                        for (String historia1 : historia) {
                            System.out.println(historia1);
                        }
                    }
                    break;
                case 4:
                    state = false;
                    break;
                default:
                    System.out.println("bledna liczba");
                    break;
            }
        }
    }
}
