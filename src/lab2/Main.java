package lab2;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ExceptionsFuncs ex = new ExceptionsFuncs();

        try {
            ex.ThrowSymbolContentException(args);
        } catch (SymbolContentException e) {
            System.out.println(e.getMessage());
        }

        try {
            ex.ThrowCertainNumberOfElementsException(args);
        } catch (CertainNumberOfElementsException e) {
            System.out.println(e.getMessage());
        }

        try {
            ex.ThrowEqualNumberException(args);
        } catch (EqualNumberException e) {
            System.out.println(e.getMessage());
        }

        List<Integer> list = new ArrayList<>();
        for (String s : args) {
            list.add(Integer.parseInt(s));
        }
        List<Integer> sortedList = list.stream().sorted().toList();
        System.out.println(sortedList);

    }
}
