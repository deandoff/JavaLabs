package lab2;

public interface IExceptionsFuncs {

    void ThrowSymbolContentException(String[] args) throws SymbolContentException;

    void ThrowCertainNumberOfElementsException(String[] args)
            throws CertainNumberOfElementsException;

    void ThrowEqualNumberException(String[] args) throws EqualNumberException;
}
