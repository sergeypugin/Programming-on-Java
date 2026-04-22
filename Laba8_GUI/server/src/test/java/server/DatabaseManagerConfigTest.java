package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseManagerConfigTest {

    @Test
    void shouldUseDefaultUrlWhenEnvIsMissing() {
        assertEquals("jdbc:postgresql://pg/studs", DatabaseManager.getDatabaseUrl(null));
    }

    @Test
    void shouldUseConfiguredUrlWhenProvided() {
        assertEquals("jdbc:postgresql://localhost:5432/custom",
                DatabaseManager.getDatabaseUrl(" jdbc:postgresql://localhost:5432/custom "));
    }
}
