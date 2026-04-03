package server;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import common.forCommunicate.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.commands.Save;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Главный класс серверного приложения.
 * Инициализирует менеджеры, ищет последний файл коллекции и запускает сокет
 */
public class ServerMain {
    static {
        System.setProperty("log4j.configurationFile", "log4j2-server.xml");
    }

    private static final Logger logger = LogManager.getLogger(ServerMain.class);
    private static int port = 1024+500;
    private static final int BUFFER_SIZE = 65535;

    public static void main(String[] args) {
        if  (args.length > 0) {
            port=Integer.parseInt(args[0]);
            logger.info("Установлен адрес сервера из аргументов: {}", port);
        } else{
            logger.info("Порт сервера не передан, используем по умолчанию: {}", port);
        }
        logger.info("Запуск сервера...");
        String latestFileName = findLatestCollectionFile();
        CollectionManager collectionManager = new CollectionManager();
        if (latestFileName != null) {
            logger.info("Найден последний файл коллекции: {}", latestFileName);
            collectionManager.setCollection(FileManager.readCollectionFromXML(latestFileName));
        } else {
            logger.info("Старые файлы коллекции не найдены, создана пустая коллекция");
        }
        CommandManager commandManager = new CommandManager(collectionManager);

        Thread consoleThread = getThread(collectionManager);
        consoleThread.start();

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(port));
            logger.info("Сервер успешно запущен на порте {} и ожидает запросы", port);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (true) {
                try {
                    buffer.clear();
                    SocketAddress clientAddress = channel.receive(buffer);
                    logger.debug("\nПолучен новый пакет от {}:{}", clientAddress);
                    buffer.flip();
                    byte[] data = new byte[buffer.limit()];
                    buffer.get(data);
                    // Десериализация запроса
                    Request request = (Request) SerializationUtils.deserialize(data);
                    logger.info("Прочитан запрос от клиента на выполнение команды <{}>", request.getCommandName());
                    Response response = commandManager.reply(request);
                    byte[] responseData = SerializationUtils.serialize(response);

                    // Отправка ответа клиенту
                    ByteBuffer sendBuffer = ByteBuffer.wrap(responseData);
                    channel.send(sendBuffer, clientAddress);
                    logger.debug("Ответ успешно отправлен клиенту");
                } catch (Exception e) {
                    logger.error("Ошибка при обработке пакета: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка: не удалось запустить сервер c портом {}: {}", port, e.getMessage());
        }

    }
    private static Thread getThread(CollectionManager collectionManager) {
        Save save = new Save(collectionManager);
        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            logger.info("Для сохранения коллекции используйте save, для завершения работы сервера - exit");
            while (true) {
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("exit")) {
                    logger.info("Получена локальная команда exit");
                    save.execute();
                    System.exit(0);
                } else if (input.equals("save")) {
                    save.execute();
                } else {
                    logger.warn("Неизвестная команда сервера. Доступные локальные команды: save, exit");
                }
            }
        });
        consoleThread.setDaemon(true);
        return consoleThread;
    }
    /**
     * Метод для поиска самого нового файла коллекции в папке Collections
     *
     * @return путь к самому свежему файлу или null если файлов нет
     */
    private static String findLatestCollectionFile() {
        File dir = new File("Collections");
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        File[] files = dir.listFiles((d, name) -> name.startsWith("Collection_") && name.endsWith(".xml"));
        if (files == null || files.length == 0) {
            return null;
        }
        File latestFile = Arrays.stream(files)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
//        File latestFile = Arrays.stream(files)
//                .skip(1)
//                .reduce(files[0], (latest, current) ->
//                        current.lastModified() > latest.lastModified() ? current : latest);
        return latestFile.getPath();
    }
}