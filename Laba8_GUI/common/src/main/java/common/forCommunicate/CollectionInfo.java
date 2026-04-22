package common.forCommunicate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Структурированная информация о коллекции для клиента
 */
public class CollectionInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private final String collectionType;
    private final Date creationDate;
    private final long size;

    public CollectionInfo(String collectionType, Date creationDate, long size) {
        this.collectionType = collectionType;
        this.creationDate = creationDate;
        this.size = size;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getSize() {
        return size;
    }
}
