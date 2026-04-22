package client.locale;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton-менеджер локализации - хранит текущую локаль и предоставляет строки.
 */
public class LocaleManager {
    private static LocaleManager instance;
    private Locale currentLocale;
    private ResourceBundle bundle;


    public static final Locale[] AVAILABLE_LOCALES = {
            new Locale("ru", "RU"),
            new Locale("pt", "BR"),
            new Locale("hu", "HU"),
            new Locale("es", "CR")
    };

    public static final String[] LOCALE_NAMES = {
            "Русский", "Português", "Magyar", "Español (CR)"
    };

    private LocaleManager() {
        setLocale(AVAILABLE_LOCALES[0]);
    }

    public static LocaleManager get() {
        if (instance == null) instance = new LocaleManager();
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("client.locale.Messages", locale);
    }

    public Locale getCurrentLocale() { return currentLocale; }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "?" + key + "?";
        }
    }

    /** Удобный алиас */
    public static String s(String key) {
        return get().getString(key);
    }
}
