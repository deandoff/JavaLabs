package lab5.client;

import lab5.compute.Task;
import lab5.compute.Compute;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

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
        try {
            String name = "Compute";
            Registry registry = LocateRegistry.getRegistry(args[0]);
            Compute comp = (Compute) registry.lookup(name);
            List<Integer> numbers = Arrays.asList(5, 3, 9, 1, 6, 2, 8, 4, 7);
            SortTask task = new SortTask(numbers);
            List<Integer> sortedNumbers = comp.execute(task);
            System.out.println("Sorted numbers: " + sortedNumbers);
        } catch (Exception e) {
            System.err.println("ComputeClient exception:");
            e.printStackTrace();
        }
    }
}