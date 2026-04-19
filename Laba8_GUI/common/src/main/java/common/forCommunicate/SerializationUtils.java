package common.forCommunicate;

import java.io.*;

/**
 * Класс-утилита для перевода объектов в байты и обратно.
 */
public class SerializationUtils {

    /**
     * Сериализует объект в массив байтов.
     *
     * @param obj объект для сериализации
     * @return массив байтов (готовый к отправке по сети)
     * @throws IOException если произошла ошибка при записи
     */
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(stream);
        objStream.writeObject(obj);
        objStream.flush();
        return stream.toByteArray();
    }

    /**
     * Десериализует массив байтов обратно в Java-объект.
     *
     * @param data массив байтов, полученный из сети
     * @return восстановленный объект
     * @throws IOException если массив поврежден
     * @throws ClassNotFoundException если мы получили байты класса, который не существует
     */
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream array = new ByteArrayInputStream(data);
        ObjectInputStream obj = new ObjectInputStream(array);
        return obj.readObject();
    }
}