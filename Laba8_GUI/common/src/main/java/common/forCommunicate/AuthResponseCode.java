package common.forCommunicate;

import java.io.Serial;
import java.io.Serializable;

public enum AuthResponseCode implements Serializable {
    LOGIN_OK,
    LOGIN_FAILED,
    REGISTER_OK,
    REGISTER_INVALID,
    REGISTER_USER_EXISTS;

    @Serial
    private static final long serialVersionUID = 2L;
}
