package src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static src.tcp.Server.userSockets;

public class ServerConnectThread extends Thread {
    private Socket socket;
    private String username;

    public ServerConnectThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("系统消息: 请输入用户名");
            username = reader.readLine();
            addUser(username, socket);
            writer.println("欢迎你进入聊天室！");
            while(true) {
                String userList = new String();
                userList += "系统消息: 在线用户列表 -\n";
                for (String username : userSockets.keySet()) {
                    userList += username + "\n";
                }
                writer.println(userList);

                writer.println("请输入你想聊天的用户(群聊输入：all):");
                String toUser = reader.readLine();
                if (toUser.equals("all")) {
                    writer.println("系统消息: 你已进入群聊");
                    String message;
                    while ((message = reader.readLine()) != null) {
                        if (message.equals("exit")) {
                            break;
                        }
                        message = "群聊消息 from " + username + ": " + message;
                        broadcast(message, username);
                    }
                } else {
                    writer.println("系统消息: 你已进入私聊" + toUser);
                    String message;
                    while ((message = reader.readLine()) != null) {
                    System.out.println(1);
                        if (message.equals("exit")) {
                            break;
                        }
                        if (Server.userSockets.containsKey(toUser)) {
                            message = "私聊消息 from " + username + ": " + message;
                            PrintWriter targetWriter = new PrintWriter(Server.userSockets.get(toUser).getOutputStream(), true);
                            targetWriter.println(message);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //连接异常时移除用户
            try {
                socket.close();
                removeUser(username);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

//    synchronized void sendUserList(String senderUsername) throws IOException {
//        StringBuilder userListMessage = new StringBuilder("系统消息: 在线用户列表 -\n");
//        for (String username : Server.userSockets.keySet()) {
//            userListMessage.append(username).append("\n");
//        }
//        PrintWriter writer = new PrintWriter(userSockets.get(senderUsername).getOutputStream(), true);
//        writer.println(userListMessage.toString());
//    }

    synchronized void broadcast(String message, String senderUsername) throws IOException {
        for (String username : userSockets.keySet()) {
            if (senderUsername.equals(username)) {
                continue;
            }
            PrintWriter writer = new PrintWriter(userSockets.get(username).getOutputStream(), true);
            // 检查不向发送消息的客户端发送消息
            writer.println(message);
        }
    }

    synchronized void addUser(String username, Socket socket) throws IOException {
        userSockets.put(username, socket);
        broadcast("系统消息: 用户 '" + username + "' 上线了！", username);
//        sendUserList();
    }

    synchronized void removeUser(String username) throws IOException {
        userSockets.remove(username);
        broadcast("系统消息: 用户 '" + username + "' 下线了！", username);
//        sendUserList(username);
    }
}
