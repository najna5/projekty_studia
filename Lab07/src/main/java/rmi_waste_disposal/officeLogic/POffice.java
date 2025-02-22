package rmi_waste_disposal.officeLogic;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ISewagePlant;
import interfaces.ITanker;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class POffice extends UnicastRemoteObject implements IOffice {
    private ISewagePlant sewagePlant;
    private Map<Integer, TankerStatus> tankers = new HashMap<>();
    private int nextId = 1;
    private String tailorHost;
    private int tailorPort;

public POffice(String tailorHost, int tailorPort, String sewageName) throws RemoteException {
    super();
    this.tailorHost = tailorHost;
    this.tailorPort = tailorPort;
    try {
        Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
        this.sewagePlant = (ISewagePlant) registry.lookup(sewageName);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static class TankerStatus{
        private final ITanker tanker;
        private final String name;
        private boolean isReady;

        public TankerStatus(ITanker tanker, String name, boolean isReady) {
            this.tanker = tanker;
            this.name = name;
            this.isReady = isReady;
        }

        public String getName() {
            return name;
        }

        public boolean isReady() {
            return isReady;
        }
    }

    public Map<Integer, TankerStatus> getTankers() {
        return tankers;
    }


    public void payOff(int number) throws RemoteException {
        sewagePlant.setPayoff(number);
    }

    public int status(int number) throws RemoteException {
        return sewagePlant.getStatus(number);
    }

    public void bindToRegistry(String officeName) throws RemoteException {
        try{
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            registry.rebind(officeName, this);

            System.out.println("Biuro zostało zarejestrowane w rejestrze RMI jako: " + officeName);
        }  catch (RemoteException e) {
            throw new RuntimeException("Błąd podczas rejestracji biura w rejestrze.", e);
        }
    }


    @Override
    public int register(ITanker tanker, String name) throws RemoteException {
        int id = nextId++;
        tankers.put(id, new TankerStatus(tanker, name, false));
        return id;
    }


    @Override
    public int order(IHouse house, String name) throws RemoteException {
        for (Map.Entry<Integer, TankerStatus> entry : tankers.entrySet()) {
            TankerStatus status = entry.getValue();
            if (status.isReady) {
                try{
                    status.isReady = false;
                    status.tanker.setJob(house);
                    System.out.println("Zlecenie dla cysterny: ID=" + entry.getKey() +" do domu: "+ getData(house));
                    return 1;
                } catch (RemoteException e) {
                    System.err.println("Błąd w komunikacji z cysterną: ID=" + entry.getKey());
                }
            }
        }
        return 0; //żadna cysterna nie była dostępna
    }

    @Override
    public void setReadyToServe(int number) {
    TankerStatus status = tankers.get(number);
        if (status != null) {
            status.isReady = true; // Ustawiamy cysternę jako gotową
            System.out.println("Cysterna gotowa do pracy: ID=" + number);
        } else {
            System.err.println("Nie znaleziono cysterny o ID: " + number);
        }
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
