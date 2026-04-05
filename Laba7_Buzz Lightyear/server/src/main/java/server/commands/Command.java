package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;

/**
 * Интерфейс для всех команд
 */

public interface Command {
    /**
     * Выполнить команду
     *
     * @param request запрос клиента
     * @return ответ сервера клиенту
     */
    Response execute(Request request);

}