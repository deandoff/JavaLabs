package lab3;

import java.io.PrintWriter;
import java.util.ArrayList;

public class OutputStreamSource {
    IOutputStreamHandler streamHandler;

    public OutputStreamSource(IOutputStreamHandler streamHandler) {
        this.streamHandler = streamHandler;
    }

    public void generateEvent(PrintWriter pw, ArrayList<String> delayedMessagesList) {
        streamHandler.Handler(pw, delayedMessagesList);
    }
}
