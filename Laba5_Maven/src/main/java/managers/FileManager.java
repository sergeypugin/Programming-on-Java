package managers;

import java.util.ArrayList;

import data.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileManager {
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

    private static String getTagValue(String xmlBlock, String tagName) {
        String startTag = "<" + tagName + ">";
        String endTag = "</" + tagName + ">";
        int start = xmlBlock.indexOf(startTag);
        int end = xmlBlock.indexOf(endTag);

        if (start == -1 || end == -1) return null;

        return xmlBlock.substring(start + startTag.length(), end).trim();
    }

    private static void printError(String msg) {
        logger.error(msg);
    }

    public static void writeCollectionToXML(String fileName, LinkedList<Product> collection) {
        try (FileWriter in = new FileWriter(fileName)) {
            in.write("<collection>\n");
            for (Product product : collection) {
                in.write("  <product>\n");
                in.write("    <id>" + product.getId() + "</id>\n");
                in.write("    <name>" + product.getName() + "</name>\n");
                in.write("    <coordinates>\n");
                in.write("      <x>" + product.getCoordinates().getX() + "</x>\n");
                in.write("      <y>" + product.getCoordinates().getY() + "</y>\n");
                in.write("    </coordinates>\n");
                in.write("    <creationDate>" + product.getCreationDate().getTime() + "</creationDate>\n");
                in.write("    <price>" + product.getPrice() + "</price>\n");
                in.write("    <unitOfMeasure>" + product.getUnitOfMeasure() + "</unitOfMeasure>\n");

                if (product.getOwner() != null) {
                    in.write("    <owner>\n");
                    in.write("      <name>" + product.getOwner().getName() + "</name>\n");
                    in.write("      <height>" + product.getOwner().getHeight() + "</height>\n");
                    in.write("      <weight>" + product.getOwner().getWeight() + "</weight>\n");
                    in.write("    </owner>\n");
                } else {
                    in.write("    <owner>null</owner>\n");
                }

                in.write("  </product>\n");
            }
            in.write("</collection>");
            logger.info("Коллекция успешно сохранена в файл " + fileName + "!");
        } catch (IOException e) {
            logger.error("Ошибка записи в файл: " + e.getMessage());
        }
    }

    public static LinkedList<Product> readCollectionFromXML(String fileName) {
        LinkedList<Product> collection = new LinkedList<>();
        if (fileName.isEmpty()) return collection;
        try (Scanner scanner = new Scanner(new File(fileName))) {
            ArrayList<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            String xmlText = String.join("", lines);
            String[] products = xmlText.split("<product>");

            for (int i = 1; i < products.length; i++) {// без нуля, т.к. там <collection>
                String block = products[i];
                try {
                    String name = getTagValue(block, "name");
                    String xStr = getTagValue(block, "x");
                    String yStr = getTagValue(block, "y");
                    Coordinates coordinates = new Coordinates(Double.parseDouble(xStr), Float.parseFloat(yStr));
                    String priceStr = getTagValue(block, "price");
                    String umStr = getTagValue(block, "unitOfMeasure");
                    long price = Long.parseLong(priceStr);
                    UnitOfMeasure unit = UnitOfMeasure.valueOf(umStr);
                    Person owner = null;
                    String ownerInText = getTagValue(block, "owner");
                    if (ownerInText != null && !ownerInText.equals("null")) {
                        String ownerName = getTagValue(ownerInText, "name");
                        int height = Integer.parseInt(getTagValue(ownerInText, "height"));
                        float weight = Float.parseFloat(getTagValue(ownerInText, "weight"));
                        owner = new Person(ownerName, height, weight);
                    }
                    Product product = new Product(name, coordinates, price, unit, owner);
//                    int id = Integer.parseInt(getTagValue(block, "id"));
//                    product.setId(id);
                    long time = Long.parseLong(getTagValue(block, "creationDate"));
                    product.setCreationDate(new Date(time));
                    collection.add(product);
                } catch (Exception e) {
                    printError("Ошибка при чтении элемента: " + e.getMessage());
                }
            }
            logger.info("Загружено объектов: " + collection.size());
        } catch (FileNotFoundException e) {
            printError("Файл не найден. Будет создана новая (пустая) коллекция.");
        } catch (Exception e) {
            printError("Ошибка чтения файла: " + e.getMessage());
        }
        return collection;
    }
}