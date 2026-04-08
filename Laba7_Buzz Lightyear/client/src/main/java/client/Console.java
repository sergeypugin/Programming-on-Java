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
        helpfulPrint(msg, true);
    }

    public void helpfulPrint(String msg, boolean newLine) {
        if (isHelpfulTextNeeded) {
            if (newLine) {
                System.out.println(msg);
            } else {
                System.out.print(msg);
            }
            System.out.flush();
        }
    }

    public void setIsHelpfulTextNeeded(boolean x) { this.isHelpfulTextNeeded = x; }
    public void setScanner(Scanner scanner) { this.scanner = scanner; }
    public Scanner getScanner() { return this.scanner; }

    /**
     * Запрашивает режим: вход или регистрация.
     * @return "login" или "register"
     */
    public String askAuthMode() {
        while (true) {
            helpfulPrint("Чтобы войти в существующий аккаунт, введите 1");
            helpfulPrint("Чтобы зарегистрироваться, введите 2");
            helpfulPrint("--> ", false);
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) return "login";
            if (choice.equals("2")) {
                helpfulPrint("Регистрация\n");
                helpfulPrint("Имя пользователя: от 3 до 255 символов.");
                helpfulPrint("Пароль: от 6 до 255 символов.\n");
                return "register";
            }
            logger.error("Вы ввели '"+choice+"', попробуйте ещё раз.");
        }
    }

    public String askUsername() {
        helpfulPrint("Логин: ", false);
        return scanner.nextLine();
    }

    public String askPassword() {
        helpfulPrint("Пароль: ", false);
        return scanner.nextLine();
    }

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
            helpfulPrint("Введите имя продукта: ", false);
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) { logger.error("Имя не может быть пустым."); continue; }
            return name;
        }
    }

    private Coordinates askCoordinates() {
        helpfulPrint("Введите координаты:");
        double x;
        while (true) {
            helpfulPrint("x (дробное с точкой): ", false);
            try { x = Double.parseDouble(scanner.nextLine().trim()); break; }
            catch (NumberFormatException e) { logger.error("Введите дробное число."); }
        }
        Float y;
        while (true) {
            helpfulPrint("y (дробное с точкой, > -93): ", false);
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
            helpfulPrint("Цена (натуральное число): ", false);
            try {
                long price = Long.parseLong(scanner.nextLine().trim());
                if (price <= 0) { logger.error("Цена должна быть > 0."); continue; }
                return price;
            } catch (NumberFormatException e) { logger.error("Введите целое число."); }
        }
    }

    private UnitOfMeasure askUnitOfMeasure() {
        while (true) {
            helpfulPrint("Единица измерения " + java.util.Arrays.toString(UnitOfMeasure.values()) + ": ", false);
            try { return UnitOfMeasure.valueOf(scanner.nextLine().trim().toUpperCase()); }
            catch (IllegalArgumentException e) { logger.error("Нет такой единицы."); }
        }
    }

    private Person askOwner() {
        while (true) {
            helpfulPrint("Имя владельца (или 'null'): ", false);
            String name = scanner.nextLine().trim().toLowerCase();
            if (name.isEmpty()) { logger.error("Строка пустая."); continue; }
            if (name.equals("null")) return null;

            Integer height;
            while (true) {
                helpfulPrint("Рост владельца: ", false);
                try {
                    height = Integer.parseInt(scanner.nextLine().trim());
                    if (height <= 0) { logger.error("Рост должен быть > 0."); continue; }
                    break;
                } catch (NumberFormatException e) { logger.error("Введите целое число."); }
            }
            float weight;
            while (true) {
                helpfulPrint("Вес владельца: ", false);
                try {
                    weight = Float.parseFloat(scanner.nextLine().trim());
                    if (weight <= 0) { logger.error("Вес должен быть > 0."); continue; }
                    return new Person(name, height, weight);
                } catch (NumberFormatException e) { logger.error("Введите число."); }
            }
        }
    }
}