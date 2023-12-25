package src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReadThread extends Thread{
    private Socket socket;
    public ClientReadThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                try {
                    String msg = reader.readLine();
                    System.out.println(msg);
                } catch (IOException e) {
                    reader.close();
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
