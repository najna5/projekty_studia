package kontroler_package;

import myExceptions.NieprawidloweDane;
import myExceptions.NieprawidlowyLogin;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KontrolerDaoImp implements KontrolerDao {
    private Connection conn = null;
    private Scanner scanner = new Scanner(System.in);

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());

        }
    }

    public KontrolerDaoImp() {}

    private void connect() throws SQLException {
        if (conn != null) {
            return;
        }
        conn = DriverManager.getConnection("jdbc:sqlite:data.sqlite");
    }

    private void disconnect() {
        if (conn != null){
            try{
                conn.close();
                conn = null;
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }

    //95 - 120 kWh (120-95) = 25
    private double meter_readings(){
        return Math.random() * (25) + 95;
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
    public int username() {
        int userId = 0;
        try{
            connect();

            String queryAvailable = "SELECT nazwa FROM Kontrolerzy";
            try (PreparedStatement statement = conn.prepareStatement(queryAvailable);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("Dostepni uzytkownicy:");
                while (resultSet.next()) {
                    System.out.println("- " + resultSet.getString("nazwa"));
                }
            }

            System.out.print("Podaj login: ");
            String userName = scanner.nextLine();


            String findQuery = "SELECT id FROM Kontrolerzy WHERE nazwa = ?";
            try (PreparedStatement findStatement = conn.prepareStatement(findQuery)) {
                findStatement.setString(1, userName);
                try (ResultSet findResult = findStatement.executeQuery()) {
                    if (findResult.next()) {
                        userId = findResult.getInt("id");
                    } else {
                        throw new NieprawidlowyLogin("Podany uzytkwonik nie istnieje");
                    }
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch(NieprawidlowyLogin n){
            System.out.println("Blad: " + n.getMessage());
        }finally {
            disconnect();
        }
        return userId;
    }

    @Override
    public List<String> sprawdzZlecenia(int userId) {
        List<String> listaZlecen = new ArrayList<>();
        try {
            connect();
            String query = "SELECT Z.id AS id_zlecenia, Z.termin_ostateczny, B.adres, K.nazwa " +
                    "FROM Zlecenia Z " +
                    "JOIN Budynki B ON Z.id_budynek = B.id " +
                    "JOIN Kontrolerzy K ON Z.id_kontroler = K.id " +
                    "WHERE Z.id_kontroler = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        System.out.println("Brak zleceń dla danego uzytkownika");
                    } else {
                        System.out.println("Zlecenia uzytkownika = " + rs.getString("nazwa") + ":");
                        while (rs.next()) {
                            String terminOstateczny = rs.getString("termin_ostateczny");
                            String adres = rs.getString("adres");
                            int idZlecenia = rs.getInt("id_zlecenia");

                            listaZlecen.add("ID: " +  idZlecenia +
                                    ", Termin Ostateczny: " + terminOstateczny +
                                    ", Adres Budynku: " + adres);
                        }
                    }
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            disconnect();
        }
        return listaZlecen;
    }

    @Override
    public boolean odczytajLiczniki(int id){
        System.out.println("Podaj nr zlecenia do wykonania: ");
        int task = scanner.nextInt();
        try {
            connect();
            String queryZlecenia = "SELECT L.id AS id_lokal, K.nazwa AS wykonawca, B.id AS budynek  " +
                    "FROM Zlecenia Z " +
                    "JOIN Budynki B ON Z.id_budynek = B.id " +
                    "JOIN lokale L ON B.id = L.id_budynek " +
                    "JOIN Kontrolerzy K ON Z.id_kontroler = K.id " +
                    "WHERE Z.id_kontroler = ? AND Z.id = ?";

            try (PreparedStatement psZlecenia = conn.prepareStatement(queryZlecenia)) {
                psZlecenia.setInt(1, id);
                psZlecenia.setInt(2, task);

                try (ResultSet rsZlecenia = psZlecenia.executeQuery()) {
                    if (!rsZlecenia.isBeforeFirst()) {

                        throw new NieprawidloweDane("Podane zlecenie nie istnieje");
                    }

                    String date = LocalDate.now().toString();

                    String queryOdczyt = "INSERT INTO Odczyty (id_lokal, stan_licznika, rzeczywista_data_wykonania, wykonawca) " +
                            "VALUES (?, ?, ?, ?)";

                    String sqlLicznikGlowny = "UPDATE Budynki SET licznik_glowny = ?, data_odczytu = ? WHERE id = ? ";


                        try (PreparedStatement psOdczyt = conn.prepareStatement(queryOdczyt);
                             PreparedStatement psLicznikGlowny = conn.prepareStatement(sqlLicznikGlowny)) {


                            while (rsZlecenia.next()) {
                                int idLokal = rsZlecenia.getInt("id_lokal");
                                String controllerName = rsZlecenia.getString("wykonawca");

                                psOdczyt.setInt(1, idLokal);
                                psOdczyt.setDouble(2, meter_readings());
                                psOdczyt.setString(3, date);
                                psOdczyt.setString(4, controllerName);
                                psOdczyt.executeUpdate();


                                double licznikGlownyValue = 2 * meter_readings();
                                int building = rsZlecenia.getInt("budynek");

                                psLicznikGlowny.setDouble(1, licznikGlownyValue);
                                psLicznikGlowny.setString(2, date);
                                psLicznikGlowny.setInt(3, building);
                                psLicznikGlowny.executeUpdate();
                            }
                        }

                        String queryDeleteZlecenie = "DELETE FROM Zlecenia WHERE id = ? ";
                        try (PreparedStatement deleteZlecenie = conn.prepareStatement(queryDeleteZlecenie);){
                            deleteZlecenie.setInt(1, task);
                            deleteZlecenie.executeUpdate();
                            resetAutoIncrement("Zlecenia");
                        }
                        //System.out.println("Odczyty dla danego zlecenia zostały zapisane.");
                        return true;
                    }
                }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }catch (NieprawidloweDane n) {
            System.out.println("Blad: " +n.getMessage());
            return false;
        }finally {
            disconnect();
        }
    }

}
