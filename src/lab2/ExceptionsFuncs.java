package lab2;

public class ExceptionsFuncs implements IExceptionsFuncs, IExceptionsConsts {

    public void ThrowSymbolContentException(String[] args) throws SymbolContentException {
        for (String s : args) {
            if (!(s.indexOf(IExceptionsConsts.symbol) == -1)) throw new SymbolContentException();
        }
    }

    public void ThrowCertainNumberOfElementsException(String[] args) throws CertainNumberOfElementsException {
        if (args.length == IExceptionsConsts.elementNumber) throw new CertainNumberOfElementsException();
    }

    public void ThrowEqualNumberException(String[] args) throws EqualNumberException {
        for (String s : args) {
            if (Integer.parseInt(s) == IExceptionsConsts.equalNumber) throw new EqualNumberException();
        }
    }
}
