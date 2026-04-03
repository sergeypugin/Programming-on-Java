package common.forCommunicate;

import common.data.Product;
import java.io.Serializable;

/**
 * Класс-запрос клиента серверу
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String argument;
    private final Product objectArgument;

    /**
     * Конструктор запроса
     *
     * @param commandName имя команды (например, "add" или "show")
     * @param argument строковый аргумент (например, id для "remove_by_id", может быть пустой строкой)
     * @param objectArgument объектный аргумент (например, Product для "add", может быть null)
     */
    public Request(String commandName, String argument, Product objectArgument) {
        this.commandName = commandName;
        this.argument = argument;
        this.objectArgument = objectArgument;
    }

    public String getCommandName() { return commandName; }
    public String getArgument() { return argument; }
    public Product getObjectArgument() { return objectArgument; }
}
