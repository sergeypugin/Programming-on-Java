package client;

import common.data.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Scanner;

public class Console {
    private static final Logger logger = LogManager.getLogger(Console.class);
    private Scanner scanner;
    private boolean isHelpfulTextNeeded = true;

    public Console(Scanner scanner) {
        this.scanner = scanner;
    }

    public void helpfulPrint(String msg) {
        if (isHelpfulTextNeeded) {
            System.out.print(msg);
            System.out.flush();
        }
    }

    public void setIsHelpfulTextNeeded(boolean x) { isHelpfulTextNeeded = x; }
    public void setScanner(Scanner scanner) { this.scanner = scanner; }
    public Scanner getScanner() { return this.scanner; }

    // ────────────────────── Авторизация ──────────────────────

    /**
     * Запрашивает режим: вход или регистрация.
     * @return "login" или "register"
     */
    public String askAuthMode() {
        while (true) {
            System.out.println("\n=== Добро пожаловать в StoreForYou! ===");
            System.out.println("1) Войти в существующий аккаунт (login)");
            System.out.println("2) Зарегистрироваться (register)");
            System.out.print("Выберите действие (1/2): ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) return "login";
            if (choice.equals("2")) return "register";
            System.out.println("Введите 1 или 2.");
        }
    }

    public String askUsername() {
        while (true) {
            System.out.print("Логин: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Логин не может быть пустым.");
                continue;
            }
            return username;
        }
    }

    public String askPassword() {
        while (true) {
            System.out.print("Пароль: ");
            String password = scanner.nextLine(); // не trim — пароль может содержать пробелы
            if (password.isEmpty()) {
                System.out.println("Пароль не может быть пустым.");
                continue;
            }
            return password;
        }
    }

    // ─────────────────────── Продукт ────────────────────────

    public Product askProduct() {
        String name = askName();
        Coordinates coordinates = askCoordinates();
        long price = askPrice();
        UnitOfMeasure unit = askUnitOfMeasure();
        Person owner = askOwner();
        return new Product(name, coordinates, price, unit, owner);
    }

    private String askName() {
        while (true) {
            helpfulPrint("Введите имя продукта:\n$ ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) { logger.error("Имя не может быть пустым."); continue; }
            return name;
        }
    }

    private Coordinates askCoordinates() {
        helpfulPrint("Введите координаты:\n");
        double x;
        while (true) {
            helpfulPrint("x (дробное с точкой):\n$ ");
            try { x = Double.parseDouble(scanner.nextLine().trim()); break; }
            catch (NumberFormatException e) { logger.error("Введите дробное число."); }
        }
        Float y;
        while (true) {
            helpfulPrint("y (дробное с точкой, > -93):\n$ ");
            try {
                y = Float.parseFloat(scanner.nextLine().trim());
                if (y <= -93) { logger.error("y должен быть > -93."); continue; }
                break;
            } catch (NumberFormatException e) { logger.error("Введите дробное число."); }
        }
        return new Coordinates(x, y);
    }

    private long askPrice() {
        while (true) {
            helpfulPrint("Цена (натуральное число):\n$ ");
            try {
                long price = Long.parseLong(scanner.nextLine().trim());
                if (price <= 0) { logger.error("Цена должна быть > 0."); continue; }
                return price;
            } catch (NumberFormatException e) { logger.error("Введите целое число."); }
        }
    }

    private UnitOfMeasure askUnitOfMeasure() {
        while (true) {
            helpfulPrint("Единица измерения " + java.util.Arrays.toString(UnitOfMeasure.values()) + ":\n$ ");
            try { return UnitOfMeasure.valueOf(scanner.nextLine().trim().toUpperCase()); }
            catch (IllegalArgumentException e) { logger.error("Нет такой единицы."); }
        }
    }

    private Person askOwner() {
        while (true) {
            helpfulPrint("Имя владельца (или 'null'):\n$ ");
            String name = scanner.nextLine().trim().toLowerCase();
            if (name.isEmpty()) { logger.error("Строка пустая."); continue; }
            if (name.equals("null")) return null;

            Integer height;
            while (true) {
                helpfulPrint("Рост владельца:\n$ ");
                try {
                    height = Integer.parseInt(scanner.nextLine().trim());
                    if (height <= 0) { logger.error("Рост должен быть > 0."); continue; }
                    break;
                } catch (NumberFormatException e) { logger.error("Введите целое число."); }
            }
            float weight;
            while (true) {
                helpfulPrint("Вес владельца:\n$ ");
                try {
                    weight = Float.parseFloat(scanner.nextLine().trim());
                    if (weight <= 0) { logger.error("Вес должен быть > 0."); continue; }
                    return new Person(name, height, weight);
                } catch (NumberFormatException e) { logger.error("Введите число."); }
            }
        }
    }
}