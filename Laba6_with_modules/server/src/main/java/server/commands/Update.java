package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

/**
 * Команда для обновления элемента коллекции по его id
 */
public class Update implements Command {
    private static final Logger logger = LogManager.getLogger(Update.class);
    private final CollectionManager cm;

    public Update(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        if (request.getObjectArgument() == null) {
            logger.error("Ошибка: в запросе <Update> отсутствует объект продукта");
            return new Response("Ошибка: объект для обновления не передан", false);
        }

        try {
            int id = Integer.parseInt(request.getArgument().trim());
            if (cm.replace(id, request.getObjectArgument())) {
                logger.info("Запрос <Update> успешно выполнен");
                return new Response("Продукт с ID=" + id + " успешно обновлен", true);
            } else {
                logger.error("Ошибка: продукт с ID={} для <Update> не найден", id);
                return new Response("Ошибка: продукт с таким ID не найден", false);
            }
        } catch (NumberFormatException e) {
            logger.error("Ошибка: передан некорректный ID в запрос <Update>");
            return new Response("Ошибка: ID должен быть числом", false);
        }
    }
}