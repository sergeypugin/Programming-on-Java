package common.forCommunicate;

import java.io.Serializable;

/**
 * Класс-ответ сервера клиенту
*/
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final boolean success;

    /**
     * Конструктор ответа сервера
     *
     * @param message строка с результатом выполнения команды для клиента
     * @param success флаг успешного выполнения команды
     */
    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}