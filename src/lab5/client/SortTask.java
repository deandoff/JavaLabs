package lab5.client;

import lab5.compute.Compute;
import lab5.compute.Task;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class SortTask {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java lab5.client.SortClient <host> <port> <number1> <number2> ...");
            System.exit(1);
        }
        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            List<Integer> numbers = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                try {
                    numbers.add(Integer.parseInt(args[i]));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number: " + args[i]);
                    System.exit(1);
                }
            }

            Registry registry = LocateRegistry.getRegistry(host, port);
            Compute comp = (Compute) registry.lookup("Compute");

            Task<List<Integer>> task = new Sort(numbers);
            List<Integer> sortedNumbers = comp.execute(task);

            System.out.println("Sorted numbers: " + sortedNumbers);
        } catch (Exception e) {
            System.err.println("SortClient exception:");
            e.printStackTrace();
        }
    }
}