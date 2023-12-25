package src.tcp;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class Server {
    public  static Map<String, Socket> userSockets = new LinkedHashMap<>();//存在线用户及socket

    public static void main(String[] args) throws IOException {
        System.out.println("服务端启动");
        ServerSocket serverSocket = new ServerSocket(8000);
        while (true){
            Socket socket = serverSocket.accept();
            new ServerConnectThread(socket).start();
        }
    }
}
