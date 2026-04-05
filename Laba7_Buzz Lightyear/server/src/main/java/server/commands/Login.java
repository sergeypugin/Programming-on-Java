package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.UserManager;

/**
 * Команда проверки учётных данных пользователя (вход в систему).
 */
public class Login implements Command {
    private static final Logger logger = LogManager.getLogger(Login.class);
    private final UserManager userManager;

    public Login(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (userManager.authenticate(username, password)) {
            logger.info("Успешный вход пользователя: {}", username);
            return new Response("Добро пожаловать, " + username + "!", true);
        } else {
            logger.warn("Неудачная попытка входа: {}", username);
            return new Response("Ошибка: неверный логин или пароль", false);
        }
    }
}