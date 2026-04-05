//package client;
//
//import common.data.Coordinates;
//import common.data.Person;
//import common.data.Product;
//import common.data.UnitOfMeasure;
// import org.apache.logging.log4j.Logger;
// import org.apache.logging.log4j.LogManager;
//
//import java.util.Scanner;
//
//public class ConsoleOldToDelete {
//    private static final Logger logger = LogManager.getLogger(ConsoleOldToDelete.class);
//    private Scanner scanner;
//    private boolean isHelpfulTextNeeded = true;
//
//
//    public void helpfulPrint(String msg) {
//        if (isHelpfulTextNeeded) {
//            System.out.print(msg);
//            System.out.flush();
//        }
//    }
//
//    public ConsoleOldToDelete(Scanner scanner) {
//        this.scanner = scanner;
//    }
//
//    public void setIsHelpfulTextNeeded(boolean x) {
//        isHelpfulTextNeeded = x;
//    }
//
//    public void setScanner(Scanner scanner) {
//        this.scanner = scanner;
//    }
//
//    public Scanner getScanner() {
//        return this.scanner;
//    }
//
//    public Product askProduct() {
//        String name = askName();
//        Coordinates coordinates = askCoordinates();
//        long price = askPrice();
//        UnitOfMeasure unit = askUnitOfMeasure();
//        Person owner = askOwner();
//        return new Product(name, coordinates, price, unit, owner);
//    }
//
//    private String askName() {
//        while (true) {
//            helpfulPrint("Введите имя продукта:\n$ ");
//            String name = scanner.nextLine().trim();
//            logger.debug("Введено имя продукта: <{}>", name);
//            if (name.isEmpty()) {
//                logger.error("Ошибка: имя не может быть пустым.");
//                continue;
//            }
//            return name;
//        }
//    }
//
//    private Coordinates askCoordinates() {
//        helpfulPrint("Введите координаты:\n");
//        double x;
//        while (true) {
//            helpfulPrint("Введите x (дробное число c точкой):\n$ ");
//            try {
//                x = Double.parseDouble(scanner.nextLine().trim());
//                logger.debug("Введена координата x: <{}>", x);
////                Double.valueOf(...) - возвращает объект-обертку класс Double.
////                Double.parseDouble(...) - возвращает примитив double.
//                break;
//            } catch (NumberFormatException e) {
//                logger.error("Ошибка: введите именно дробное число с точкой.");
//            }
//        }
//        Float y;
//        while (true) {
//            helpfulPrint("Введите y (дробное число c точкой, > -93):\n$ ");
//            try {
//                y = Float.parseFloat(scanner.nextLine().trim());
//                logger.debug("Введена координата y: <{}>", y);
//            } catch (NumberFormatException e) {
//                logger.error("Ошибка: введите именно дробное число с точкой.");
//                continue;
//            }
//            if (y <= -93) {
//                logger.error("Ошибка: y должен быть больше -93.");
//                continue;
//            }
//            break;
//        }
//        return new Coordinates(x, y);
//    }
//
//    private long askPrice() {
//        while (true) {
//            helpfulPrint("Введите цену (натуральное число):\n$ ");
//            try {
//                long price = Long.parseLong(scanner.nextLine().trim());
//                logger.debug("Введена цена: <{}>", price);
//                if (price <= 0) {
//                    logger.error("Ошибка:\n\"натуральное\" число - \n",
//                            "(матем.) целое положительное число;\n",
//                            "одно из чисел, возникающих естественным\n",
//                            "образом при нумерации предметов\n");
//                    continue;
//                }
//                return price;
//            } catch (NumberFormatException e) {
//                logger.error("Ошибка: введите именно натуральное число.");
//            }
//        }
//    }
//
//    private UnitOfMeasure askUnitOfMeasure() {
//        while (true) {
//            helpfulPrint("Выберите единицу измерения из списка:\n");
//            for (UnitOfMeasure unit : UnitOfMeasure.values()) {
//                helpfulPrint(unit + " ");
//            }
//            helpfulPrint("\n$ ");
//            String input = scanner.nextLine().trim().toUpperCase();
//            try {
//                return UnitOfMeasure.valueOf(input);
//            } catch (IllegalArgumentException e) {
//                logger.error("Ошибка: такой единицы измерения нет.");
//            }
//        }
//    }
//
//    private Person askOwner() {
//        String name;
//        while (true) {
//            helpfulPrint("Введите имя владельца (если его нет, то 'null'):\n$ ");
//            name = scanner.nextLine().trim().toLowerCase();
//            logger.debug("Введено имя владельца: <{}>", name);
//            if (name.isEmpty()) {
//                logger.error("Строка пустая! Где же владелец?");
//                continue;
//            } else if (name.equals("null")) {
//                return null;
//            } else {
//                Integer height;
//                while (true) {
//                    try {
//                        helpfulPrint("Введите рост владельца:\n$ ");
//                        height = Integer.parseInt(scanner.nextLine().trim());
//                        logger.debug("Введён рост владельца: <{}>", height);
//                        if (height <= 0) {
//                            logger.error("Не уверен, что рост может быть <=0");
//                            continue;
//                        }
//                        break;
//                    } catch (NumberFormatException e) {
//                        logger.error("Ошибка: введите именно целое число.");
//                    }
//                }
//                float weight;
//                while (true) {
//                    try {
//                        helpfulPrint("Введите вес владельца:\n$ ");
//                        weight = Float.valueOf(scanner.nextLine().trim());
//                        logger.debug("Введён вес владельца: <{}>", weight);
//                        if (weight <= 0) {
//                            logger.error("Не уверен, что вес может быть <=0");
//                            continue;
//                        }
//                        break;
//                    } catch (NumberFormatException e) {
//                        logger.error("Ошибка: введите именно число c точкой.");
//                    }
//                }
//                return new Person(name, height, weight);
//            }
//        }
//    }
//}