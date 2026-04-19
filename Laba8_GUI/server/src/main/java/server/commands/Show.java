package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

import java.util.stream.Collectors;

/**
 * Команда для вывода всех элементов коллекции
 */
public class Show implements Command {
    private final CollectionManager cm;

    public Show(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        if (cm.getCollection().isEmpty()) {
            return new Response("Коллекция пуста.", true);
        }

        String result = cm.getCollection().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        return new Response(result, true);
    }
}