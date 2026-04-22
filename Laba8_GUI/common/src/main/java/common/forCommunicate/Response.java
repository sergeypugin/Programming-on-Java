package common.forCommunicate;

import java.io.Serial;
import java.io.Serializable;

/**
 * Класс-ответ сервера клиенту
*/
public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private final String message;
    private final boolean success;
    private final Object data;

    /**
     * Конструктор ответа сервера
     *
     * @param message строка с результатом выполнения команды для клиента
     * @param success флаг успешного выполнения команды
     * @param data данные для клиента
     */
    public Response(String message, boolean success, Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.data = null;
    }

    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    public Object getData() { return data; }
}