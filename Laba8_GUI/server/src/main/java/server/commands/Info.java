package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import common.forCommunicate.CollectionInfo;
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
        CollectionInfo infoData = new CollectionInfo(
                "java.util.LinkedList",
                cm.getCreationDate(),
                cm.size()
        );
        String info = "Информация о коллекции:\n" +
                "Тип: " + infoData.getCollectionType() + "\n" +
                "Дата инициализации: " + infoData.getCreationDate() + "\n" +
                "Количество элементов: " + infoData.getSize();
        return new Response(info, true, infoData);
    }
}
