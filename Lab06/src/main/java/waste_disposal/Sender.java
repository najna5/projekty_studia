package waste_disposal;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender {
    //method for only sending message/ no response
    public void send(String message, String host, int port){
        Socket socket;
        try{
            socket = new Socket(host,port);
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out,false);
            pw.println(message);
            pw.flush();
            pw.close();
            socket.close();
        } catch (UnknownHostException e) {
            System.out.println("błędny host");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("inny błąd");
            e.printStackTrace();
        }
    }

    //method used when waiting for a response
    public int sendAndReceive(String message, String host, int port){
        try (Socket socket = new Socket(host, port)) {
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out, false);
            pw.println(message);
            pw.flush();

            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String response = br.readLine();

            return Integer.parseInt(response);

        } catch (UnknownHostException e) {
        System.out.println("Błędny host");
        e.printStackTrace();
    } catch (IOException e) {
        System.out.println("Inny błąd");
        e.printStackTrace();
    } catch (NumberFormatException e) {
        System.out.println("Otrzymana odpowiedź nie jest liczbą");
        e.printStackTrace();
    }
        return -1;

    }

}
