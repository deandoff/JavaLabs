package lab4;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
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
            case 1:
                intArray[row][col] = Integer.parseInt(value);
                break;
            case 2:
                doubleArray[row][col] = Double.parseDouble(value);
                break;
            case 3:
                stringArray[row][col] = value;
                break;
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
            case 1:
                intArray[row][col] = -1;
                break;
            case 2:
                doubleArray[row][col] = -1.0;
                break;
            case 3:
                stringArray[row][col] = "default";
                break;
        }
        printArrays();
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                logger.info("Получена команда от клиента: " + inputLine);
                String[] parts = inputLine.split(" ");
                String command = parts[0];

                switch (command) {
                    case "READ":
                        int readArrayType = Integer.parseInt(parts[1]);
                        int readRow = Integer.parseInt(parts[2]);
                        int readCol = Integer.parseInt(parts[3]);
                        out.println(readValue(readArrayType, readRow, readCol));
                        break;

                    case "WRITE":
                        int writeArrayType = Integer.parseInt(parts[1]);
                        int writeRow = Integer.parseInt(parts[2]);
                        int writeCol = Integer.parseInt(parts[3]);
                        String value = parts[4];
                        writeValue(writeArrayType, writeRow, writeCol, value);
                        out.println("Запись выполнена.");
                        break;

                    case "DIMENSION":
                        int dimensionArrayType = Integer.parseInt(parts[1]);
                        out.println("Размерность: " + getDimension(dimensionArrayType));
                        break;

                    case "SET_DEFAULT":
                        int cellCount = Integer.parseInt(parts[1]);
                        int j = 2;
                        for (int i = 0; i < cellCount; i++) {
                            if (j + 2 >= parts.length) {
                                out.println("Ошибка: недостаточно аргументов для установки предустановленных значений.");
                                return;
                            }
                            int arrayType = Integer.parseInt(parts[j]);
                            int row = Integer.parseInt(parts[j + 1]);
                            int col = Integer.parseInt(parts[j + 2]);
                            if (isValidIndex(arrayType, row, col)) {
                                setDefaultValues(arrayType, row, col);
                            } else {
                                out.println("Ошибка: Индексы выходят за пределы массива для типа " + arrayType);
                            }
                            j += 3;
                        }
                        out.println("Предустановленные значения установлены.");
                        break;

                    default:
                        out.println("Неизвестная команда.");
                }
            }
        } catch (IOException e) {
            logger.severe("Ошибка при обработке клиента: " + e.getMessage());
        }
    }

    private static boolean isValidIndex(int arrayType, int row, int col) {
        return switch (arrayType) {
            case 1 -> row >= 0 && row < intArray.length && col >= 0 && col < intArray[0].length;
            case 2 -> row >= 0 && row < doubleArray.length && col >= 0 && col < doubleArray[0].length;
            case 3 -> row >= 0 && row < stringArray.length && col >= 0 && col < stringArray[0].length;
            default -> false;
        };
    }

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("src/lab4/server.properties"));
        String host = prop.getProperty("SERVER_HOSTNAME");
        int port = Integer.parseInt(prop.getProperty("SERVER_PORT"));

        Scanner sc = new Scanner(System.in);
        System.out.println("Введите путь к файлу журнала: ");
        String file = sc.nextLine();

        FileHandler fileHandler = new FileHandler("src/lab4/"+file, true);
        logger.addHandler(fileHandler);
        logger.setLevel(Level.INFO);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);

        logger.info("Starting TCP Server on " + host + ":" + port + " ...");

        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Listening on port " + port + " ....");
            while (true) {
                Socket clientSocket = server.accept();
                logger.info("Client connected: " + clientSocket.getRemoteSocketAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            logger.severe("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
}