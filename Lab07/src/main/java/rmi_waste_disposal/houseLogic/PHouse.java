package rmi_waste_disposal.houseLogic;

import interfaces.IHouse;
import interfaces.IOffice;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PHouse extends UnicastRemoteObject implements IHouse {
    private int capacity;
    private int currentLevel;
    private IOffice office;
    private String name;
    private String tailorHost;
    private int tailorPort;


    public PHouse(String name, int capacity, String tailorHost, int tailorPort, String officeName) throws RemoteException {
        this.name = name;
        this.capacity = capacity;
        this.currentLevel = 0;
        this.tailorHost = tailorHost;
        this.tailorPort = tailorPort;
        try {
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            this.office = (IOffice) registry.lookup(officeName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Błąd połączenia z biurem");
        }
    }


    public void fill(int amount) throws RemoteException {
        if (currentLevel + amount > capacity) {
            currentLevel = capacity;
        }else {
            currentLevel += amount;
        }
        if(currentLevel >= capacity * 0.95) {
            System.out.println("poziom krytyczny");
            sendRequest();
        }
    }

    private void sendRequest() throws RemoteException {
        int response = office.order(this, name);

        if(response == 1){
            System.out.println("pozytywnie rozpatrzono wniosek");
        }else{
            System.out.println("nie przyjęto wniosku");
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCapacity() {
        return capacity;
    }


    public void bindToRegistry() throws RemoteException {
        try{
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            registry.rebind(name, this);

            System.out.println("dom zostały zarejestrowany w rejestrze RMI jako " + name);
        }  catch (RemoteException e) {
            throw new RuntimeException("Błąd podczas rejestracji domu w rejestrze.", e);
        }
    }

    @Override
    public int getPumpOut(int tankerMax){
        System.out.println("podjechano do domu: "+ getData(this));
        int pumped = 0;

        if(currentLevel >= tankerMax){
            pumped = tankerMax;
            currentLevel -= tankerMax;
        }else{
            pumped = currentLevel;
            currentLevel = 0;
        }
        return pumped;
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
