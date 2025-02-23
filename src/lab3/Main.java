package lab3;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final int requiredNumber = 5;

    public static void main(String[] args) {
        OutputStreamSource outputStreamSource = new OutputStreamSource(new OutputStreamReceiver());
        FunctionResultSource functionResultSource = new FunctionResultSource(new FunctionResultReceiver());
        ArrayNumberSource arrayNumberSource = new ArrayNumberSource(new ArrayNumberReceiver());

        ArrayList<String> delayedMessages = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        System.out.print("Введите путь к файлу: ");
        String pathToFile = sc.nextLine();
        delayedMessages.add("Путь к файлу: " + pathToFile);

        File file = new File(pathToFile);
        int counter = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String journalFilePath = br.readLine();
            PrintWriter pw = new PrintWriter(journalFilePath);

            outputStreamSource.generateEvent(pw, delayedMessages);

            String[] dataString = br.readLine().split(" ");
            ArrayList<Integer> numbers = new ArrayList<>();

            for (String numberStr : dataString) {
                int number = Integer.parseInt(numberStr);
                numbers.add(number);
                counter++;
            }

            if (counter == requiredNumber) {
                arrayNumberSource.generateEvent(pw, delayedMessages);
            }

            numbers.sort(Integer::compareTo);
            System.out.println("Отсортированные числа: " + numbers);
            pw.println("Отсортированные числа: " + numbers);

            functionResultSource.generateEvent(pw, delayedMessages);

            for (String message : delayedMessages) {
                pw.println(message);
            }

            pw.flush();
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}