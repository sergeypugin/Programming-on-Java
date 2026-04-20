package server;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Менеджер команд. Перед выполнением
 * любой команды кроме register и login (теперь ещё и ping)
 * проверяет аутентификацию пользователя.
 */
public class CommandManager {
    private static final Logger logger = LogManager.getLogger(CommandManager.class);
    /** Команды, доступные без авторизации */
    private static final Set<String> PUBLIC_COMMANDS = Set.of("register", "login", "ping");

    private final Map<String, Command> map = new HashMap<>();
    private final UserManager userManager;

    public CommandManager(CollectionManager cm, UserManager userManager, DatabaseManager db) {
        this.userManager = userManager;

        // Публичные команды
        map.put("register", new Register(userManager));
        map.put("login", new Login(userManager));
        map.put("ping", new Ping());

        // Команды чтения
        map.put("help", new Help());
        map.put("info", new Info(cm));
        map.put("show", new Show(cm));
        map.put("count_by_unit_of_measure", new Count_by_unit_of_measure(cm));
        map.put("filter_by_owner", new Filter_by_owner(cm));
        map.put("print_field_descending_unit_of_measure", new Print_field_descending_unit_of_measure(cm));

        // Команды изменения (все проверяют владельца)
        map.put("add", new Add(cm));
        map.put("update", new Update(cm));
        map.put("remove_by_id", new Remove_by_id(cm));
        map.put("remove_at", new Remove_at(cm));
        map.put("remove_last", new Remove_last(cm));
        map.put("clear", new Clear(cm));
        map.put("reorder", new Reorder(cm));

        logger.info("Все команды инициализированы");
    }

    public Response reply(Request request) {
        String commandName = request.getCommandName().toLowerCase();
        String username = request.getUsername();

        if (!PUBLIC_COMMANDS.contains(commandName)) {
            if (!userManager.authenticate(username, request.getPassword())) {
                logger.warn("Команда <{}> не выполнена для пользователя '{}': авторизация не пройдена.", commandName, username);
                return new Response("Ошибка: необходима авторизация.\n" +
                        "Используйте 'login' или 'register'.", false);
            }
        }

        Command command = map.get(commandName);
        if (command == null) {
            logger.warn("Получена неизвестная команда <{}> от пользователя '{}'", commandName, username);
            return new Response("Ошибка: команда <" + commandName + "> не найдена.\n" +
                    "Введите 'help' для справки.", false);
        }

        Response response = command.execute(request);
        if (commandName.equals("show")) return response;
        logger.info("Выполняется команда '{}' (user={})", commandName, username);
        Response response = command.execute(request);

        if (response.isSuccess()) {
            logger.info("Команда '{}' (user={}) успешно выполнена. Ответ: {}", commandName, username, response.getMessage());
        } else {
            logger.warn("Команда '{}' (user={}) завершилась с ошибкой. Ответ: {}", commandName, username, response.getMessage());
        }
        return response;
    }
}