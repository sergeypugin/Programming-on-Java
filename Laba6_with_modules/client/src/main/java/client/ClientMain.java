package client;

import common.data.Product;
import common.forCommunicate.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Главный класс клиентского приложения
 * Отвечает за чтение команд, их упаковку и обмен данными с сервером по UDP
 */
public class ClientMain {
    static {
        System.setProperty("log4j.configurationFile", "log4j2-client.xml");
    }

    private static final Logger logger = LogManager.getLogger(ClientMain.class);
    private static int serverPort = 1024 + 500;
    private static String serverHost = "localhost";

    private static final Set<String> scriptHistory = new HashSet<>();
    private static Console console;
    private static DatagramChannel channel;
    private static InetSocketAddress serverAddress;

    public static void main(String[] args) {
        logger.info("Приветствуем Вас в мини-приложении StoreForYou!");
        logger.info("Хотите стать нашим поставщиком?");
        logger.info("Изучите команды интерфейса приложения и добавляйте товары!\n");
        logger.info("Для получения справки по командам введите 'help'");
        if (args.length > 0) {
            serverHost = args[0];
            logger.info("Установлен адрес сервера из аргументов: {}", serverHost);
            if (args.length > 1) {
                try {
                    serverPort = Integer.parseInt(args[1]);
                    logger.info("Установлен порт сервера из аргументов: {}", serverPort);
                } catch (NumberFormatException e) {
                    logger.error("Ошибка: порт сервера не может быть равен {}", args[1]);
                }
            } else {
                logger.info("Порт сервера не передан, используем по умолчанию: {}", serverPort);
            }
        } else {
            logger.info("Адрес сервера не передан, используем по умолчанию: {}", serverHost);
        }
        Scanner scanner = new Scanner(System.in);
        console = new Console(scanner);

        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            serverAddress = new InetSocketAddress(serverHost, serverPort);
            while (true) {
                console.helpfulPrint("Введите команду:\n$ ");
                if (!scanner.hasNextLine()) break; // Если нажмут Ctrl+D
                String input = scanner.nextLine().trim();
                processCommand(input);
            }
        } catch (IOException e) {
            logger.error("Ошибка сети при запуске клиента: " + e.getMessage());
        } finally {
            try {
                if (channel != null && channel.isOpen()) {
                    channel.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Обрабатывает введенную строку, собирает аргументы и отправляет на сервер
     *
     * @param commandLine строка введенная пользователем или прочитанная из скрипта
     */
    private static void processCommand(String commandLine) {
        if (commandLine.isEmpty()) return;
        String[] parts = commandLine.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String arg = (parts.length == 2) ? parts[1].trim() : "";
        // Локальная команда: завершение работы клиента
        if (commandName.equals("exit")) {
            logger.info("Завершение работы программы");
            System.exit(0);
        }
        // Локальная команда: выполнение скрипта
        if (commandName.equals("execute_script")) {
            executeScript(arg);
            return;
        }
        // Проверяем, требует ли команда ввода данных о продукте
        Product product = null;
        if (commandName.equals("add") || commandName.equals("update")) {
            product = console.askProduct();
            if (product == null) {
                logger.warn("Создание продукта прервано");
                return;
            }
        }

        Request request = new Request(commandName, arg, product);
        sendAndReceive(request);
    }

    /**
     * Выполняет чтение команд из указанного файла скрипта
     *
     * @param arg путь к файлу скрипта
     */
    private static void executeScript(String arg) {
        if (arg.isEmpty()) {
            logger.error("Ошибка: не указан путь к скрипту");
            return;
        }
        File file = new File(arg);
        String absolutePath = file.getAbsolutePath();
        if (scriptHistory.contains(absolutePath)) {
            logger.error("Ошибка: обнаружена рекурсия! Скрипт \"{}\" уже выполняется", arg);
            return;
        }
        scriptHistory.add(absolutePath);
        try (Scanner scriptScanner = new Scanner(file)) {
            Scanner oldScanner = console.getScanner();
            console.setScanner(scriptScanner);
            console.setIsHelpfulTextNeeded(false);
            logger.info("Начинается выполнение скрипта: " + arg);
            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine().trim();
                processCommand(line);
            }
            logger.info("Выполнение скрипта \"{}\" завершено", arg);
            console.setScanner(oldScanner);
            console.setIsHelpfulTextNeeded(true);
        } catch (FileNotFoundException e) {
            logger.error("Ошибка при работе с файлом: {}", e.getMessage());
        } finally {
            scriptHistory.remove(absolutePath);
        }
    }

    /**
     * Отправляет запрос на сервер по UDP и ждёт ответа
     *
     * @param request сформированный запрос
     */
    private static void sendAndReceive(Request request) {
        try {
            byte[] requestData = SerializationUtils.serialize(request);
            ByteBuffer sendBuffer = ByteBuffer.wrap(requestData);
            channel.send(sendBuffer, serverAddress);
            logger.debug("Запрос <{}> отправлен на сервер", request.getCommandName());
            ByteBuffer receiveBuffer = ByteBuffer.allocate(65535);
            SocketAddress receivedAddress = null;
            long startTime = System.currentTimeMillis();
            // Ожидаем ответ от сервера с таймаутом в 1 секунду
            while (receivedAddress == null && (System.currentTimeMillis() - startTime) < 1000) {
                receivedAddress = channel.receive(receiveBuffer);
                // Небольшая задержка, чтобы избежать чрезмерной загрузки ЦП при активном ожидании ответа
                Thread.sleep(10);
            }

            if (receivedAddress == null) {
                logger.error("Ошибка: сервер недоступен! Попробуйте позже или проверьте, запущен ли сервер");
                return;
            }

            byte[] responseData = new byte[receiveBuffer.position()];
            receiveBuffer.flip();
            receiveBuffer.get(responseData);

            Response response = (Response) SerializationUtils.deserialize(responseData);
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
                System.out.flush();
            } else {
                logger.error(response.getMessage());
            }

        } catch (Exception e) {
            logger.error("Ошибка при обмене данными с сервером: {}", e.getMessage());
        }
    }
}