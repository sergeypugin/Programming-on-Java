package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

/**
 * Команда для удаления последнего элемента из коллекции
 */
public class Remove_last implements Command {
    private static final Logger logger = LogManager.getLogger(Remove_last.class);
    private final CollectionManager cm;

    public Remove_last(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        if (cm.removeLast(username)) {
            logger.info("Запрос <Remove_last> (user: {}) успешно выполнен", username);
            return new Response("Последний элемент успешно удален", true);
        } else {
            logger.warn("Ошибка при выполнении <Remove_last> (user: {}): коллекция пуста или у пользователя нет прав на удаление последнего элемента", username);
            return new Response("Ошибка: коллекция пуста или у вас нет прав на удаление последнего элемента.", false);
        }
    }
}