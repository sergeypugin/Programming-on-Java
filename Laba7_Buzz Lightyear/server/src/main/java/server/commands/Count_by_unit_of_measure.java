package server.commands;

import common.data.UnitOfMeasure;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CollectionManager;

/**
 * Команда для подсчета элементов с заданным значением unitOfMeasure
 */
public class Count_by_unit_of_measure implements Command {
    private static final Logger logger = LogManager.getLogger(Count_by_unit_of_measure.class);
    private final CollectionManager cm;

    public Count_by_unit_of_measure(CollectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Response execute(Request request) {
        try {
            UnitOfMeasure unit = UnitOfMeasure.valueOf(request.getArgument().trim().toUpperCase());
            long count = cm.countByUnitOfMeasure(unit);

            logger.info("Запрос <Count_by_unit_of_measure> успешно выполнен");
            return new Response("Количество элементов с единицей измерения " + unit + ": " + count, true);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка: некорректная единица измерения в запросе <Count_by_unit_of_measure>");
            return new Response("Ошибка: такой единицы измерения не существует", false);
        }
    }
}