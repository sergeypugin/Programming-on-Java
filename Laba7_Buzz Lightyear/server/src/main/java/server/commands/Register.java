package server.commands;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.UserManager;

/**
 * Команда регистрации нового пользователя.
 */
public class Register implements Command {
    private static final Logger logger = LogManager.getLogger(Register.class);
    private final UserManager userManager;

    public Register(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Response execute(Request request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || username.isBlank()) {
            return new Response("Ошибка: логин не может быть пустым", false);
        }
        if (password == null || password.isEmpty()) {
            return new Response("Ошибка: пароль не может быть пустым", false);
        }

        boolean success = userManager.register(username, password);
        if (success) {
            logger.info("Пользователь '{}' зарегистрирован", username);
            return new Response("Регистрация прошла успешно! Добро пожаловать, " + username + "!", true);
        } else {
            logger.warn("Попытка зарегистрировать занятый логин: {}", username);
            return new Response("Ошибка: пользователь с логином '" + username + "' уже существует", false);
        }
    }
}