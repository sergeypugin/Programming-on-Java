package client.locale;

import java.util.ListResourceBundle;

/**
 * Базовый набор строк - совпадает с русским.
 */
public class Messages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Messages_ru_RU().getContents();
    }
}
