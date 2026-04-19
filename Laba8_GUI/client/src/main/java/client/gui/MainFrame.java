package client.gui;

import client.locale.LocaleManager;
import client.network.NetworkClient;
import common.data.Product;
import common.data.UnitOfMeasure;
import common.forCommunicate.Response;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Главное окно приложения.
 * Содержит шапку с юзером/языком, таблицу с фильтром,
 * панель визуализации и панель кнопок для всех команд.
 */
public class MainFrame extends JFrame {

    // --- Модель таблицы ---
    private static final String[] COL_KEYS = {
        "col.id","col.name","col.x","col.y","col.date",
        "col.price","col.unit","col.owner","col.created_by"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[0][0], new String[0]) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);

    private JTable table;
    private VisualizationPanel visPanel;

    // Шапка
    private JLabel curUserLabel;
    private JLabel langLabel;
    private JComboBox<String> langCombo;

    // Фильтр
    private JTextField filterField;
    private JComboBox<String> filterColCombo;
    private JLabel filterLabel;

    // Кнопки команд
    private Map<String, JButton> buttons = new LinkedHashMap<>();

    // Авторефреш
    private Timer refreshTimer;
    // Храним список продуктов
    private List<Product> currentProducts = Collections.emptyList();

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        buildUI();
        applyLocale();
        refreshCollection();
        // Авто-обновление каждые 5 секунд
        refreshTimer = new Timer(5000, e -> refreshCollection());
        refreshTimer.start();
    }

    // -------------------------------------------------------
    //  Построение UI
    // -------------------------------------------------------
    private void buildUI() {
        setLayout(new BorderLayout(5, 5));

        // ── Шапка ──
        JPanel header = buildHeader();
        add(header, BorderLayout.NORTH);

        // ── Центр: вкладки таблица / визуализация ──
        JTabbedPane tabs = new JTabbedPane();

        // Вкладка таблицы
        JPanel tableTab = buildTableTab();
        tabs.addTab(LocaleManager.s("tab.table"), tableTab);

        // Вкладка визуализации
        visPanel = new VisualizationPanel();
        visPanel.setOnProductClick(p -> VisualizationPanel.showProductInfo(this, p));
        JScrollPane visSP = new JScrollPane(visPanel);
        tabs.addTab(LocaleManager.s("tab.visual"), visSP);

        add(tabs, BorderLayout.CENTER);

        // ── Боковая панель команд ──
        JPanel commandPanel = buildCommandPanel();
        add(commandPanel, BorderLayout.EAST);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        // Левая часть: текущий пользователь
        curUserLabel = new JLabel();
        curUserLabel.setFont(curUserLabel.getFont().deriveFont(Font.BOLD));
        p.add(curUserLabel, BorderLayout.WEST);

        // Правая часть: язык + выход
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        langLabel = new JLabel();
        langCombo = new JComboBox<>(LocaleManager.LOCALE_NAMES);
        // Установить текущую локаль
        for (int i = 0; i < LocaleManager.AVAILABLE_LOCALES.length; i++) {
            if (LocaleManager.AVAILABLE_LOCALES[i].equals(LocaleManager.get().getCurrentLocale())) {
                langCombo.setSelectedIndex(i);
                break;
            }
        }
        langCombo.addActionListener(e -> {
            LocaleManager.get().setLocale(LocaleManager.AVAILABLE_LOCALES[langCombo.getSelectedIndex()]);
            applyLocale();
        });
        JButton logoutBtn = new JButton(LocaleManager.s("btn.logout"));
        logoutBtn.addActionListener(e -> logout());
        right.add(langLabel);
        right.add(langCombo);
        right.add(logoutBtn);
        buttons.put("logout", logoutBtn);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel buildTableTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Строка фильтра
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filterLabel = new JLabel(LocaleManager.s("filter.label"));
        filterField = new JTextField(20);
        filterColCombo = new JComboBox<>();
        for (String key : COL_KEYS) filterColCombo.addItem(LocaleManager.s(key));
        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        filterColCombo.addActionListener(e -> applyFilter());
        filterRow.add(filterLabel);
        filterRow.add(filterField);
        filterRow.add(filterColCombo);
        panel.add(filterRow, BorderLayout.NORTH);

        // Таблица
        rebuildTableColumns();
        table = new JTable(tableModel);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        // Двойной клик — редактирование
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) onUpdate(null);
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCommandPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setPreferredSize(new Dimension(185, 0));

        String[][] cmds = {
            {"btn.refresh",     "refresh"},
            {"btn.add",         "add"},
            {"btn.update",      "update"},
            {"btn.delete",      "delete"},
            {"btn.clear",       "clear"},
            {"btn.info",        "info"},
            {"btn.reorder",     "reorder"},
            {"btn.remove_last", "remove_last"},
            {"btn.remove_at",   "remove_at"},
            {"btn.filter_owner","filter_owner"},
            {"btn.count_unit",  "count_unit"},
            {"btn.print_desc",  "print_desc"},
        };
        for (String[] cmd : cmds) {
            JButton btn = new JButton(LocaleManager.s(cmd[0]));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(165, 30));
            btn.setActionCommand(cmd[1]);
            btn.addActionListener(this::handleCommand);
            buttons.put(cmd[1], btn);
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        return panel;
    }

    // -------------------------------------------------------
    //  Локализация (вызывается при смене языка)
    // -------------------------------------------------------
    private void applyLocale() {
        setTitle(LocaleManager.s("app.title"));
        curUserLabel.setText(LocaleManager.s("label.cur_user") + NetworkClient.get().getCurrentUser());
        langLabel.setText(LocaleManager.s("label.language"));

        // Кнопки
        String[][] keys = {
            {"refresh","btn.refresh"},{"add","btn.add"},{"update","btn.update"},
            {"delete","btn.delete"},{"clear","btn.clear"},{"info","btn.info"},
            {"reorder","btn.reorder"},{"remove_last","btn.remove_last"},
            {"remove_at","btn.remove_at"},{"filter_owner","btn.filter_owner"},
            {"count_unit","btn.count_unit"},{"print_desc","btn.print_desc"},
            {"logout","btn.logout"},
        };
        for (String[] kv : keys) {
            JButton b = buttons.get(kv[0]);
            if (b != null) b.setText(LocaleManager.s(kv[1]));
        }

        // Перестроить колонки
        rebuildTableColumns();
        // Обновить combo фильтра
        if (filterColCombo != null) {
            filterColCombo.removeAllItems();
            for (String key : COL_KEYS) filterColCombo.addItem(LocaleManager.s(key));
        }
        if (filterLabel != null) filterLabel.setText(LocaleManager.s("filter.label"));

        // Перезаполнить таблицу с новыми форматами
        populateTable(currentProducts);
        repaint();
    }

    private void rebuildTableColumns() {
        String[] headers = Arrays.stream(COL_KEYS)
                .map(LocaleManager::s)
                .toArray(String[]::new);
        tableModel.setColumnIdentifiers(headers);
    }

    // -------------------------------------------------------
    //  Обновление данных
    // -------------------------------------------------------
    void refreshCollection() {
        SwingWorker<List<Product>, Void> w = new SwingWorker<>() {
            @Override protected List<Product> doInBackground() {
                return NetworkClient.get().fetchCollection();
            }
            @Override protected void done() {
                try {
                    List<Product> list = get();
                    currentProducts = list;
                    populateTable(list);
                    visPanel.setProducts(list);
                } catch (Exception ignored) {}
            }
        };
        w.execute();
    }

    private void populateTable(List<Product> products) {
        tableModel.setRowCount(0);
        Locale locale = LocaleManager.get().getCurrentLocale();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        for (Product p : products) {
            String owner = p.getOwner() != null ? p.getOwner().getName() : "—";
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                nf.format(p.getCoordinates().getX()),
                nf.format(p.getCoordinates().getY()),
                df.format(p.getCreationDate()),
                nf.format(p.getPrice()),
                p.getUnitOfMeasure(),
                owner,
                p.getCreatorUsername() != null ? p.getCreatorUsername() : "—"
            });
        }
        applyFilter();
    }

    private void applyFilter() {
        String text = filterField.getText().trim();
        int colIdx = filterColCombo != null ? filterColCombo.getSelectedIndex() : -1;
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            String regex = "(?i)" + java.util.regex.Pattern.quote(text);
            if (colIdx >= 0) sorter.setRowFilter(RowFilter.regexFilter(regex, colIdx));
            else sorter.setRowFilter(RowFilter.regexFilter(regex));
        }
    }

    private int[] getColumnIndices() {
        int n = tableModel.getColumnCount();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        return arr;
    }

    // -------------------------------------------------------
    //  Вспомогательное: получить выбранный продукт
    // -------------------------------------------------------
    private Product getSelectedProduct() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        long id = (long) tableModel.getValueAt(modelRow, 0);
        return currentProducts.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    // -------------------------------------------------------
    //  Обработчик кнопок
    // -------------------------------------------------------
    private void handleCommand(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "refresh" -> refreshCollection();
            case "add" -> onAdd(e);
            case "update" -> onUpdate(e);
            case "delete" -> onDelete(e);
            case "clear" -> onClear(e);
            case "info" -> onInfo(e);
            case "reorder" -> sendSimple("reorder", "");
            case "remove_last" -> sendSimple("remove_last", "");
            case "remove_at" -> onRemoveAt(e);
            case "filter_owner" -> onFilterOwner(e);
            case "count_unit" -> onCountUnit(e);
            case "print_desc" -> sendSimple("print_field_descending_unit_of_measure", "");
        }
    }

    private void onAdd(ActionEvent e) {
        ProductDialog dlg = new ProductDialog(this, null);
        dlg.setVisible(true);
        Product p = dlg.getResult();
        if (p == null) return;
        exeCommand(() -> NetworkClient.get().send("add", "", p));
    }

    private void onUpdate(ActionEvent e) {
        Product sel = getSelectedProduct();
        if (sel == null) { showInfo(LocaleManager.s("msg.no_sel")); return; }
        if (!Objects.equals(sel.getCreatorUsername(), NetworkClient.get().getCurrentUser())) {
            showError(LocaleManager.s("msg.not_owner")); return;
        }
        ProductDialog dlg = new ProductDialog(this, sel);
        dlg.setVisible(true);
        Product p = dlg.getResult();
        if (p == null) return;
        exeCommand(() -> NetworkClient.get().send("update", String.valueOf(sel.getId()), p));
    }

    private void onDelete(ActionEvent e) {
        Product sel = getSelectedProduct();
        if (sel == null) { showInfo(LocaleManager.s("msg.no_sel")); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                LocaleManager.s("msg.confirm_del"), LocaleManager.s("msg.error"),
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        exeCommand(() -> NetworkClient.get().send("remove_by_id", String.valueOf(sel.getId()), null));
    }

    private void onClear(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Удалить все СВОИ объекты из коллекции?", LocaleManager.s("msg.error"),
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        exeCommand(() -> NetworkClient.get().send("clear", "", null));
    }

    private void onInfo(ActionEvent e) {
        exeCommand(() -> NetworkClient.get().send("info", "", null));
    }

    private void onRemoveAt(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this, LocaleManager.s("dlg.idx_prompt"));
        if (input == null || input.isBlank()) return;
        exeCommand(() -> NetworkClient.get().send("remove_at", input.trim(), null));
    }

    private void onFilterOwner(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this, LocaleManager.s("dlg.owner_prompt"));
        if (input == null || input.isBlank()) return;
        // Фильтруем через Streams API на клиенте
        String ownerName = input.trim();
        List<Product> filtered = currentProducts.stream()
                .filter(p -> p.getOwner() != null &&
                        p.getOwner().getName().equalsIgnoreCase(ownerName))
                .collect(Collectors.toList());
        populateTable(filtered);
        showInfo("Найдено: " + filtered.size() + " объектов с владельцем «" + ownerName + "»");
    }

    // ── Count by unit ──
    private void onCountUnit(ActionEvent e) {
        String[] options = Arrays.stream(UnitOfMeasure.values())
                .map(Enum::name).toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(this,
                LocaleManager.s("dlg.unit_prompt"), LocaleManager.s("btn.count_unit"),
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (chosen == null) return;
        exeCommand(() -> NetworkClient.get().send("count_by_unit_of_measure", chosen, null));
    }

    // ── Общий метод отправки простой команды ──
    private void sendSimple(String cmd, String arg) {
        exeCommand(() -> NetworkClient.get().send(cmd, arg, null));
    }

    /** Запускает сетевую команду в фоне, показывает результат, обновляет коллекцию */
    private void exeCommand(java.util.concurrent.Callable<Response> task) {
        SwingWorker<Response, Void> w = new SwingWorker<>() {
            @Override protected Response doInBackground() throws Exception {
                return task.call();
            }
            @Override protected void done() {
                try {
                    Response resp = get();
                    if (resp.isSuccess()) showInfo(resp.getMessage());
                    else showError(resp.getMessage());
                    refreshCollection();
                } catch (Exception ex) {
                    showError(LocaleManager.s("msg.server_down"));
                }
            }
        };
        w.execute();
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, LocaleManager.s("msg.success"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, LocaleManager.s("msg.error"), JOptionPane.ERROR_MESSAGE);
    }

    private void logout() {
        refreshTimer.stop();
        NetworkClient.get().setCurrentUser("");
        NetworkClient.get().setUserPassword("");
        new LoginFrame().setVisible(true);
        dispose();
    }
}
