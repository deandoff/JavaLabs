package lab3;

import java.io.PrintWriter;
import java.util.ArrayList;

public class FunctionResultReceiver implements IFunctionResultHandler {
    @Override
    public void Handler(PrintWriter pw, ArrayList<String> delayedMessagesList) {
        System.out.println("Event: Получение некоторого результата работы функции");

        if (pw == null) {
            delayedMessagesList.add("Event: Получение некоторого результата работы функции");
        } else {
            pw.println("Event: Получение некоторого результата работы функции");
        }
    }
}
