package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

import java.util.stream.Collectors;

/**
 * Команда для вывода всех элементов коллекции
 */
public class Show implements Command {
    private static final Logger logger = LogManager.getLogger(Show.class);
    private final CollectionManager cm;

    public Show(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        if (cm.getCollection().isEmpty()) {
            logger.info("Запрос <Show> успешно выполнен, но коллекция пуста");
            return new Response("Коллекция пуста.", true);
        }

        String result = cm.getCollection().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        logger.info("Запрос <Show> успешно выполнен");
        return new Response(result, true);
    }
}