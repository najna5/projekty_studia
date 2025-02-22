package waste_disposal.officeLogic;

import waste_disposal.Sender;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

interface IOffice {
    int register(String host, String port);
    int order(String host, String port);
    void setReadyToServe(int number);
}


public class POffice implements IOffice{
    private int port;
    private String sewageHost;
    private int sewagePlantPort;


    private Map<String,Boolean> tankers = new HashMap<>();

    private int nextId = 1;
    private Thread t;
    private ServerSocket ss = null;

public POffice(int port, String sewageHost, int sewagePlantPort) {
    this.sewagePlantPort = sewagePlantPort;
    this.sewageHost = sewageHost;
    this.port = port;
}


public void listen(){
    t = new Thread(new Runnable(){
        @Override
        public void run() {
            try{
                ss = new ServerSocket(port);
                while(true){
                    Socket cs = ss.accept(); //client socket
                        InputStream is = cs.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        String line = br.readLine();
                    int colonIndex;

                    if(line.startsWith("o:")) { //order(String houseHost, String housePort)
                        colonIndex = line.indexOf(':');
                        String[] parts = line.substring(colonIndex + 1).split(",");
                        String host = parts[0];
                        String port = parts[1];
                        int response = order(host, port);
                        OutputStream os = cs.getOutputStream();
                        PrintWriter pw = new PrintWriter(os,true);
                        pw.println(response);

                    }else if(line.startsWith("sr:")) { //setReadyToServe(int number)
                        colonIndex = line.indexOf(':');
                        String number = line.substring(colonIndex + 1);
                        setReadyToServe(Integer.parseInt(number));

                    }else if(line.startsWith("r:")) { //register(String tankerHost, String TankerPort)
                        colonIndex = line.indexOf(':');
                        String[] parts = line.substring(colonIndex + 1).split(",");
                        String host = parts[0];
                        String port = parts[1];
                        int id = register(host,port);
                        OutputStream os = cs.getOutputStream();
                        PrintWriter pw = new PrintWriter(os,true);
                        pw.println(id);

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


    public Map<String, Boolean> getTankers() {
        return tankers;
    }

    public void payOff(int number){
        String message = "spo:"+number;
        new Sender().send(message, sewageHost, sewagePlantPort);
    }

    public int status(int number){
        String message = "gs:"+number;
        return new Sender().sendAndReceive(message, sewageHost, sewagePlantPort);
    }

    @Override
    public int register(String tankerHost, String TankerPort) {
        int id = nextId++;
        tankers.put(id + ";"+tankerHost+";"+TankerPort, false);
        return id;
    }

    @Override
    public int order(String houseHost, String housePort) {
        for (Map.Entry<String, Boolean> entry : tankers.entrySet()) {
            if (entry.getValue()) {
                tankers.put(entry.getKey(), false);

                String[] parts = entry.getKey().split(";");
                String tankerId = parts[0];
                String tankerHost = parts[1];
                int tankerPort = Integer.parseInt(parts[2]);

                String message = "sj:"+ houseHost +";"+ housePort;
                new Sender().send(message, tankerHost, tankerPort);
                System.out.println("Zlecenie dla cysterny: ID=" + tankerId +" do domu: "+housePort);

                return 1;
            }
        }
        return 0;
    }

    @Override
    public void setReadyToServe(int number) {
        for (String key: tankers.keySet()){
            if(key.startsWith(number+";")){
                tankers.put(key,true);
                break;
            }
        }


    }
}
