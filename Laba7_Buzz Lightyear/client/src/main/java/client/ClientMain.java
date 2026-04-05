package client;

import common.data.Product;
import common.forCommunicate.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Главный класс клиентского приложения.
 * При запуске пользователь проходит аутентификацию (login/register).
 * Логин и пароль прикрепляются к каждому последующему запросу.
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

    private static String currentUsername = null;
    private static String currentPassword = null;

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

            if (!performAuth()) {
                logger.error("Не удалось войти в систему. Завершение работы.");
                return;
            }

            logger.info("Авторизован как '{}'. Для справки введите 'help'.", currentUsername);

            while (true) {
                console.helpfulPrint("Введите команду:\n$ ");
                if (!scanner.hasNextLine()) break;
                String input = scanner.nextLine().trim();
                processCommand(input);
            }
        } catch (IOException e) {
            logger.error("Ошибка сети при запуске клиента: {}", e.getMessage());
        } finally {
            try {
                if (channel != null && channel.isOpen()) channel.close();
            } catch (IOException ignored) {}
        }
    }

    /**
     * Проводит цикл login/register до успешного входа.
     */
    private static boolean performAuth() {
        while (true) {
            String mode = console.askAuthMode();
            String username = console.askUsername();
            String password = console.askPassword();

            Request request = new Request(mode, "", null, username, password);
            Response response = sendAndReceive(request);

            if (response == null) {
                logger.error("Ошибка: сервер недоступен! Попробуйте позже или проверьте, запущен ли сервер");
                return false;
            }

            if (response.isSuccess()) {
                currentUsername = username;
                currentPassword = password;
                return true;
            } else {
                System.out.println("Попробуйте ещё раз.\n");
            }
        }
    }

    /**
     * Обрабатывает введённую строку, собирает аргументы и отправляет на сервер.
     *
     * @param commandLine строка, введённая пользователем или прочитанная из скрипта
     */
    private static void processCommand(String commandLine) {
        if (commandLine.isEmpty()) return;

        String[] parts = commandLine.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String arg = (parts.length == 2) ? parts[1].trim() : "";

        if (commandName.equals("exit")) {
            logger.info("Завершение работы программы");
            System.exit(0);
        }

        if (commandName.equals("execute_script")) {
            executeScript(arg);
            return;
        }

        Product product = null;
        if (commandName.equals("add") || commandName.equals("update")) {
            product = console.askProduct();
            if (product == null) {
                logger.warn("Создание продукта прервано");
                return;
            }
        }

        Request request = new Request(commandName, arg, product, currentUsername, currentPassword);
        sendAndReceive(request);
    }

    /**
     * Выполняет чтение команд из указанного файла скрипта.
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
            logger.info("Начинается выполнение скрипта: {}", arg);
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
     * Отправляет запрос на сервер по UDP и ждёт ответа.
     *
     * @param request сформированный запрос
     * @return Response или null если сервер не ответил
     */
    private static Response sendAndReceive(Request request) {
        try {
            byte[] requestData = SerializationUtils.serialize(request);
            ByteBuffer sendBuffer = ByteBuffer.wrap(requestData);
            channel.send(sendBuffer, serverAddress);
            logger.debug("Запрос <{}> отправлен на сервер", request.getCommandName());

            ByteBuffer receiveBuffer = ByteBuffer.allocate(65535);
            SocketAddress receivedAddress = null;
            long startTime = System.currentTimeMillis();

            while (receivedAddress == null && (System.currentTimeMillis() - startTime) < 3000) {
                receiveBuffer.clear();
                receivedAddress = channel.receive(receiveBuffer);
                if (receivedAddress == null) Thread.sleep(10);
            }

            if (receivedAddress == null) {
                logger.error("Ошибка: сервер недоступен! Попробуйте позже или проверьте, запущен ли сервер");
                return null;
            }

            receiveBuffer.flip();
            byte[] responseData = new byte[receiveBuffer.limit()];
            receiveBuffer.get(responseData);

            Response response = (Response) SerializationUtils.deserialize(responseData);
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
                System.out.flush();
            } else {
                logger.error(response.getMessage());
            }
            return response;

        } catch (Exception e) {
            logger.error("Ошибка при обмене данными с сервером: {}", e.getMessage());
            return null;
        }
    }
}