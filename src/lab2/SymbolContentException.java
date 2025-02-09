package lab2;

public class SymbolContentException extends Exception {
    @Override
    public String getMessage() {
        return "Строка содержит некоторый символ";
    }
}
