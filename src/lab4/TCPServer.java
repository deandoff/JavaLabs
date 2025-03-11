package lab4;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class TCPServer {
    public static int[][] intArray = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
    public static double[][] doubleArray = {{1.1, 2.2, 3.3}, {4.4, 5.5, 6.6}, {7.7, 8.8, 9.9}};
    public static String[][] stringArray = {{"one", "two", "three"}, {"four", "five", "six"}, {"seven", "eight", "nine"}};

    private static Logger logger = Logger.getLogger(TCPServer.class.getName());

    private static void printArrays() {
        logger.info("Целочисленный массив: " + Arrays.deepToString(intArray));
        logger.info("Вещественный массив: " + Arrays.deepToString(doubleArray));
        logger.info("Строковый массив: " + Arrays.deepToString(stringArray));
    }

    private static String readValue(int arrayType, int row, int col) {
        return switch (arrayType) {
            case 1 -> String.valueOf(intArray[row][col]);
            case 2 -> String.valueOf(doubleArray[row][col]);
            case 3 -> stringArray[row][col];
            default -> "Неверный тип массива.";
        };
    }

    private static void writeValue(int arrayType, int row, int col, String value) {
        switch (arrayType) {
            case 1 -> intArray[row][col] = Integer.parseInt(value);
            case 2 -> doubleArray[row][col] = Double.parseDouble(value);
            case 3 -> stringArray[row][col] = value;
        }
        printArrays();
    }

    private static int getDimension(int arrayType) {
        return switch (arrayType) {
            case 1 -> intArray.length * intArray[0].length;
            case 2 -> doubleArray.length * doubleArray[0].length;
            case 3 -> stringArray.length * stringArray[0].length;
            default -> -1;
        };
    }

    private static void setDefaultValues(int arrayType, int row, int col) {
        switch (arrayType) {
            case 1 -> intArray[row][col] = -1;
            case 2 -> doubleArray[row][col] = -1.0;
            case 3 -> stringArray[row][col] = "default";
        }
        printArrays();
    }

    private static boolean isValidIndex(int arrayType, int row, int col) {
        return switch (arrayType) {
            case 1 -> row >= 0 && row < intArray.length && col >= 0 && col < intArray[0].length;
            case 2 -> row >= 0 && row < doubleArray.length && col >= 0 && col < doubleArray[0].length;
            case 3 -> row >= 0 && row < stringArray.length && col >= 0 && col < stringArray[0].length;
            default -> false;
        };
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String command;
            while ((command = in.readLine()) != null) {
                String dataLine = in.readLine();
                logger.info(String.format("Получено: [%s] Данные: [%s] От клиента: [%s]", command, dataLine, clientSocket.getRemoteSocketAddress()));

                if (dataLine == null) {
                    out.println("Ошибка: Нет данных для команды");
                    continue;
                }

                String[] data = dataLine.split(" ");

                switch (command) {
                    case "READ" -> {
                        if (data.length < 3) {
                            out.println("Ошибка: Недостаточно параметров");
                            break;
                        }
                        int arrayType = Integer.parseInt(data[0]);
                        int row = Integer.parseInt(data[1]);
                        int col = Integer.parseInt(data[2]);
                        out.println(readValue(arrayType, row, col));
                    }

                    case "WRITE" -> {
                        if (data.length < 4) {
                            out.println("Ошибка: Недостаточно параметров");
                            break;
                        }
                        int type = Integer.parseInt(data[0]);
                        int row = Integer.parseInt(data[1]);
                        int col = Integer.parseInt(data[2]);
                        String value = data[3];
                        writeValue(type, row, col, value);
                        out.println("Запись выполнена.");
                    }

                    case "DIMENSION" -> {
                        if (data.length < 1) {
                            out.println("Ошибка: Недостаточно параметров");
                            break;
                        }
                        int type = Integer.parseInt(data[0]);
                        out.println("Размерность: " + getDimension(type));
                    }

                    case "SET_DEFAULT" -> {
                        if (data.length < 1) {
                            out.println("Ошибка: Недостаточно параметров");
                            break;
                        }
                        int cellCount = Integer.parseInt(data[0]);
                        int index = 1;
                        boolean hasErrors = false;

                        for (int i = 0; i < cellCount; i++) {
                            if (index + 2 >= data.length) {
                                out.println("Ошибка: Недостаточно аргументов для установки");
                                hasErrors = true;
                                break;
                            }
                            int arrType = Integer.parseInt(data[index]);
                            int r = Integer.parseInt(data[index + 1]);
                            int c = Integer.parseInt(data[index + 2]);

                            if (isValidIndex(arrType, r, c)) {
                                setDefaultValues(arrType, r, c);
                            } else {
                                out.println("Ошибка: Неверные индексы для типа " + arrType);
                                hasErrors = true;
                            }
                            index += 3;
                        }
                        if (!hasErrors) out.println("Предустановленные значения установлены.");
                    }

                    default -> out.println("Неизвестная команда.");
                }
            }
        } catch (IOException e) {
            logger.severe("Ошибка при обработке клиента: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("src/lab4/server.properties"));
            String host = prop.getProperty("SERVER_HOSTNAME");
            int port = Integer.parseInt(prop.getProperty("SERVER_PORT"));

            Scanner sc = new Scanner(System.in);
            System.out.println("Введите путь к файлу журнала: ");
            String file = sc.nextLine();

            FileHandler fileHandler = new FileHandler("src/lab4/" + file, true);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());

            logger.info("Запуск TCP-сервера на " + host + ":" + port);

            try (ServerSocket server = new ServerSocket(port)) {
                logger.info("Сервер запущен и слушает порт " + port);
                var executor = Executors.newCachedThreadPool();

                while (true) {
                    Socket clientSocket = server.accept();
                    executor.submit(() -> {
                        logger.info("Подключен клиент: " + clientSocket.getRemoteSocketAddress());
                        handleClient(clientSocket);
                    });
                }
            }
        } catch (IOException e) {
            logger.severe("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
}