package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Менеджер пользователей для регистрации, аутентификации и хэширования паролей алгоритмом SHA-384
 */
public class UserManager {
    private static final Logger logger = LogManager.getLogger(UserManager.class);
    private static final String HASH_ALGORITHM = "SHA-384";
    private final DatabaseManager db;

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 255;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 255;

    public UserManager(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Хэширует пароль алгоритмом SHA-384 и возвращает hex-строку
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            // SHA-384 генерирует 384 бита хеша, что равно 48 байтам.
            // Каждый байт - 2 шестнадцатеричных символа,
            // поэтому hex-строка будет иметь длину 96 символов.
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Алгоритм " + HASH_ALGORITHM + " недоступен", e);
        }
    }

    /**
     * Регистрирует нового пользователя.
     * @return true если успешно, false если логин уже занят
     */
    public boolean register(String username, String password) {
        if (username == null || username.isBlank() ||
                username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            logger.warn("Имя пользователя не соответствует правилам.");
            return false;
        }
        if (password == null || password.isBlank() ||
                username.length() < MIN_PASSWORD_LENGTH || username.length() > MAX_PASSWORD_LENGTH) {
            logger.warn("Пароль не соответствует правилам.");
            return false;
        }
        // TODO: Вывести правила валидации имени пользователя и пароля в консоль для пользователя.
        String hash = hashPassword(password);
        boolean success = db.addUser(username, hash);
        if (success) {
            logger.info("Пользователь '{}' успешно зарегистрирован", username);
        } else {
            logger.warn("Регистрация не удалась: пользователь с именем '{}' уже занят", username);
        }
        return success;
    }

    /**
     * Аутентифицирует пользователя по логину и паролю.
     * @return true если логин+пароль верны
     */
    public boolean authenticate(String username, String password) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank())
            return false;
        String storedHash = db.getPasswordHash(username);
        if (storedHash == null) return false;
        return storedHash.equals(hashPassword(password));
    }
}