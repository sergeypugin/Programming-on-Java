package client.gui;

import client.locale.LocaleManager;
import client.theme.Theme;
import client.theme.ThemeAware;
import client.theme.ThemeManager;
import client.theme.ThemeRole;
import common.data.Product;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Панель визуализации коллекции.
 */
public class VisualizationPanel extends JPanel implements ThemeAware {
    private final Map<Long, Float> animProgress = new HashMap<>();
    private final Map<String, Color> userColors = new HashMap<>();
    private final Map<Long, Integer> radii = new HashMap<>();
    private List<Product> products = Collections.emptyList();
    private Consumer<Product> onProductClick;

    public VisualizationPanel() {
        setPreferredSize(new Dimension(600, 600));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
        Timer animTimer = new Timer(200, e -> {
            boolean anyUpdated = false;
            for (long id : new HashSet<>(animProgress.keySet())) {
                float p = animProgress.get(id);
                if (p < 1f) {
                    animProgress.put(id, Math.min(1f, p + 0.04f));
                    anyUpdated = true;
                }
            }
            if (anyUpdated) {
                repaint();
            }
        });
        animTimer.start();
        applyTheme();
    }

    public void setOnProductClick(Consumer<Product> cb) {
        this.onProductClick = cb;
    }

    public void setProducts(List<Product> newProducts) {
        Set<Long> newIds = new HashSet<>();
        for (Product p : newProducts) {
            newIds.add(p.getId());
            animProgress.putIfAbsent(p.getId(), 0f);
        }
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

        int width = getWidth();
        int height = getHeight();
        Theme theme = ThemeManager.get().getTheme();

        g2.setColor(theme.color(ThemeRole.VIS_AXIS));
        g2.drawLine(width / 2, 0, width / 2, height);
        g2.drawLine(0, height / 2, width, height / 2);
        g2.setColor(theme.color(ThemeRole.VIS_LABEL));
        g2.setFont(g2.getFont().deriveFont(10f));
        g2.drawString("X", width - 15, height / 2 - 5);
        g2.drawString("Y", width / 2 + 5, 15);

        for (Product product : products) {
            drawProduct(g2, product, width, height, theme);
        }
    }

    private void drawProduct(Graphics2D g2, Product product, int width, int height, Theme theme) {
        int cx = width / 2 + (int) (product.getCoordinates().getX() * 2);
        int cy = height / 2 - (int) (product.getCoordinates().getY() * 2);
        cx = Math.max(30, Math.min(width - 30, cx));
        cy = Math.max(30, Math.min(height - 30, cy));

        int targetR = radii.getOrDefault(product.getId(), 20);
        float progress = animProgress.getOrDefault(product.getId(), 1f);
        progress = progress * progress;
        int r = Math.max(4, (int) (targetR * progress));

        Color base = getColorForUser(product.getCreatorUsername());
        g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 180));
        g2.fill(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));

        g2.setColor(base.darker());
        g2.setStroke(new BasicStroke(2));
        g2.draw(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));

        if (r > 18) {
            g2.setColor(theme.color(ThemeRole.VIS_NODE_TEXT));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
            FontMetrics fm = g2.getFontMetrics();
            String label = product.getName().length() > 8 ? product.getName().substring(0, 8) : product.getName();
            g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2 - 1);
        } else if (r > 8) {
            g2.setColor(theme.color(ThemeRole.VIS_NODE_TEXT));
            g2.setFont(g2.getFont().deriveFont(9f));
            String label = String.valueOf(product.getId());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2 - 1);
        }
    }

    private void handleClick(int mx, int my) {
        int width = getWidth();
        int height = getHeight();
        for (int i = products.size() - 1; i >= 0; i--) {
            Product product = products.get(i);
            int cx = width / 2 + (int) (product.getCoordinates().getX() * 2);
            int cy = height / 2 - (int) (product.getCoordinates().getY() * 2);
            cx = Math.max(30, Math.min(width - 30, cx));
            cy = Math.max(30, Math.min(height - 30, cy));
            int r = radii.getOrDefault(product.getId(), 20);
            if (Math.hypot(mx - cx, my - cy) <= r) {
                if (onProductClick != null) {
                    onProductClick.accept(product);
                }
                return;
            }
        }
    }

    private int computeRadius(Product p) {
        double logPrice = Math.log10(Math.max(1, p.getPrice()));
        return Math.max(10, Math.min(50, (int) (logPrice * 10)));
    }

    private Color getColorForUser(String user) {
        if (user == null) {
            user = "unknown";
        }
        Theme theme = ThemeManager.get().getTheme();
        List<Color> palette = theme.visualizationPalette();
        return userColors.computeIfAbsent(user, u -> {
            int idx = Math.abs(u.hashCode()) % palette.size();
            return palette.get(idx);
        });
    }

    public static void showProductInfo(Component parent, Product p) {
        DateFormat df = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.MEDIUM,
                LocaleManager.get().getCurrentLocale()
        );
        NumberFormat nf = NumberFormat.getNumberInstance(LocaleManager.get().getCurrentLocale());
        String info = "<html><b>ID:</b> " + p.getId() +
                "<br><b>" + LocaleManager.s("col.name") + "</b> " + p.getName() +
                "<br><b>" + LocaleManager.s("col.x") + " / " + LocaleManager.s("col.y") + ":</b> (" +
                nf.format(p.getCoordinates().getX()) + ", " + nf.format(p.getCoordinates().getY()) + ")" +
                "<br><b>" + LocaleManager.s("col.price") + ":</b> " + nf.format(p.getPrice()) +
                "<br><b>" + LocaleManager.s("col.unit") + ":</b> " + p.getUnitOfMeasure() +
                "<br><b>" + LocaleManager.s("col.owner") + ":</b> " +
                (p.getOwner() != null ? p.getOwner().getName() : "—") +
                "<br><b>" + LocaleManager.s("col.date") + ":</b> " + df.format(p.getCreationDate()) +
                "<br><b>" + LocaleManager.s("col.created_by") + ":</b> " +
                (p.getCreatorUsername() != null ? p.getCreatorUsername() : "—") +
                "</html>";
        JOptionPane.showMessageDialog(parent, info,
                LocaleManager.s("vis.info_title"), JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void applyTheme() {
        Theme theme = ThemeManager.get().getTheme();
        userColors.clear();
        setBackground(theme.color(ThemeRole.SURFACE));
        repaint();
    }
}
