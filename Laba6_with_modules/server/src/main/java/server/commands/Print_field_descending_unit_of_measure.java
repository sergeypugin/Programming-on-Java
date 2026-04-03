package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

import java.util.stream.Collectors;

/**
 * Команда для вывода значений поля unitOfMeasure всех элементов в порядке убывания
 */
public class Print_field_descending_unit_of_measure implements Command {
    private static final Logger logger = LogManager.getLogger(Print_field_descending_unit_of_measure.class);
    private final CollectionManager cm;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public Print_field_descending_unit_of_measure(CollectionManager collectionManager) {
        this.cm = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (cm.getCollection().isEmpty()) {
            logger.info("Запрос <Print_field_descending_unit_of_measure> успешно выполнен, но коллекция пуста");
            return new Response("Коллекция пуста.", true);
        }
        String result = cm.getCollection().stream()
                .sorted((p1, p2) -> p2.getUnitOfMeasure().compareTo(p1.getUnitOfMeasure()))
                .map(pr -> pr.getName() + ": " + pr.getUnitOfMeasure())
                .collect(Collectors.joining("\n"));

        logger.info("Запрос <Print_field_descending_unit_of_measure> успешно выполнен");
        return new Response(result, true);
    }
}