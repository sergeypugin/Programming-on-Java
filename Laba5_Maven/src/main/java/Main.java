import managers.*;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CommandManager commandManager = new CommandManager(scanner);
        logger.info("========================================================");
        logger.info("          НОВЫЙ СЕАНС РАБОТЫ ПРОГРАММЫ ЗАПУЩЕН");
        logger.info("Время запуска: {}", java.time.LocalDateTime.now());
        logger.info("========================================================");
        commandManager.commandExecuter(args);
    }
}