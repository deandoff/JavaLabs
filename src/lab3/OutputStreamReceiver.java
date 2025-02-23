package lab3;

import java.io.PrintWriter;
import java.util.ArrayList;

public class OutputStreamReceiver implements IOutputStreamHandler {
    @Override
    public void Handler(PrintWriter pw, ArrayList<String> delayedMessagesList) {
        System.out.println("Event: Обращение к потоку вывода в указанный файл");

        if (pw == null) {
            delayedMessagesList.add("Event: Обращение к потоку вывода в указанный файл");
        } else {
            pw.println("Event: Обращение к потоку вывода в указанный файл");
        }
    }
}
