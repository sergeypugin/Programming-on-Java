package client.gui;

import client.locale.LocaleManager;
import common.data.Product;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.text.DateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Панель визуализации коллекции.
 *
 * Каждый продукт — цветной кружок, радиус ∝ цене.
 * Разные владельцы (createdBy) — разные цвета.
 * Анимация: при появлении объект «вырастает» из центра.
 * Клик на объект → всплывающая информация.
 */
public class VisualizationPanel extends JPanel {

    // Состояние анимации для каждого продукта: id → прогресс [0..1]
    private final Map<Long, Float> animProgress = new HashMap<>();
    private final Map<String, Color> userColors = new HashMap<>();
    private static final Color[] PALETTE = {
        new Color(70, 130, 180),  // steel blue
        new Color(220, 80,  80),  // red
        new Color(80,  180, 80),  // green
        new Color(230, 160, 40),  // orange
        new Color(150, 80,  200), // purple
        new Color(40,  190, 190), // teal
        new Color(220, 100, 160), // pink
    };

    private List<Product> products = Collections.emptyList();
    private Consumer<Product> onProductClick; // callback для отображения инфо
    private Timer animTimer;

    // Маппинг id → целевой радиус (чтобы не пересчитывать)
    private final Map<Long, Integer> radii = new HashMap<>();

    public VisualizationPanel() {
        setBackground(new Color(245, 248, 252));
        setPreferredSize(new Dimension(600, 500));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
        // Анимационный таймер ~60 fps
        animTimer = new Timer(16, e -> {
            boolean anyUpdated = false;
            for (long id : new HashSet<>(animProgress.keySet())) {
                float p = animProgress.get(id);
                if (p < 1f) {
                    animProgress.put(id, Math.min(1f, p + 0.04f));
                    anyUpdated = true;
                }
            }
            if (anyUpdated) repaint();
        });
        animTimer.start();
    }

    public void setOnProductClick(Consumer<Product> cb) {
        this.onProductClick = cb;
    }

    /** Обновить список продуктов (вызывать из EDT). */
    public void setProducts(List<Product> newProducts) {
        // Запустить анимацию для новых объектов
        Set<Long> newIds = new HashSet<>();
        for (Product p : newProducts) {
            newIds.add(p.getId());
            animProgress.putIfAbsent(p.getId(), 0f);
        }
        // Удалить старые записи анимации
        animProgress.keySet().retainAll(newIds);
        radii.clear();
        for (Product p : newProducts) {
            radii.put(p.getId(), computeRadius(p));
        }
        this.products = new ArrayList<>(newProducts);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();

        // Оси
        g2.setColor(new Color(200, 210, 220));
        g2.drawLine(W / 2, 0, W / 2, H);
        g2.drawLine(0, H / 2, W, H / 2);
        g2.setColor(new Color(180, 190, 200));
        g2.setFont(g2.getFont().deriveFont(10f));
        g2.drawString("X", W - 15, H / 2 - 5);
        g2.drawString("Y", W / 2 + 5, 15);

        for (Product p : products) {
            drawProduct(g2, p, W, H);
        }
    }

    private void drawProduct(Graphics2D g2, Product p, int W, int H) {
        // Координаты: масштабируем в пиксели
        int cx = W / 2 + (int)(p.getCoordinates().getX() * 2);
        int cy = H / 2 - (int)(p.getCoordinates().getY() * 2);
        // Ограничиваем экраном
        cx = Math.max(30, Math.min(W - 30, cx));
        cy = Math.max(30, Math.min(H - 30, cy));

        int targetR = radii.getOrDefault(p.getId(), 20);
        float progress = animProgress.getOrDefault(p.getId(), 1f);
        // Easing: smooth step
        progress = progress * progress * (3 - 2 * progress);
        int r = Math.max(4, (int)(targetR * progress));

        Color base = getColorForUser(p.getCreatorUsername());
        // Заливка с прозрачностью
        g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 180));
        g2.fill(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
        // Контур
        g2.setColor(base.darker());
        g2.setStroke(new BasicStroke(2));
        g2.draw(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));

        // Название, если радиус достаточно большой
        if (r > 18) {
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
            FontMetrics fm = g2.getFontMetrics();
            String label = p.getName().length() > 8 ? p.getName().substring(0, 8) : p.getName();
            g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2 - 1);
        } else if (r > 8) {
            // Хотя бы id
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(9f));
            String label = String.valueOf(p.getId());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2 - 1);
        }
    }

    private void handleClick(int mx, int my) {
        int W = getWidth(), H = getHeight();
        // Обходим в обратном порядке — верхний слой первым
        for (int i = products.size() - 1; i >= 0; i--) {
            Product p = products.get(i);
            int cx = W / 2 + (int)(p.getCoordinates().getX() * 2);
            int cy = H / 2 - (int)(p.getCoordinates().getY() * 2);
            cx = Math.max(30, Math.min(W - 30, cx));
            cy = Math.max(30, Math.min(H - 30, cy));
            int r = radii.getOrDefault(p.getId(), 20);
            double dist = Math.hypot(mx - cx, my - cy);
            if (dist <= r) {
                if (onProductClick != null) onProductClick.accept(p);
                return;
            }
        }
    }

    private int computeRadius(Product p) {
        // Радиус от 10 до 50 в зависимости от цены (log-шкала)
        double logPrice = Math.log10(Math.max(1, p.getPrice()));
        return Math.max(10, Math.min(50, (int)(logPrice * 10)));
    }

    private Color getColorForUser(String user) {
        if (user == null) user = "unknown";
        return userColors.computeIfAbsent(user, u -> {
            int idx = Math.abs(u.hashCode()) % PALETTE.length;
            return PALETTE[idx];
        });
    }

    /** Показать popup с информацией об объекте (вызывается при клике) */
    public static void showProductInfo(Component parent, Product p) {
        DateFormat df = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.SHORT,
                LocaleManager.get().getCurrentLocale());
        String info = "<html><b>ID:</b> " + p.getId() +
                "<br><b>Название:</b> " + p.getName() +
                "<br><b>Координаты:</b> (" + p.getCoordinates().getX() + ", " + p.getCoordinates().getY() + ")" +
                "<br><b>Цена:</b> " + p.getPrice() +
                "<br><b>Ед.изм.:</b> " + p.getUnitOfMeasure() +
                "<br><b>Владелец:</b> " + (p.getOwner() != null ? p.getOwner().getName() : "—") +
                "<br><b>Дата:</b> " + df.format(p.getCreationDate()) +
                "<br><b>Автор:</b> " + (p.getCreatorUsername() != null ? p.getCreatorUsername() : "—") +
                "</html>";
        JOptionPane.showMessageDialog(parent, info,
                LocaleManager.s("vis.info_title"), JOptionPane.INFORMATION_MESSAGE);
    }
}
