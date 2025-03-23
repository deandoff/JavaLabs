package lab5.client;

import lab5.compute.Task;
import lab5.compute.Compute;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class SortTask implements Task<List<Integer>>, Serializable {
    private final List<Integer> numbers;

    public SortTask(List<Integer> numbers) {
        this.numbers = numbers;
    }

    @Override
    public List<Integer> execute() {
        Integer[] sortedArray = numbers.toArray(new Integer[0]);
        Arrays.sort(sortedArray);
        return Arrays.asList(sortedArray);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java lab5.client.SortTask <host> <port> <number1> <number2> ...");
            System.exit(1);
        }

        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);

            List<Integer> numbers = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                numbers.add(Integer.parseInt(args[i]));
            }

            Registry registry = LocateRegistry.getRegistry(host, port);
            Compute comp = (Compute) registry.lookup("Compute");

            Task<List<Integer>> task = new SortTask(numbers);
            List<Integer> sortedNumbers = comp.execute(task);

            System.out.println("Sorted numbers: " + sortedNumbers);
        } catch (Exception e) {
            System.err.println("ComputeClient exception:");
            e.printStackTrace();
        }
    }
}
