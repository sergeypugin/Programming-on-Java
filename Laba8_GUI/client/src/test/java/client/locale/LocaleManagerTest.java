package client.locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocaleManagerTest {

    @AfterEach
    void resetLocale() {
        LocaleManager.get().setLocale(new Locale("ru", "RU"));
    }

    @Test
    void shouldSwitchLocalesWithoutRestart() {
        LocaleManager.get().setLocale(new Locale("pt", "BR"));
        assertEquals("Ajuda", LocaleManager.s("button.help"));

        LocaleManager.get().setLocale(new Locale("es", "CR"));
        assertEquals("Ayuda", LocaleManager.s("button.help"));
    }

    @Test
    void shouldExposeNewLocalizationKeys() {
        LocaleManager.get().setLocale(new Locale("hu", "HU"));
        assertTrue(LocaleManager.s("status.sync_failed").length() > 5);
        assertTrue(LocaleManager.s("vis.action.edit").length() > 3);
    }
}
