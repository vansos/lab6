package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ChatServer {
    private static final int PORT = 12345;
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
    public static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();


    public static void main(String[] args) {
        logger.info("Консольный чат запущен с портом {}", PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            logger.error("Ошибка сервера: ", e);
        }
    }
}
