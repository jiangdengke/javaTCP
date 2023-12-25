package src.tcp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(), 8000);
        new ClientReadThread(socket).start();
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.next();
            if (msg.equals("退出")) {
                writer.close();
                socket.close();
                break;
            }
            writer.println(msg);
        }
    }
}
