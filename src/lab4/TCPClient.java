package lab4;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.*;

public class TCPClient {
    private static final Logger logger = Logger.getLogger(TCPClient.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Настройка журналирования
        try {
            System.out.println("Введите путь к файлу журнала клиента: ");
            String logFile = scanner.nextLine();
            FileHandler fileHandler = new FileHandler("src/lab4/" + logFile, true);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            System.err.println("Ошибка настройки журнала: " + e.getMessage());
            return;
        }

        // Подключение к серверу
        System.out.print("Введите IP-адрес сервера: ");
        String serverAddress = scanner.nextLine();
        System.out.print("Введите порт сервера: ");
        int port = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера

        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                System.out.print("\nДоступные команды:\n" +
                        "1. READ\n" +
                        "2. WRITE\n" +
                        "3. DIMENSION\n" +
                        "4. SET_DEFAULT\n" +
                        "5. EXIT\n" +
                        "Введите команду: ");
                String command = scanner.nextLine().toUpperCase();

                if (command.equals("EXIT")) {
                    logger.info("Клиент завершил работу");
                    break;
                }

                switch (command) {
                    case "READ" -> handleRead(scanner, out, in);
                    case "WRITE" -> handleWrite(scanner, out, in);
                    case "DIMENSION" -> handleDimension(scanner, out, in);
                    case "SET_DEFAULT" -> handleSetDefault(scanner, out, in);
                    default -> System.out.println("Неизвестная команда");
                }
            }
        } catch (IOException e) {
            logger.severe("Ошибка подключения: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void handleRead(Scanner scanner, PrintWriter out, BufferedReader in) {
        try {
            System.out.print("Тип массива (1-int, 2-double, 3-string): ");
            int arrayType = scanner.nextInt();
            System.out.print("Строка: ");
            int row = scanner.nextInt();
            System.out.print("Столбец: ");
            int col = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера

            out.println("READ");
            out.println(String.format("%d %d %d", arrayType, row, col));
            logger.info(String.format("READ отправлено: тип=%d, строка=%d, столбец=%d", arrayType, row, col));

            String response = in.readLine();
            System.out.println("Ответ сервера: " + response);
            logger.info("Ответ на READ: " + response);
        } catch (Exception e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }

    private static void handleWrite(Scanner scanner, PrintWriter out, BufferedReader in) {
        try {
            System.out.print("Тип массива (1-int, 2-double, 3-string): ");
            int arrayType = scanner.nextInt();
            System.out.print("Строка: ");
            int row = scanner.nextInt();
            System.out.print("Столбец: ");
            int col = scanner.nextInt();
            System.out.print("Значение: ");
            String value = scanner.next();
            scanner.nextLine(); // Очистка буфера

            out.println("WRITE");
            out.println(String.format("%d %d %d %s", arrayType, row, col, value));
            logger.info(String.format("WRITE отправлено: тип=%d, строка=%d, столбец=%d, значение=%s",
                    arrayType, row, col, value));

            String response = in.readLine();
            System.out.println("Ответ сервера: " + response);
            logger.info("Ответ на WRITE: " + response);
        } catch (Exception e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }

    private static void handleDimension(Scanner scanner, PrintWriter out, BufferedReader in) {
        try {
            System.out.print("Тип массива (1-int, 2-double, 3-string): ");
            int arrayType = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера

            out.println("DIMENSION");
            out.println(arrayType);
            logger.info("DIMENSION отправлено: тип=" + arrayType);

            String response = in.readLine();
            System.out.println("Ответ сервера: " + response);
            logger.info("Ответ на DIMENSION: " + response);
        } catch (Exception e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }

    private static void handleSetDefault(Scanner scanner, PrintWriter out, BufferedReader in) {
        try {
            System.out.print("Количество ячеек: ");
            int count = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера

            StringBuilder data = new StringBuilder(String.valueOf(count));
            for (int i = 0; i < count; i++) {
                System.out.printf("Ячейка %d: введите тип, строку и столбец (пример: 1 0 0): ", i + 1);
                String[] parts = scanner.nextLine().split(" ");
                if (parts.length != 3) {
                    System.out.println("Неверный формат, пропуск...");
                    continue;
                }
                data.append(" ").append(parts[0]).append(" ").append(parts[1]).append(" ").append(parts[2]);
            }

            out.println("SET_DEFAULT");
            out.println(data.toString());
            logger.info("SET_DEFAULT отправлено: " + data);

            String response = in.readLine();
            System.out.println("Ответ сервера: " + response);
            logger.info("Ответ на SET_DEFAULT: " + response);
        } catch (Exception e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }
}