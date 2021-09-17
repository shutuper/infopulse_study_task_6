package chat;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class ChatSocket {

    private static final String resourcesPath = "src/main/resources/chat.properties";
    private static final Properties properties;
    private final String username;
    private final AtomicReference<String> chatId = new AtomicReference<>("null");
    private final AtomicBoolean canContinue = new AtomicBoolean(true);

    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(resourcesPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ChatSocket(String username) {
        if (username.length() < 3) throw new IllegalArgumentException("Username length should be greater than 2!");
        this.username = username;
    }

    public void chat() throws IOException {
        Socket socket = new Socket(properties.getProperty("server.host"), Integer.parseInt(properties.getProperty("server.port")));
        Scanner scanner = new Scanner(System.in);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(() -> {
            String protocol;
            while (true) {
                try {
                    if ((protocol = in.readLine()) == null) continue;
                    parseProtocolPrinter(protocol);
                    canContinue.set(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            if (canContinue.get()) {
                out.println(protocolMaker(getChatId(), username, scanner.nextLine()));
                canContinue.set(false);
            }
        }
    }


    private synchronized String protocolMaker(String chatId, String username, String input) {
        return chatId + " | " + username + " | " + input;
    }

    private synchronized void parseProtocolPrinter(String protocol) {
        String[] parsedProtocol = protocol.split(" \\| ");
        String chatIdProtocol = parsedProtocol[0];
        String usernameProtocol = parsedProtocol[1];
        String message = parsedProtocol[2];
        String curDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy HH-mm"));
        setChatId(chatIdProtocol);
        System.out.println("----------------------------------");
        System.out.println(curDateTime + " - chat " + chatIdProtocol + ":");
        System.out.println(usernameProtocol + ": " + message);
    }

    public synchronized void setChatId(String chatId) {
        this.chatId.set(chatId);
    }

    public synchronized String getChatId() {
        return chatId.get();
    }
}
