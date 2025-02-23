package lab3;

import java.io.PrintWriter;
import java.util.ArrayList;

public class FunctionResultSource {
    IFunctionResultHandler functionResultHandler;

    public FunctionResultSource(IFunctionResultHandler functionResultHandler) {
        this.functionResultHandler = functionResultHandler;
    }

    public void generateEvent(PrintWriter pw, ArrayList<String> delayedMessagesList) {
        functionResultHandler.Handler(pw, delayedMessagesList);
    }
}
