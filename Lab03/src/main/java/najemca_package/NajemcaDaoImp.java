package najemca_package;

import myExceptions.NieprawidloweDane;
import myExceptions.NieprawidlowyLogin;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NajemcaDaoImp implements NajemcaDao {
    private Connection conn = null;
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());

        }
    }

    public NajemcaDaoImp() {}

    private void connect() throws SQLException {
        if (conn != null) {
            return;
        }
        String url = "jdbc:sqlite:data.sqlite";
        conn = DriverManager.getConnection(url);
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
            Scanner scanner = new Scanner(System.in);

            String showQuery = "SELECT nazwa FROM Najemcy";
            try (PreparedStatement statement = conn.prepareStatement(showQuery);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("Dostepni uzytkownicy:");
                while (resultSet.next()) {
                    System.out.println("- " + resultSet.getString("nazwa"));
                }
            }

            System.out.print("Podaj login: ");
            String userName = scanner.nextLine();


            String findQuery = "SELECT id FROM Najemcy WHERE nazwa = ?";
            try (PreparedStatement findStatement = conn.prepareStatement(findQuery)) {
                findStatement.setString(1, userName);
                try (ResultSet findResult = findStatement.executeQuery()) {
                    if (findResult.next()) {
                        userId = findResult.getInt("id");
                    } else {
                        throw new NieprawidlowyLogin("Podany uzytkownik nie istnieje");
                        //System.out.println("Podany uzytkownik nie istnieje");
                    }
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch(NieprawidlowyLogin n) {
            System.out.println("Blad: " + n.getMessage());
        }
        return userId;
    }

    @Override
    public List<String> oczekujaceRachunki(int userId){
        List<String> rachunki = new ArrayList<>();
        try{
            connect();

            String query = "SELECT L.numer_lokalu, B.adres, R.koszt, R.data_dodania FROM Rozliczenia R " +
                    "JOIN Lokale L ON R.id_lokal = L.id " +
                    "JOIN Budynki B ON B.id = L.id_budynek " +
                    "JOIN Najemcy N on N.id = L.id_najemca " +
                    "WHERE N.id = ? ";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);

                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (!resultSet.isBeforeFirst()) {
                        //System.out.println("nie ma zaleglych oplat");
                        return rachunki;
                    }

                    System.out.println("Rachunki do oplacenia: ");
                    while (resultSet.next()) {
                        int nrLokalu = resultSet.getInt("numer_lokalu");
                        String adress = resultSet.getString("adres");
                        double cost = resultSet.getDouble("koszt");
                        cost = Math.round(cost * 100.0) / 100.0;
                        String added_date = resultSet.getString("data_dodania");

                        rachunki.add("ul." + adress + " m." + nrLokalu + ",     DO ZAPLATY: " +
                                cost  + "zl  -  dodano: " + added_date);
                    }
                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally {
            disconnect();
        }
        return rachunki;
    }

    @Override
    public int zaplac(int userId){
        try{
            connect();
            Scanner scanner = new Scanner(System.in);

            String optionsQuery = "SELECT R.id AS kod, L.numer_lokalu, B.adres " +
                    "FROM Rozliczenia R " +
                    "JOIN Lokale L ON L.id = R.id_lokal " +
                    "JOIN Budynki B ON B.id = L.id_budynek " +
                    "JOIN Najemcy N ON N.id = L.id_najemca " +
                    "WHERE N.id = ? ";

            try(PreparedStatement stmt = conn.prepareStatement(optionsQuery)) {
                stmt.setInt(1, userId);

                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (!resultSet.isBeforeFirst()) {
                        //System.out.println("Nie ma zaleglych platnosci.");
                        return 0;
                    }

                    System.out.println("Do zaplaty");
                    while (resultSet.next()) {
                        int kod = resultSet.getInt("kod");
                        int nrLokalu = resultSet.getInt("numer_lokalu");
                        String adres = resultSet.getString("adres");

                        System.out.println("nr zlecenia: *" + kod + "*, adres: ul." + adres + " m." + nrLokalu);
                    }

                    System.out.println("Podaj nr zlecenia do oplacenia: ");
                    int choice = scanner.nextInt();

                    String selectOrderQuery = "SELECT R.koszt, B.adres, L.numer_lokalu " +
                            "FROM Rozliczenia R " +
                            "JOIN Lokale L ON L.id = R.id_lokal " +
                            "JOIN Budynki B ON B.id = L.id_budynek " +
                            "WHERE R.id = ?";

                    try (PreparedStatement selectStmt = conn.prepareStatement(selectOrderQuery)) {
                        selectStmt.setInt(1, choice);

                        try (ResultSet orderResult = selectStmt.executeQuery()) {
                            if (!orderResult.next()) {
                                throw new NieprawidloweDane("Nie znaleziono wybranego zlecenia.");
                            }

                            double cost = orderResult.getDouble("koszt");
                            cost = Math.round(cost * 100.0) / 100.0;
                            String adress = orderResult.getString("adres");
                            int nrLokalu = orderResult.getInt("numer_lokalu");


                            String insertQuery = "INSERT INTO historia_platnosci (data_platnosci, najemca_id, adres_budynku, " +
                                    "nr_lokalu, kwota) VALUES (?, ?, ?, ?, ?)";

                            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                resetAutoIncrement("historia_platnosci");
                                insertStmt.setString(1, LocalDate.now().toString());
                                insertStmt.setInt(2, userId);
                                insertStmt.setString(3, adress);
                                insertStmt.setInt(4, nrLokalu);
                                insertStmt.setDouble(5, cost);
                                insertStmt.executeUpdate();
                            }

                            String deleteQuery = "DELETE FROM Rozliczenia WHERE id = ?";
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                                deleteStmt.setInt(1, choice);
                                deleteStmt.executeUpdate();
                            }
                            //System.out.println("Zaplacono!");
                            return 1;

                        }
                    }
                }
            }

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }catch (NieprawidloweDane n){
            System.out.println("Blad: " + n.getMessage());
            return -1;
        }finally{
            disconnect();
        }
    }

    @Override
    public List<String> historiaPlatnosci(int userId){
        List<String> historia = new ArrayList<>();
        try{
            connect();

            String query = "SELECT data_platnosci, adres_budynku, nr_lokalu, kwota " +
                    "FROM historia_platnosci WHERE najemca_id = ? ";

            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet resultSet = stmt.executeQuery()) {

                    if (!resultSet.isBeforeFirst()) {
                        //System.out.println("Nie mamy twojej historii platnosci w bazie");
                        return historia;
                    }

                    System.out.println("Historia platnosci =================================");
                    while (resultSet.next()) {
                        String date = resultSet.getString("data_platnosci");
                        String adress = resultSet.getString("adres_budynku");
                        int nrLokalu = resultSet.getInt("nr_lokalu");
                        double sum = resultSet.getDouble("kwota");

                        historia.add("- data: " + date + ", adres: ul." + adress + " m." + nrLokalu + ", kwota: "
                                + sum + "zl.");
                    }
                }
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally {
            disconnect();
        }
    return historia;
    }

}
