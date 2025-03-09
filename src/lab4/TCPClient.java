package lab4;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.*;

public class TCPClient {
    private static final Logger logger = Logger.getLogger(TCPClient.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            String file = scanner.nextLine();
            FileHandler fileHandler = new FileHandler("src/lab4/"+file, true);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            System.err.println("Ошибка при настройке журналирования: " + e.getMessage());
        }

        System.out.print("Введите IP-адрес сервера: ");
        String serverAddress = scanner.nextLine();

        System.out.print("Введите порт сервера: ");
        int port = scanner.nextInt();
        scanner.nextLine();

        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String command;
            while (true) {
                System.out.print("Введите команду (READ, WRITE, DIMENSION, SET_DEFAULT или EXIT для выхода): ");
                command = scanner.nextLine().trim().toUpperCase();

                if (command.equals("EXIT")) {
                    logger.info("Клиент завершил работу.");
                    break;
                }

                switch (command) {
                    case "READ":
                        System.out.print("Введите тип массива (1 - int, 2 - double, 3 - string): ");
                        int readArrayType = scanner.nextInt();
                        System.out.print("Введите номер строки: ");
                        int readRow = scanner.nextInt();
                        System.out.print("Введите номер столбца: ");
                        int readCol = scanner.nextInt();
                        scanner.nextLine(); // Очистка буфера после nextInt()

                        out.println("READ " + readArrayType + " " + readRow + " " + readCol);
                        String readResponse = in.readLine();
                        logger.info("Отправлена команда: READ " + readArrayType + " " + readRow + " " + readCol);
                        logger.info("Ответ сервера: " + readResponse);
                        System.out.println("Ответ сервера: " + readResponse);
                        break;

                    case "WRITE":
                        System.out.print("Введите тип массива (1 - int, 2 - double, 3 - string): ");
                        int writeArrayType = scanner.nextInt();
                        System.out.print("Введите номер строки: ");
                        int writeRow = scanner.nextInt();
                        System.out.print("Введите номер столбца: ");
                        int writeCol = scanner.nextInt();
                        System.out.print("Введите значение для записи: ");
                        String value = scanner.next();
                        scanner.nextLine(); // Очистка буфера после nextInt()

                        out.println("WRITE " + writeArrayType + " " + writeRow + " " + writeCol + " " + value);
                        String writeResponse = in.readLine();
                        logger.info("Отправлена команда: WRITE " + writeArrayType + " " + writeRow + " " + writeCol + " " + value);
                        logger.info("Ответ сервера: " + writeResponse);
                        System.out.println("Ответ сервера: " + writeResponse);
                        break;

                    case "DIMENSION":
                        System.out.print("Введите тип массива (1 - int, 2 - double, 3 - string): ");
                        int dimensionArrayType = scanner.nextInt();
                        scanner.nextLine(); // Очистка буфера после nextInt()

                        out.println("DIMENSION " + dimensionArrayType);
                        String dimensionResponse = in.readLine();
                        logger.info("Отправлена команда: DIMENSION " + dimensionArrayType);
                        logger.info("Ответ сервера: " + dimensionResponse);
                        System.out.println("Ответ сервера: " + dimensionResponse);
                        break;

                    case "SET_DEFAULT":
                        System.out.print("Введите количество ячеек для установки предустановленных значений: ");
                        int count = scanner.nextInt();
                        int[] cells = new int[count];
                        System.out.println("Введите тип массива и номера ячеек (например, 1 0 0, 1 1 1, 2 0 0): ");
                        StringBuilder sb = new StringBuilder("SET_DEFAULT");
                        sb.append(" ").append(count);
                        for (int i = 0; i < count; i++) {
                            System.out.print("Введите тип массива (1 - int, 2 - double, 3 - string) и номер ячейки: ");
                            int arrayType = scanner.nextInt();
                            int row = scanner.nextInt();
                            int col = scanner.nextInt();
                            sb.append(" ").append(arrayType).append(" ").append(row).append(" ").append(col);
                        }
                        scanner.nextLine(); // Очистка буфера после nextInt()
                        out.println(sb.toString());
                        String setDefaultResponse = in.readLine();
                        logger.info("Отправлена команда: " + sb.toString());
                        logger.info("Ответ сервера: " + setDefaultResponse);
                        System.out.println("Ответ сервера: " + setDefaultResponse);
                        break;

                    default:
                        System.out.println("Неизвестная команда.");
                        logger.warning("Неизвестная команда введена: " + command);
                }
            }
        } catch (IOException e) {
            logger.severe("Ошибка при подключении к серверу: " + e.getMessage());
        } finally {
            scanner.close(); // Закрываем сканер
        }
    }
}