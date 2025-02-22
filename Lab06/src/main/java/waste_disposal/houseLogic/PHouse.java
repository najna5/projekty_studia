package waste_disposal.houseLogic;

import waste_disposal.Sender;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

interface IHouse{
    int getPumpOut(int max);
}

public class PHouse implements IHouse {
    private int port;

    private Thread t;
    private ServerSocket ss = null;

    private int capacity;
    private int currentLevel;
    private int officePort;
    private String officeHost;


    public PHouse(int port, int capacity) {
        this.port = port;
        this.capacity = capacity;
        this.currentLevel = 0;
    }


    public void setOfficeHost(String officeHost) {
        this.officeHost = officeHost;
    }


    public void setOfficePort(int officePort) {
        this.officePort = officePort;
    }

    public void listen(){
        t = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    ss = new ServerSocket(port);
                    System.out.println("Dom nasłuchuje na porcie: " + port);
                    while(true){
                        Socket cs = ss.accept();
                            InputStream is = cs.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String line = br.readLine();

                        if(line.startsWith("gp:")) {
                            System.out.println("dom otrzymał wiadomosć " + line);
                            int colonIndex = line.indexOf(':');
                            String number = line.substring(colonIndex + 1);
                            int pumped = getPumpOut(Integer.parseInt(number));
                            OutputStream os = cs.getOutputStream();
                            PrintWriter pw = new PrintWriter(os,true);
                            pw.println(pumped);

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


    public void fill(int amount) throws UnknownHostException {
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

    private void sendRequest() throws UnknownHostException {
        String myHost = InetAddress.getLocalHost().getHostAddress();
        String message ="o:"+myHost+","+port;
        int response = new Sender().sendAndReceive(message,officeHost, officePort);
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

    @Override
    public int getPumpOut(int tankerMax){
        System.out.println("podjechano do domu: "+ port);
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

}
