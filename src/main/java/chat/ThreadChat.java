package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class ThreadChat extends Thread {

    private static final String STR_HELP = "/help";
    private static final String STR_EXIT_FROM_CHAT = "/exit_chat";

    private static final String STR_PROTOCOL_PATTERN = "[\\d\\w_-]{3,20} \\| [\\d\\w_]{3,12} \\| .+";
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile(STR_PROTOCOL_PATTERN);

    private static final String STR_NEW_CHAT_PATTERN = "/new_chat [\\d\\w_-]{8,20}";
    private static final Pattern NEW_CHAT_PATTERN = Pattern.compile(STR_NEW_CHAT_PATTERN);

    private static final String STR_CONNECT_CHAT_PATTERN = "/connect_to [\\d\\w_-]{8,20}";
    private static final Pattern CONNECT_CHAT_PATTERN = Pattern.compile(STR_CONNECT_CHAT_PATTERN);

    private static volatile Map<String, Set<Socket>> chatMap = new ConcurrentHashMap<>();
    private volatile Socket clientSocket;

    public ThreadChat(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        PrintWriter out;
        BufferedReader in;
        String protocol, chatId, message, username;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true) {
                if (!PROTOCOL_PATTERN.matcher(protocol = in.readLine()).matches() || protocol.equals("null"))
                    continue;
                System.out.println(protocol);
                String[] parsedProtocol = protocol.split(" \\| ");
                chatId = parsedProtocol[0];
                username = parsedProtocol[1];
                message = parsedProtocol[2];
                if (chatId.equals("null")) {
                    if (commonCommands(clientSocket, out, chatId, message, username))
                        out.println(protocolMaker(chatId, username) + "Connect to any chat or enter '/help' - to get commands list!");
                } else {
                    if (commonCommands(clientSocket, out, chatId, message, username))
                        chattingOrCommands(out, chatId, message, username);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized static void chattingOrCommands(PrintWriter out, String chatId, String message, String username) throws IOException {
        if (chatMap.containsKey(chatId)) {
            PrintWriter writer;
            ReentrantLock locker = new ReentrantLock();
            for (Socket chatMember : chatMap.get(chatId)) {
                locker.lock();
                writer = new PrintWriter(chatMember.getOutputStream(), true);
                writer.println(protocolMaker(chatId, username) + message);
                locker.unlock();
            }
        } else
            out.println(protocolMaker(chatId, username) + "Your chat id isn't valid!");
    }

    private synchronized static boolean commonCommands(Socket clientSocket, PrintWriter out, String chatId, String message, String username) {
        if (NEW_CHAT_PATTERN.matcher(message).matches()) {
            createNewChat(clientSocket, out, message, chatId, username);
        } else if (CONNECT_CHAT_PATTERN.matcher(message).matches()) {
            connectToChat(clientSocket, out, chatId, message, username);
        } else if (message.equals(STR_HELP)) {
            showCommands(out, chatId, username);
        } else if (message.equals(STR_EXIT_FROM_CHAT)) {
            leaveChat(clientSocket, out, chatId, username);
        } else return true;
        return false;
    }

    private synchronized static void leaveChat(Socket clientSocket, PrintWriter out, String chatId, String username) {
        if (chatMap.containsKey(chatId)) {
            Set<Socket> chat = chatMap.get(chatId);
            if (chat.size() <= 1) {
                chatMap.remove(chatId);
            } else {
                chat.remove(clientSocket);
            }
            out.println(protocolMaker("null", username) + "You left chat!");
        }
    }

    private synchronized static void showCommands(PrintWriter out, String chatId, String username) {
        out.println(protocolMaker(chatId, username) + "'/new_chat (id)' - creates new chat with your (id) (from 8 to 20 symbols(letters, numbers, '_', '-')," +
                " '/exit_chat' - removing you from chat," +
                " '/connect_to (id)' - connect you to chat with entered id if exists.");
    }

    private synchronized static void connectToChat(Socket clientSocket, PrintWriter out, String chatId, String message, String username) {
        String newChatId = message.split(" ")[1];
        if (chatMap.containsKey(newChatId)) {
            chatMap.get(newChatId).add(clientSocket);
            out.println(protocolMaker(newChatId, username) + "You are successfully connected to chat: " + newChatId);
        } else {
            out.println(protocolMaker(chatId, username) + "Chat with id: " + newChatId + " doesn't exist!");
        }
    }

    private synchronized static void createNewChat(Socket clientSocket, PrintWriter out, String message, String chatId, String username) {
        String newChatId = message.split(" ")[1];
        leaveChat(clientSocket, out, chatId, username);
        if (chatMap.containsKey(newChatId)) {
            out.println(protocolMaker(newChatId, username) + "Chat with id: " + newChatId + " already exists!");
        } else {
            Set<Socket> chat = new HashSet<>();
            chat.add(clientSocket);
            chatMap.put(newChatId, chat);
            out.println(protocolMaker(newChatId, username) + "Chat with id: " + newChatId + " successfully created!");
        }
    }

    private synchronized static String protocolMaker(String chatId, String username) {
        return chatId + " | " + username + " | ";
    }
}
