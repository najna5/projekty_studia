package sand_castle_competition;

import java.util.ArrayList;
import java.util.Scanner;


/*
Autorka: Nina Masek

komentarz:
"łopatka", którą usypywane będą kolejne partie zamku jest stała i ma wartosc
ustawianą przy inicjalizacji shovel

dla uproszczenia założyłam, że każdy zamek musi mieć podstawę równą polu
miejsca, na którym stoi, że musi istnieć na każdym polu oraz ze musi byc skonczony

funkcja MAX nie spelnia założen związanych z zadaniem - średnia wysokość zamków
jest nieproporcjonalnie mniejsza niż pozostały piasek w wiaderkach
-> skupiam się więc raczej na kącie pod jakim jest każdy segment
-> Funkcja MAX jest tylko dla formalnośći
 */

public class Main {
    private static final double shovel = 10;
    private static Maths maths = new Maths();

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<Place> places = new ArrayList<>();
        ArrayList<Bucket> buckets = new ArrayList<>();
        ArrayList<Castle> castles = new ArrayList<>();

        FileReader.readFile("wiaderka.txt", places, buckets );
        FileReader.readFile("miejsca.txt", places, buckets);


        for (Place place : places) {
            Castle castle = new Castle(place.getId(), 0.0, place.getRadius());
            castles.add(castle);
        }

        System.out.println("Podaj wagi ocenianych kryteriow (wynikiem bedzie podana wartosc/w1+w2): ");
        int wH, wV;
        while (true) {
           System.out.println("wysokosc zamkow: ");
           int wh = scan.nextInt();
           System.out.println("ilosc pozostalego piasku w wiaderkach: ");
           int wv = scan.nextInt();
           if(wh<0 || wv < 0 || (wh ==0 && wv ==0)){
               System.out.println("niepoprawne wartosci, obie liczby musza byc nieujemne, a przynajmniej jedna wieksza od 0");
           }else{
               wH = wh;
               wV = wv;
               break;
           }
        }

        System.out.println("=======================================================");
        System.out.println("Dostepne zasoby: ");
        System.out.println("miejsca: ");
        for(Place p: places) {
            System.out.println(p);
        }
        System.out.println("wiaderka: ");
        for(Bucket b: buckets) {
            System.out.println(b);
        }
        System.out.println();


        maths.buildCastles(buckets, castles, shovel, wH, wV);

        System.out.println("=======================================================");
        for(Castle c: castles) {
            System.out.println(c);
        }
        /*
        System.out.println("wiaderka z pozostałym piaskiem: ");
        for(Bucket b: buckets) {
            System.out.println(b);
        }
        System.out.println();
         */


        double[] results = maths.MAX(wH,wV,buckets,castles);
        System.out.println("Srednia wysokosc zamkow: " + results[1]);
        System.out.println("Pozostaly piasek w wiaderkach: "+ results[2]);
        System.out.println("MAX: " + results[0]);
    }
}