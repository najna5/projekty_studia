package waste_disposal.tankerLogic;

import waste_disposal.Sender;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

interface ITanker{
    void setJob(String host, String port);
}


public class PTanker implements ITanker{
    private int port;
    private String officeHost;
    private int officePort;
    private int sewagePort;
    private String sewageHost;
    private int maxCapacity;
    private int capacity;

    private int id;
    //to use in JFrame
    private String currentJobHost;
    private int currentJobPort;

    private Thread t;
    private ServerSocket ss = null;

    public PTanker(int port, String officeHost, int officePort, String sewageHost, int sewagePort, int maxCapacity) {
        this.port = port;
        this.officeHost = officeHost;
        this.officePort = officePort;
        this.sewageHost = sewageHost;
        this.sewagePort = sewagePort;
        this.maxCapacity = maxCapacity;
        this.capacity = 0;

        this.currentJobHost="";
        this.currentJobPort = 0;

    }

    public void listen(){
        t = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    ss = new ServerSocket(port);
                    while(true){
                        Socket cs = ss.accept();
                            InputStream is = cs.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String line = br.readLine();

                        if(line.startsWith("sj:")) { //setJob(String host, String port)
                            int colonIndex = line.indexOf(':');
                            String[] parts = line.substring(colonIndex + 1).split(";");
                            String host = parts[0];
                            String port = parts[1];
                            setJob(host, port);

                        }else{
                            System.out.println("niepoprawny format wiadomości: " +line);
                        }
                        cs.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (ss != null && !ss.isClosed()) {
                            ss.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Port został zwolniony.");
                }
            }
        });
        t.start();
    }

    public void registerInOffice() throws UnknownHostException {
        String myHost = InetAddress.getLocalHost().getHostAddress();
        String message ="r:"+ myHost+"," + port;
        int receivedId = new Sender().sendAndReceive(message, officeHost, officePort);

        if(receivedId != -1){
            id = receivedId;
        }else{
            System.out.println("błąd w przypisaniu ID");
        }
    }

    public void driveToSewagePlant(){
        String message ="spi:"+ id +";"+ capacity;
        System.out.println("wysłano polecenie: "+ message +" do oczyszczalni:"+sewageHost+";"+sewagePort);
        try {
            Thread.sleep(5000); // transport duration (5 s)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Sender().send(message, sewageHost, sewagePort);

        capacity = 0;
        this.currentJobHost = "";
        this.currentJobPort = 0;

        String done = "sr:" + id;
        new Sender().send(done, officeHost, officePort);
        System.out.println("Zadanie zakończone, cysterna ponownie dostępna");

    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public String getCurrentJob() {
        return currentJobHost + ":" + currentJobPort;
    }

    public int getId() {
        return id;
    }


    public void setActive() {
        String message = "sr:" + id;
        new Sender().send(message, officeHost, officePort);
    }

    @Override
    public void setJob(String host, String port) {
        System.out.println("Cysterna otrzymała zlecenie z biura do domu: " + host + ":" + port);
        String message = "gp:" + maxCapacity;
        System.out.println("Wysłanie zlecenia do domu: " + message + " na adres: " + host + ":" + port);
        capacity = new Sender().sendAndReceive(message,host,Integer.parseInt(port));
        System.out.println("odp z domu = " + capacity);
        this.currentJobHost = host;
        this.currentJobPort = Integer.parseInt(port);
        System.out.println("Odebrano ścieki z domu: " + capacity + " jednostek");

        driveToSewagePlant();
    }
}
