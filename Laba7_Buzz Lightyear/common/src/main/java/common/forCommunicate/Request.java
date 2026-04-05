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
    private final String username;
    private final String password;

    /**
     * Конструктор запроса
     *
     * @param commandName имя команды (например, "add" или "show")
     * @param argument строковый аргумент (например, id для "remove_by_id", может быть пустой строкой)
     * @param objectArgument объектный аргумент (например, Product для "add", может быть null)
     * @param username логин пользователя
     * @param password пароль пользователя
     */
    public Request(String commandName, String argument, Product objectArgument, String username, String password) {
        this.commandName = commandName;
        this.argument = argument;
        this.objectArgument = objectArgument;
        this.username = username;
        this.password = password;
    }

    public String getCommandName() { return commandName; }
    public String getArgument() { return argument; }
    public Product getObjectArgument() { return objectArgument; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
