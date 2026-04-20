package server;

import common.forCommunicate.Request;
import common.forCommunicate.Response;
import common.forCommunicate.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Console;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Главный класс серверного приложения.
 * <p>
 * Архитектура многопоточной обработки запросов:
 * <ul>
 *   <li>Главный поток - неблокирующий приём UDP-пакетов</li>
 *   <li>{@link ForkJoinPool} readPool - десериализация входящего пакета</li>
 *   <li>{@link ForkJoinPool} processPool - выполнение команды (с аутентификацией)</li>
 *   <li>Fixed Thread Pool sendPool  сериализация и отправка ответа клиенту</li>
 * </ul>
 * Доступ к коллекции синхронизирован через {@code synchronized} в {@link CollectionManager}.
 */
public class ServerMain {
    static {
        System.setProperty("log4j.configurationFile", "log4j2-server.xml");
    }
    private static final Logger logger = LogManager.getLogger(ServerMain.class);
    private static int port = 1024+500;
    private static final int BUFFER_SIZE = 65535;

    public static void main(String[] args) {
        if (args.length > 0) {
            port=Integer.parseInt(args[0]);
            logger.info("Установлен порт сервера из аргументов: {}", port);
        } else{
            logger.info("Порт сервера не передан, используем по умолчанию: {}", port);
        }
        Scanner sc = new Scanner(System.in);
        DatabaseManager dbManager;
        while (true) {
            System.out.print("Логин для БД: ");
//            String dbUser = sc.nextLine().trim();
            String dbUser="s365809"; //TODO to delete
            System.out.print("Пароль для БД: ");
//            String dbPassword = sc.nextLine().trim();
            String dbPassword="DQvEZD1xtSBSXNNT";//TODO to delete
            try {
                dbManager = new DatabaseManager(dbUser, dbPassword);
                break;
            } catch (SQLException e) {
                logger.error("Не удалось подключиться к БД: {}", e.getMessage());
            }
        }
        UserManager userManager = new UserManager(dbManager);
        CollectionManager collectionManager = new CollectionManager(dbManager);
        try {
            collectionManager.loadFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        CommandManager commandManager = new CommandManager(collectionManager, userManager, dbManager);

        ForkJoinPool readPool = new ForkJoinPool();
        ForkJoinPool processPool = new ForkJoinPool();
        int sendThreads = Math.max(2, Runtime.getRuntime().availableProcessors());
        ExecutorService sendPool = Executors.newFixedThreadPool(sendThreads);

        Thread consoleThread = new Thread(() -> {
            logger.info("Доступные команды сервера: exit");
            while (!Thread.currentThread().isInterrupted()) {
                String line = sc.nextLine().trim().toLowerCase();
                if (line.equals("exit")) {
                    logger.info("Сервер останавливается по команде exit");
                    readPool.shutdown();
                    processPool.shutdown();
                    sendPool.shutdown();
                    System.exit(0);
                } else {
                    logger.warn("Неизвестная команда сервера. Доступные: exit");
                }
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (true) {
                buffer.clear();
                SocketAddress clientAddress = channel.receive(buffer);
                if (clientAddress == null) {
                    Thread.sleep(5);
                    continue;
                }
                buffer.flip();
                byte[] data = new byte[buffer.limit()];
                buffer.get(data);
                final SocketAddress addr = clientAddress;
                readPool.execute(() -> {
                    try {
                        Request request = (Request) SerializationUtils.deserialize(data);
                        logger.info("Получен запрос '{}' от {} (user={})",
                                request.getCommandName(), addr, request.getUsername());
                        processPool.execute(() -> {
                            Response response = commandManager.reply(request);
                            sendPool.execute(() -> {
                                try {
                                    byte[] responseData = SerializationUtils.serialize(response);
                                    ByteBuffer sendBuf = ByteBuffer.wrap(responseData);
                                    synchronized (channel) {
                                        channel.send(sendBuf, addr);
                                    }
                                    logger.debug("Ответ отправлен клиенту {}", addr);
                                } catch (Exception e) {
                                    logger.error("Ошибка отправки ответа: {}", e.getMessage());
                                }
                            });
                        });
                    } catch (Exception e) {
                        logger.error("Ошибка десериализации пакета от {}: {}", addr, e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Критическая ошибка сервера: {}", e.getMessage());
        }
    }
}