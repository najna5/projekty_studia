package zarzadca_package;

import java.util.List;
import java.util.Scanner;

/*
Autorka: Nina Masek
Grupa: 3, wtorek parzysty 9.15
*/

/* komenatrze ZarządaApp:
- jest tylko jeden zarządca
- cena za 1 kWh = 1,4zł
- w przypadku chęci poszerzerzenia ilości budynków i kontrolerów w bazach danych, należy zmienić wartości finalnych zmiennych
w "ZarzadcaDaoImp" na ich nową wartość
*/

public class ZarzadcaApp {
    public static void main(String[] args) {
        ZarzadcaDaoImp zarzadcaDaoImp = new ZarzadcaDaoImp();
        Scanner scanner = new Scanner(System.in);


        if(!zarzadcaDaoImp.login()){
            return;
        }

        boolean state = true;
        while(state){
            System.out.println("1 - dodaj zlecenie" +
                    "\n2 - sprawdz stan odczytow" +
                    "\n3 - policz koszty na podstawie odczytow" +
                    "\n4 - koniec");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    boolean result = zarzadcaDaoImp.dodajZlecenie();
                    if(result){
                        System.out.println("Dodano zlecenie.");
                    }else{
                        System.out.println("błąd");
                    }
                    break;
                case 2:
                    List<String> odczyty = zarzadcaDaoImp.stanOdczytow();
                    if(odczyty.isEmpty()){
                        System.out.println("Nie ma zrealizowanych odczytow");
                    }else{
                        for (String odczyt : odczyty) {
                            System.out.println(odczyt);
                        }
                    }
                    break;
                case 3:
                    int results = zarzadcaDaoImp.policzKoszty();
                    if(results == 0){
                        System.out.println("Nie ma takiego odczytu.");
                    }else if(results == 1){
                        System.out.println("Rozliczono najemcow");
                    }else{
                        System.out.println("Bład");
                    }
                    break;
                case 4:
                    state = false;
                    break;
                default:
                    System.out.println("bledna liczba");
                    break;
            }}
    }
}
