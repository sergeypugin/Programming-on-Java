package managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import commands.*;
import commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandManager {
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private final HashMap<String, Command> map = new HashMap<String, Command>();
    private final Console console;
    private final Scanner scanner;
    private final Set<String> someset = new HashSet<>();

    public CommandManager(Scanner scanner) {
        this.scanner = scanner;
        console = new Console(scanner);
    }

    private void mapInizialization(CollectionManager cm) {
        map.put("help", new Help());
        map.put("info", new Info(cm));
        map.put("show", new Show(cm));
        map.put("add", new Add(cm, console));
        map.put("update", new Update(cm, console));
        map.put("remove_by_id", new Remove_by_id(cm));
        map.put("clear", new Clear(cm));
        map.put("save", new Save(cm));
//        map.put("execute_script", new Execute_script());
        map.put("exit", new Exit());
        map.put("remove_at", new Remove_at(cm));
        map.put("remove_last", new Remove_last(cm));
        map.put("reorder", new Reorder(cm));
        map.put("count_by_unit_of_measure", new Count_by_unit_of_measure(cm));
        map.put("filter_by_owner", new Filter_by_owner(cm));
        map.put("print_field_descending_unit_of_measure", new Print_field_descending_unit_of_measure(cm));
    }

    public void commandExecuter(String[] args) {
        String fileName = "";
        if (args.length == 0) {
            logger.info("Имя файла не передано в аргументах.");
            logger.info("Программа запущена в интерактивном режиме.\n");
        } else {
            fileName = args[0];
            logger.info("Файл '{}' был передан программе",fileName);
        }
        CollectionManager collectionManager = new CollectionManager();
        collectionManager.setCollection(FileManager.readCollectionFromXML(fileName));
        mapInizialization(collectionManager);
        logger.info("Программа готова к работе.");
        logger.info("Для справки введите 'help'.");
        while (true) {
            console.helpfulPrint("Введите команду: ");
            executeScript(scanner.nextLine().trim());
        }
    }

    private void executeScript(String commandLine) {
        logger.debug("Введена команда: <{}>", commandLine);
        if (commandLine.isEmpty()) return;
        String[] parts = commandLine.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String arg = (parts.length == 2) ? parts[1] : "";
        boolean isScript = commandName.equals("execute_script");
        if (isScript) {
            File file = new File(arg);
            String absolutePath = file.getAbsolutePath();
            if (someset.contains(absolutePath)) {
                logger.error("Ошибка: Обнаружена рекурсия! Скрипт " + arg + " уже выполняется.");
                return;
            }
            someset.add(absolutePath);
            try (Scanner scriptScanner = new Scanner(file)) {
                Scanner oldScanner = console.getScanner();
                console.setScanner(scriptScanner);
                console.setIsHelpfulTextNeeded(false);
                while (scriptScanner.hasNextLine()) {
                    executeScript(scriptScanner.nextLine().trim());
                }
                logger.info("Выполнение скрипта " + arg + " завершено.");
                console.setScanner(oldScanner);
                console.setIsHelpfulTextNeeded(true);
            } catch (FileNotFoundException e) {
                logger.error("Ошибка при работе с файлом: " + e.getMessage());
            } finally {
                someset.remove(absolutePath);
            }
        } else executeCommand(commandName, arg);
        logger.debug("Команда завершена.");
    }

    private void executeCommand(String commandName, String arg) {
        Command commandExecuter = map.get(commandName);
        if (commandExecuter != null) {
            commandExecuter.execute(arg);
        } else {
            logger.error("Неизвестная команда " + commandName + ", введите help для справки.");
        }
    }
}