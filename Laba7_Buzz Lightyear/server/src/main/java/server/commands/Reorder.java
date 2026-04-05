package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

import java.util.Collections;

/**
 * Команда для сортировки коллекции в обратном порядке
 */
public class Reorder implements Command {
    private static final Logger logger = LogManager.getLogger(Reorder.class);
    private final CollectionManager collectionManager;

    public Reorder(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            logger.warn("Запрос <Reorder> не выполнен: коллекция пуста");
            return new Response("Коллекция пуста, нечего сортировать", false);
        }
        Collections.reverse(collectionManager.getCollection());
        logger.info("Запрос <Reorder> успешно выполнен");
        return new Response("Коллекция успешно отсортирована в обратном порядке", true);
    }
}