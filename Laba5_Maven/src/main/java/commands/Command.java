package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Command {
    Logger logger = LoggerFactory.getLogger(Command.class);
    default boolean validMethodWithArg(String arg) {
        if (arg.isEmpty()) {
            logger.error("Ошибка: данный метод требует аргумент (например, 'update 5')");
            return false;
        }
        return true;
    }

    void execute(String arg);
}