package lab2;

public class CertainNumberOfElementsException extends Exception {

    @Override
    public String getMessage() {
        return "В массиве число элементов равно указанному";
    }
}
