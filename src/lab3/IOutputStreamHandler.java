package lab3;

import java.io.PrintWriter;
import java.util.ArrayList;

public interface IOutputStreamHandler {
    void Handler(PrintWriter pw, ArrayList<String> delayedMessagesList);
}
