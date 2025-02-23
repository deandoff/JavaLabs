package lab3;

import java.io.PrintWriter;
import java.util.ArrayList;

public class ArrayNumberSource {
    IArrayNumberHandler arrayNumberHandler;

    public ArrayNumberSource(IArrayNumberHandler arrayNumberHandler) {
        this.arrayNumberHandler = arrayNumberHandler;
    }

    public void generateEvent(PrintWriter pw, ArrayList<String> delayedMessagesList) {
        arrayNumberHandler.Handler(pw, delayedMessagesList);
    }
}
