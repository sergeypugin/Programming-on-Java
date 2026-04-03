package WorldModel.Exceptions;

public class CheckedException extends Exception {
    private final String ObjectName;

    public CheckedException(String message, String ObjectName) {
        super(message);
        this.ObjectName = ObjectName;
    }

    @Override
    public String getMessage() {
        return "Проверяемое исключение!!! Поломанный объект: " + ObjectName + ". Причина:\n" + super.getMessage();
    }
}