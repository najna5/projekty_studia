package zarzadca_package;

import myExceptions.NieprawidloweDane;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ZarzadcaDaoImp implements ZarzadcaDao {
    private Connection conn = null;
    private String date = "";
    private Scanner scanner = new Scanner(System.in);
    private final int numberOfBulidings = 3;
    private final int numberOfControllers = 2;


    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());

        }
    }

    public ZarzadcaDaoImp() {
    }

    private void connect() throws SQLException {
        if (conn != null) {
            return;
        }
        conn = DriverManager.getConnection("jdbc:sqlite:data.sqlite");
        date = "";
    }

    private void disconnect(){
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    //adding tasks to the tables
    private static void insertIntoTables(Connection conn, int selectedBuilding, String date, int controller) throws SQLException {
        String query = "INSERT INTO Zlecenia (id_budynek, termin_ostateczny, id_kontroler) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, selectedBuilding);
            ps.setString(2, date);
            ps.setInt(3, controller);
            ps.executeUpdate();
        }
    }

    //only reset when table is empty
    private void resetAutoIncrement(String tableName) throws SQLException {
        String checkIfEmptyQuery = "SELECT COUNT(*) FROM " + tableName;
        String resetSequenceQuery =  "UPDATE sqlite_sequence SET seq = 0 WHERE name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkIfEmptyQuery);
             PreparedStatement resetStmt = conn.prepareStatement(resetSequenceQuery)) {
            try(ResultSet resultSet = checkStmt.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    resetStmt.setString(1, tableName);
                    resetStmt.executeUpdate();
                }
            }
        }
    }


    @Override
    public boolean login(){
        System.out.println("Jestes zarzadca? - wpisz 'tak'");
        String answer = scanner.nextLine();
        if (answer.equals("tak")) {
            System.out.println("Witamy zarzadce!");
            return true;
        }else{
            System.out.println("brak dostepu.");
            return false;
        }
    }


    @Override
    public boolean dodajZlecenie() {

        try {
            connect();

            String queryShowBuildings = "SELECT id, adres FROM Budynki";
            try (Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(queryShowBuildings)) {

                System.out.println("Dostepne budynki: ");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String address = resultSet.getString("adres");
                    System.out.println("ID: " + id + " - Adres: ul." + address);
                }
            }

            System.out.print("Podaj id budynku, w ktorym nalezy odczytac liczniki: ");
            int selectedBuilding = scanner.nextInt();
            scanner.nextLine(); //delete \n from buffer

            if (selectedBuilding <= 0 || selectedBuilding > numberOfBulidings) {
                throw new NieprawidloweDane("Nieprawidlowe ID budynku");
            }

            System.out.println();

            System.out.println("Podaj deadline wykonania zadania: ");
            System.out.println("rok (2024-2026): ");
            int year = scanner.nextInt();
            if (year < 2024 || year > 2026) {
                throw new NieprawidloweDane("Nieprawidlowy rok");
            }

            System.out.println("miesiac: ");
            int month = scanner.nextInt();
            if (month < 1 || month > 12) {
                throw new NieprawidloweDane("Nieprawidlowy miesiac - mamy 12 miesiecy!");
            }

            int day;
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                // 31 days
                System.out.println("Dzien (1-31): ");
                day = scanner.nextInt();
                if (day < 1 || day > 31) {
                    throw new NieprawidloweDane("Nieprawidlowy dzien - miesiac ma 31 dni!");
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                // 30 days
                System.out.println("Dzien (1-30): ");
                day = scanner.nextInt();
                if (day < 1 || day > 30) {
                    throw new NieprawidloweDane("Nieprawidlowy dzien - miesiac ma 30 dni!");
                }
            } else if (month == 2 && year == 2024) {
                // February
                System.out.println("Dzien (1-29): ");
                day = scanner.nextInt();
                if (day < 1 || day > 29) {
                    throw new NieprawidloweDane("Nieprawidlowy dzien");
                }
            } else {
                System.out.println("Dzien (1-28): ");
                day = scanner.nextInt();
                if (day < 1 || day > 28) {
                    throw new NieprawidloweDane("Nieprawidlowy dzien");
                }

            }

            date = String.format("%04d-%02d-%02d", year, month, day);


            System.out.println();

            String queryShowControllers = "SELECT id, nazwa FROM Kontrolerzy";
            try (Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(queryShowControllers)) {

                System.out.println("Dostepni kontrolerzy: ");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nazwa = resultSet.getString("nazwa");
                    System.out.println("ID: " + id + " - nazwa: " + nazwa);
                }
            }

            System.out.print("Podaj id kontrolera, ktory ma wykonac zadanie: ");
            if (!scanner.hasNextInt()) {
                scanner.next(); // Oczyszczenie błędnego wejścia
                throw new NieprawidloweDane("Wprowadzono niepoprawne dane. Oczekiwano liczby.");
            }

            int controller = scanner.nextInt();
            if (controller < 1 || controller > numberOfControllers) {
                throw new NieprawidloweDane("Nie istnieje taki kontroler");
            }

            insertIntoTables(conn, selectedBuilding, date, controller);

            //System.out.println("Dodano zlecenie.");
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }catch (NieprawidloweDane n){
            System.out.println("Blad: " + n.getMessage());
            return false;
        } finally {
            disconnect();
        }
    }

    //1,4zł za kWh
    @Override
    public int policzKoszty() {
        double kWh = 1.4;
        try {
            System.out.println("Podaj ID odczytu do rozliczenia: ");
            int buildingId = scanner.nextInt();
            if (buildingId <= 0 || buildingId > numberOfBulidings) {
                throw new NieprawidloweDane("Nieprawidlowe ID budynku");
            }
            connect();

            String query = " SELECT L.id AS idLokalu, O.stan_licznika, B.licznik_glowny, " +
            " (SELECT COUNT(*) FROM Lokale WHERE Lokale.id_budynek = L.id_budynek) AS liczba_lokali " +
                " FROM Odczyty O" +
                " JOIN Lokale L ON L.id = O.id_lokal " +
                " JOIN Budynki B ON B.id = L.id_budynek " +
                " WHERE B.id = ? ";


            try(PreparedStatement statement = conn.prepareStatement(query)){
                statement.setInt(1, buildingId);
                try (ResultSet resultSet = statement.executeQuery()){

                    if (!resultSet.isBeforeFirst()) {
                        //System.out.println("Nie ma takiego odczytu.");
                        return 0;
                    }


                    String insertQuery = "INSERT INTO Rozliczenia (koszt , data_dodania, id_lokal) VALUES (?,?,?) ";
                    String deleteQuery = "DELETE FROM Odczyty WHERE id_lokal = ? ";
                    String updateQuery = "UPDATE Budynki SET licznik_glowny = NULL, data_odczytu = NULL WHERE id = ?";

                    try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                         PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery);
                         PreparedStatement updateStatement = conn.prepareStatement(updateQuery)){

                        date = LocalDate.now().toString();

                        while (resultSet.next()) {
                            int idLokal = resultSet.getInt("idLokalu");
                            double stanLicznika = resultSet.getDouble("stan_licznika");
                            double licznikGlowny = resultSet.getDouble("licznik_glowny");
                            int liczbaLokali = resultSet.getInt("liczba_lokali");

                            double koszt = ((licznikGlowny * kWh) / liczbaLokali) + (stanLicznika * kWh);

                            insertStatement.setDouble(1, koszt);
                            insertStatement.setString(2, date);
                            insertStatement.setInt(3, idLokal);
                            insertStatement.executeUpdate();

                            deleteStatement.setInt(1, idLokal);
                            deleteStatement.executeUpdate();
                            resetAutoIncrement("Odczyty");

                        }
                        updateStatement.setInt(1, buildingId);
                        updateStatement.executeUpdate();

                        //System.out.println("Rozliczono najemcow");
                        return 1;
                    }
                }
            }
        } catch (
                SQLException e) {
            System.out.println("Błąd SQL: " + e.getMessage());
            return -1;
        }catch(NieprawidloweDane n){
            System.out.println("Błąd:  "+ n.getMessage());
            return -1;
        } finally {
            disconnect();
        }
    }

    @Override
    public List<String> stanOdczytow(){
        List<String> odczyty = new ArrayList<>();
        try{
            connect();

            String query = " SELECT adres, data_odczytu, id FROM Budynki WHERE licznik_glowny IS NOT NULL ";

            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                if (!resultSet.isBeforeFirst()) {
                    //System.out.println("Nie ma zrealizowanych odczytow");
                    return odczyty;
                } else {
                    System.out.println("Dostepne dczyty w budynkach: ");
                    while (resultSet.next()) {
                        String adres = resultSet.getString("adres");
                        String data_odczytu = resultSet.getString("data_odczytu");
                        int id = resultSet.getInt("id");
                        odczyty.add("- ID: " + id + ", adres: ul." + adres + ", odczyt z dnia: " + data_odczytu);
                    }
                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            disconnect();
        }
        return odczyty;
    }
}
