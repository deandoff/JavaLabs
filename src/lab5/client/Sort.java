package lab5.client;

import lab5.compute.Task;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Sort implements Task<List<Integer>>, Serializable {
    private final List<Integer> numbers;

    public Sort(List<Integer> numbers) {
        this.numbers = numbers;
    }

    @Override
    public List<Integer> execute() {
        Integer[] sortedArray = numbers.toArray(new Integer[0]);
        Arrays.sort(sortedArray);
        return Arrays.asList(sortedArray);
    }
}
