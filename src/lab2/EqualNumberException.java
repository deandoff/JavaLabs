package lab2;

public class EqualNumberException extends Exception {
    @Override
    public String getMessage() {
        return "Один из элементов массива равен некоторому числу";
    }
}
