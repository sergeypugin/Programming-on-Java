package server;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.commands.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления и исполнения команд на сервере
 */
public class CommandManager {
    private static final Logger logger = LogManager.getLogger(CommandManager.class);
    private final Map<String, Command> map = new HashMap<>();

    /**
     * Конструктор менеджера команд, где регистрируются все доступные команды
     *
     * @param cm       менеджер коллекции
     */
    public CommandManager(CollectionManager cm) {
        map.put("help", new Help());
        map.put("info", new Info(cm));
        map.put("show", new Show(cm));
        map.put("add", new Add(cm));
        map.put("update", new Update(cm));
        map.put("remove_by_id", new Remove_by_id(cm));
        map.put("clear", new Clear(cm));
        map.put("remove_at", new Remove_at(cm));
        map.put("remove_last", new Remove_last(cm));
        map.put("reorder", new Reorder(cm));
        map.put("count_by_unit_of_measure", new Count_by_unit_of_measure(cm));
        map.put("filter_by_owner", new Filter_by_owner(cm));
        map.put("print_field_descending_unit_of_measure", new Print_field_descending_unit_of_measure(cm));
        logger.info("Все команды инициализированны");
    }

    /**
     * Метод для обработки запроса
     *
     * @param request объект запросаклиента
     * @return ответ клиенту
     */
    public Response reply(Request request) {
        String commandName = request.getCommandName().toLowerCase();
        Command command = map.get(commandName);
        if (command == null) {
            logger.warn("Получена неизвестная команда: {}", commandName);
            return new Response("Ошибка: команда <" + commandName + "> не найдена. Введите 'help' для справки", false);
        }
        return command.execute(request);
    }
}