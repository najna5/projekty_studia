package waste_disposal.sewagePlantLogic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

interface ISewagePlant{
    void setPumpIn(int number, int volume);
    int getStatus(int number);
    void setPayoff(int number);
}

public class PSewagePlant implements ISewagePlant{
    private int port;
    private Map<Integer,Integer> sewageMap = new HashMap<>();

    private Thread t;
    private ServerSocket ss = null;

    public PSewagePlant(int port){
        this.port = port;
    }

    public void listen(){
        t = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    ss = new ServerSocket(port);
                    System.out.println("oczyszczalnia nasłuchuje na porcie: " + port);
                    while(true){
                        Socket cs = ss.accept();
                            InputStream is = cs.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String line = br.readLine();
                        int colonIndex;

                        if(line.startsWith("spi:")) { //setPumpIn(int number, int volume)
                            System.out.println("zadanie: opróżnianie cysterny");
                            colonIndex = line.indexOf(':');
                            String[] parts = line.substring(colonIndex + 1).split(";");
                            int number = Integer.parseInt(parts[0]);
                            int volume = Integer.parseInt(parts[1]);
                            setPumpIn(number,volume);
                            System.out.println(sewageMap);

                        }else if(line.startsWith("spo:")) { //setPayoff(int number)
                            colonIndex = line.indexOf(':');
                            String number = line.substring(colonIndex + 1);
                            setPayoff(Integer.parseInt(number));
                            System.out.println("Usunięto cysternę"+ number +"z bazy");

                        }else if(line.startsWith("gs:")) { //getStatus(int number)
                            colonIndex = line.indexOf(':');
                            String number = line.substring(colonIndex + 1);
                            int response = getStatus(Integer.parseInt(number));
                            OutputStream os = cs.getOutputStream();
                            PrintWriter pw = new PrintWriter(os,true);
                            pw.println(response);

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

    public Map<Integer, Integer> getSewageMap() {
        return new HashMap<>(sewageMap); // Zwraca kopię mapy
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
