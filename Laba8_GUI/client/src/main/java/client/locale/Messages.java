package client.locale;

import java.util.ListResourceBundle;

/**
 * Базовый (fallback) набор строк — совпадает с русским.
 */
public class Messages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Messages_ru_RU().getContents();
    }
}
