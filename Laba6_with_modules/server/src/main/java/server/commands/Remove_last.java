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
        if (cm.removeLast()) {
            logger.info("Запрос <Remove_last> успешно выполнен");
            return new Response("Последний элемент успешно удален", true);
        } else {
            logger.error("Ошибка: запрос <Remove_last> не выполнен, коллекция пуста");
            return new Response("Ошибка: коллекция пуста, удалять нечего", false);
        }
    }
}