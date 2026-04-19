package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.CollectionManager;

/**
 * Команда для вывода информации о коллекции
 */
public class Info implements Command {
    private final CollectionManager cm;

    public Info(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        String info = "Информация о коллекции:\n" +
                "Тип: " + cm.getCollection().getClass().getName() + "\n" +
                "Дата инициализации: " + cm.getCreationDate() + "\n" +
                "Количество элементов: " + cm.getCollection().size();
        return new Response(info, true);
    }
}