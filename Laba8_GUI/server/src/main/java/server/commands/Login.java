package server.commands;

import common.forCommunicate.AuthResponseCode;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import server.UserManager;

/**
 * Команда проверки учётных данных пользователя (вход в систему).
 */
public class Login implements Command {
    private final UserManager userManager;

    public Login(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        String password = request.getPassword();
        if (userManager.authenticate(username, password)) {
            return new Response("Сколько лет сколько зим, " + username + "!", true, AuthResponseCode.LOGIN_OK);
        } else {
            return new Response("Ошибка: неверный логин или пароль", false, AuthResponseCode.LOGIN_FAILED);
        }
    }
}
