package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

/**
 * Команда для добавления нового элемента в коллекцию
 */
public class Add implements Command {
    private static final Logger logger = LogManager.getLogger(Add.class);
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getObjectArgument() == null) {
            logger.warn("Получен запрос <Add> без объекта Product");
            return new Response("Ошибка: не передан объект для добавления!", false);
        }

        collectionManager.add(request.getObjectArgument());
        logger.info("Запрос <Add> успешно выполнен");
        return new Response("Продукт успешно добавлен в коллекцию!", true);
    }
}