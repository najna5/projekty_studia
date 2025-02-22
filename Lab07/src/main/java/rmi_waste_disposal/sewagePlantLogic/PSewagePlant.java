package rmi_waste_disposal.sewagePlantLogic;

import interfaces.ISewagePlant;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;


public class PSewagePlant extends UnicastRemoteObject implements ISewagePlant {
    private Map<Integer,Integer> sewageMap = new HashMap<>();
    private String tailorHost;
    private int tailorPort;
    private String sewageName;


    public PSewagePlant(String tailorHost, int tailorPort, String name) throws RemoteException {
        super();
        this.tailorHost = tailorHost;
        this.tailorPort = tailorPort;
        this.sewageName = name;
    }

    public Map<Integer, Integer> getSewageMap() {
        return new HashMap<>(sewageMap); // Zwraca kopię mapy
    }

    public void bindToRegistry() throws RemoteException {
        try{
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            registry.rebind(sewageName, this);

            System.out.println("Oczyszczalnia została zarejestrowana w rejestrze RMI jako " + sewageName);
        }  catch (RemoteException e) {
            throw new RuntimeException("Błąd podczas rejestracji oczyszczalni w rejestrze.", e);
        }
    }


    @Override
    public void setPumpIn(int number, int volume) {
        if(sewageMap.containsKey(number)){
            int currentVolume = sewageMap.get(number);
            sewageMap.put(number, currentVolume + volume);
        } else{
            sewageMap.put(number, volume);
        }
    }

    @Override
    public int getStatus(int number) {
        if(sewageMap.containsKey(number)){
            return sewageMap.get(number);
        }else {
            return -1;
        }
    }

    @Override
    public void setPayoff(int number) {
        if(sewageMap.containsKey(number)){
            sewageMap.remove(number);
        }else{
            System.out.println("brak danych o danej cysternie");
        }
    }
}
