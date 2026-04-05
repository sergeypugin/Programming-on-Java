package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Help implements Command {
    private static final Logger logger = LogManager.getLogger(Help.class);

    @Override
    public Response execute(Request request) {
        String helpText = "В данной программе реализованы следующие команды:\n" +
                "help : вывести справку по доступным командам\n" +
                "info : вывести информацию о коллекции\n" +
                "show : вывести все элементы коллекции\n" +
                "add {element} : добавить новый элемент\n" +
                "update id {element} : обновить значение элемента по id\n" +
                "remove_by_id id : удалить элемент по его id\n" +
                "clear : очистить коллекцию\n" +
                "execute_script file_name : исполнить скрипт из файла\n" +
                "exit : завершить работу клиента\n" +
                "remove_at index : удалить элемент в заданной позиции\n" +
                "remove_last : удалить последний элемент\n" +
                "reorder : отсортировать коллекцию в обратном порядке\n" +
                "count_by_unit_of_measure unit : количество элементов с заданной единицей измерения\n" +
                "filter_by_owner owner : вывести элементы конкретного владельца\n" +
                "print_field_descending_unit_of_measure : вывести единицы измерения в порядке убывания";
        logger.info("Запрос <Help> выполнен");
        return new Response(helpText, true);
    }
}