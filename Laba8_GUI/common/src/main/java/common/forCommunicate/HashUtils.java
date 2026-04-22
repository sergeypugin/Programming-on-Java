package common.forCommunicate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    private static final String HASH_ALGORITHM = "SHA-384";
    /**
     * Хэширует пароль алгоритмом SHA-384 и возвращает hex-строку
     */
    static public String hashPassword(String password) {
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
}
