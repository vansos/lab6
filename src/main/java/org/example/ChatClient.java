package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(in.readLine());
            String nickname = scanner.nextLine();
            out.println(nickname);

            new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Соединение закрыто.");
                }
            }).start();

            while (true) {
                System.out.println("Выберите тип сообщения: 1 - личное, 2 - широковещательное");
                String type = scanner.nextLine();
                if (type.equals("1")) {
                    System.out.println("Введите псевдоним получателя (используй /users, чтобы просмотреть список)::");
                    String recipient = scanner.nextLine();
                    if (recipient.equals("/users")) {
                        out.println("/users");
                        continue;
                    }
                    System.out.println("Введите свое сообщение:");
                    String msg = scanner.nextLine();
                    out.println("@" + recipient + ":" + msg);
                } else if (type.equals("2")) {
                    System.out.println("Введите свое сообщение:");
                    String msg = scanner.nextLine();
                    out.println(msg);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
        }
    }
}