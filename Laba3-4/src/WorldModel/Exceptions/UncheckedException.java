package WorldModel.Exceptions;

public final class UncheckedException extends Exception {
    private final String ObjectName;

    public UncheckedException(String ObjectName, String reason) {
        super(reason);
        this.ObjectName = ObjectName;
    }

    @Override
    public String getMessage() {
        return "Непроверяемое исключение!!! Поломанный объект: " + ObjectName + ". Причина:\n" + super.getMessage();
    }
}