package chat;

import java.io.IOException;

/**
 *                       !!! TCP multi-user chat !!!
 *  Protocol:
 *  '/new_chat (id)' - creates new chat with your (id) (from 8 to 20 symbols(letters, numbers, '_', '-'),
 *  '/exit_chat' - removing you from chat,
 *  '/connect_to (id)' - connect you to chat(with entered id) if it exists.
 *   launch ChatServerSocket and then other chats.
 *                    Enter smth in commandline :)
 **/

public class Chat1 {
    public static void main(String[] args) throws IOException, InterruptedException {
        ChatSocket chatSocket = new ChatSocket("lolan");
        chatSocket.chat();
    }
}
