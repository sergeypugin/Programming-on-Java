package client.network;

import common.data.Product;
import common.forCommunicate.Request;
import common.forCommunicate.Response;
import common.forCommunicate.SerializationUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.List;

/**
 * Singleton-клиент для связи с сервером по UDP.
 * Все запросы из GUI идут через этот класс.
 */
public class NetworkClient {
    private static final int TIMEOUT_MS = 2000;
    private static NetworkClient instance;

    private final String host;
    private final int port;
    private String currentUser = "";
    private String userPassword = "";

    private NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void init(String host, int port) {
        instance = new NetworkClient(host, port);
    }

    public static NetworkClient get() {
        if (instance == null) throw new IllegalStateException("NetworkClient not initialized");
        return instance;
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }
    public String getCurrentUser() {
        return currentUser;
    }

    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Проверка доступности сервера (ping).
     */
    public boolean isServerAvailable() {
        try {
            Response r = sendRaw(new Request("ping"));
            return r != null && r.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Отправить запрос, получить ответ. Кидает RuntimeException если недоступен.
     */
    public Response send(String command, String argument, Product product) {
        Request req = new Request(command, argument, product, currentUser, userPassword);
        Response resp = sendRaw(req);
        if (resp == null) throw new RuntimeException("Сервер недоступен");
        return resp;
    }

    /** Для login/register — явно передаём username и password */
    public Response sendAuth(String command, String username, String password) {
        Request req = new Request(command, null, null, username, password);
        Response resp = sendRaw(req);
        if (resp == null) throw new RuntimeException("Сервер недоступен");
        return resp;
    }

    @SuppressWarnings("unchecked")
    public List<Product> fetchCollection() {
        try {
            Response resp = send("show", "", null);
            if (resp.getData() instanceof List) {
                return (List<Product>) resp.getData();
            }
        } catch (Exception e) {
            // ignore
        }
        return Collections.emptyList();
    }

    private Response sendRaw(Request request) {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            InetSocketAddress serverAddr = new InetSocketAddress(host, port);
            byte[] data = SerializationUtils.serialize(request);
            channel.send(ByteBuffer.wrap(data), serverAddr);

            ByteBuffer buf = ByteBuffer.allocate(65535);
            long start = System.currentTimeMillis();
            SocketAddress received = null;
            while (received == null && System.currentTimeMillis() - start < TIMEOUT_MS) {
                received = channel.receive(buf);
                if (received == null) Thread.sleep(10);
            }
            if (received == null) return null;

            byte[] respData = new byte[buf.position()];
            buf.flip();
            buf.get(respData);
            return (Response) SerializationUtils.deserialize(respData);
        } catch (Exception e) {
            return null;
        }
    }
}
