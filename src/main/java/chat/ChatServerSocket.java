package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServerSocket {

    private static ServerSocket server;

    public static void main(String[] args) throws IOException {
        server = new ServerSocket(8097);
        chat();
    }

    private static void chat() throws IOException {
        while (true) {
            Socket clientSocket = server.accept();
            newThreadChat(clientSocket);
        }
    }

    private static void newThreadChat(Socket clientSocket) {
        ThreadChat threadChat = new ThreadChat(clientSocket);
        threadChat.start();
    }


}
