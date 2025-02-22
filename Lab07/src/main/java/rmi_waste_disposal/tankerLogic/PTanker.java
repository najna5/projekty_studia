package rmi_waste_disposal.tankerLogic;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ISewagePlant;
import interfaces.ITanker;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PTanker extends UnicastRemoteObject implements ITanker {
    private int maxCapacity;
    private int capacity;
    private String name;
    private String tailorHost;
    private int tailorPort;

    private IOffice office;
    private ISewagePlant sewage;

    private int id;
    //to use in JFrame
    private String currentJob;



    public PTanker(String name, String tailorHost, int tailorPort, String sewageName, String officeName, int maxCapacity) throws RemoteException{
        super();
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.capacity = 0;
        this.tailorHost = tailorHost;
        this.tailorPort = tailorPort;
        try {
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            this.office = (IOffice) registry.lookup(officeName);
            Registry registry2 = LocateRegistry.getRegistry(tailorHost, tailorPort);
            this.sewage = (ISewagePlant) registry2.lookup(sewageName);
        } catch (Exception e) {
            e.printStackTrace();
        }


        this.currentJob="";
    }

    public void registerInOffice() throws RemoteException {
        int receivedId = office.register(this, name);

        if(receivedId != -1){
            id = receivedId;
        }else{
            System.out.println("błąd w przypisaniu ID");
        }
    }

    public void driveToSewagePlant() throws RemoteException {
        System.out.println(id + ": pojechała do oczyszczalni");
        try {
            Thread.sleep(5000); // transport duration (5 s)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sewage.setPumpIn(id, capacity);

        capacity = 0;
        this.currentJob = "";

        office.setReadyToServe(id);
        System.out.println("Zadanie zakończone, cysterna ponownie dostępna");

    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public String getCurrentJob() {
        return currentJob;
    }

    public int getId() {
        return id;
    }


    public void setActive() throws RemoteException {
        office.setReadyToServe(id);
    }

    public void bindToRegistry() throws RemoteException {
        try{
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            registry.rebind(name, this);

            System.out.println("Cysterna została zarejestrowana w rejestrze RMI jako " + name);
        }  catch (RemoteException e) {
            throw new RuntimeException("Błąd podczas rejestracji cysterny w rejestrze.", e);
        }
    }

    @Override
    public void setJob(IHouse house) throws RemoteException {
        System.out.println("Cysterna otrzymała zlecenie z biura do domu: " + getData(house));
        capacity = house.getPumpOut(maxCapacity);

        this.currentJob = getData(house);

        System.out.println("Odebrano ścieki z domu: " + capacity + " jednostek");

        driveToSewagePlant();
    }

    private String getData(IHouse house) {
        String info = house.toString();
        String regex = "endpoint:\\[([0-9.]+:\\d+)\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(info);

        // Sprawdzamy, czy wzorzec został znaleziony
        if (matcher.find()) {
            return matcher.group(1);  // Zwracamy pierwszy (i jedyny) wynik: host:port
        }
        return null;  // Jeśli nie uda się znaleźć wzorca, zwróć null
    }
}
