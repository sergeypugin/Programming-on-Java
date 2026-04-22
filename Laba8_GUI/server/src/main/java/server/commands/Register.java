package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import common.forCommunicate.AuthResponseCode;
import server.UserManager;

/**
 * Команда регистрации нового пользователя.
 */
public class Register implements Command {
    private final UserManager userManager;

    public Register(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        String password = request.getPassword();
        Boolean registrationResult = userManager.register(username, password);

        if (Boolean.TRUE.equals(registrationResult)) {
            return new Response("Регистрация прошла успешно! Добро пожаловать, " + username + "!", true, AuthResponseCode.REGISTER_OK);
        } else if (Boolean.FALSE.equals(registrationResult)) {
            return new Response("Ошибка регистрации: некорректное имя пользователя или пароль. ", false, AuthResponseCode.REGISTER_INVALID);
        } else {
            return new Response("Ошибка регистрации: пользователь с логином '" + username + "' уже существует.", false, AuthResponseCode.REGISTER_USER_EXISTS);
        }
    }
}
