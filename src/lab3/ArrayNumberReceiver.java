package lab3;

import java.io.PrintWriter;
import java.util.ArrayList;

public class ArrayNumberReceiver implements IArrayNumberHandler {
    @Override
    public void Handler(PrintWriter pw, ArrayList<String> delayedMessagesList) {
        System.out.println("Event: В массиве число элементов равно указанному");

        if (pw == null) {
            delayedMessagesList.add("Event: В массиве число элементов равно указанному");
        } else {
            pw.println("Event: В массиве число элементов равно указанному");
        }
    }
}
