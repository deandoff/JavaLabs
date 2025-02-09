package lab1;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (String s : args) {
            list.add(Integer.parseInt(s));
        }
        List<Integer> sortedList = list.stream().sorted().toList();
        System.out.println(sortedList);
    }
}