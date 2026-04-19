package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;

/** Проверка доступности сервера — без аутентификации */
public class Ping implements Command {
    @Override
    public Response execute(Request request) {
        return new Response("I'm okay", true);
    }
}