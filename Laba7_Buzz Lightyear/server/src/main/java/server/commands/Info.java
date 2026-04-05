package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

//TODO удалить все комментарии перед классами команд,
// ибо очев, что они делают

/**
 * Команда для вывода информации о коллекции
 */
public class Info implements Command {
    private static final Logger logger = LogManager.getLogger(Info.class);
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
        // TODO: ну удали ты нахер эти говнологеры, ну кому они сдались.
        //  Вынеси логику в CommandManager и хватит
        logger.info("Запрос <Info> успешно выполнен");
        return new Response(info, true);
    }
}