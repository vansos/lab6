package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;

class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket socket;
    private String nickname;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Введите свое имя/никнейм:");
            nickname = in.readLine();

            if (nickname == null || nickname.isBlank()) {
                socket.close();
                return;
            }

            ChatServer.clients.put(nickname, this);
            logger.info("{} подключен", nickname);

            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }

        } catch (IOException e) {
            logger.warn("Связь с {} потеряна.", nickname);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Ошибка закрытия сокета для {}", nickname);
            }
            ChatServer.clients.remove(nickname);
            logger.info("{} отключен", nickname);
        }
    }

    private void handleMessage(String message) {
        if (message.equals("/users")) {
            send("Подключенные пользователи: " + getConnectedUsers());
            return;
        }

        if (message.startsWith("@")) {
            int colonIndex = message.indexOf(":");
            if (colonIndex != -1) {
                String recipient = message.substring(1, colonIndex).trim();
                String text = message.substring(colonIndex + 1).trim();

                ClientHandler target = ChatServer.clients.get(recipient);
                if (target != null) {
                    target.send("[Приватно] " + nickname + ": " + text);
                    logger.info("Частное сообщение {} к {}: {}", nickname, recipient, text);
                } else {
                    send("Пользователь не найден: " + recipient);
                }
            }
        } else {
            for (Map.Entry<String, ClientHandler> entry : ChatServer.clients.entrySet()) {
                if (!entry.getKey().equals(nickname)) {
                    entry.getValue().send("[Трансляция] " + nickname + ": " + message);
                }
            }
            logger.info("Трансляция для {}: {}", nickname, message);
        }
    }

    public void send(String message) {
        out.println(message);
    }

    public static String getConnectedUsers() {
        return String.join(", ", ChatServer.clients.keySet());
    }
}