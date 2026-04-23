package client.gui;

import client.locale.LocaleManager;
import client.network.NetworkClient;
import client.theme.Theme;
import client.theme.ThemeAware;
import client.theme.ThemeManager;
import client.theme.ThemeRole;
import client.theme.ThemeStyler;
import common.data.Product;
import common.data.UnitOfMeasure;
import common.forCommunicate.CollectionInfo;
import common.forCommunicate.Response;
import common.forCommunicate.ShowData;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Главное окно приложения.
 */
public class MainFrame extends JFrame implements ThemeAware {
    private static final String[] COL_KEYS = {
            "col.id", "col.name", "col.x", "col.y", "col.date",
            "col.price", "col.unit", "col.owner", "col.created_by"
    };

    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[0][0], new String[0]) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private JTable table;
    private JTabbedPane tabs;
    private VisualizationPanel visPanel;
    private JPanel rootPanel;
    private JPanel headerPanel;
    private JPanel rightHeaderPanel;
    private JPanel tableTabPanel;
    private JPanel tableControlsPanel;
    private JPanel commandPanel;
    private JPanel statusPanel;

    private JLabel curUserLabel;
    private JLabel langLabel;
    private JLabel themeLabel;
    private JLabel syncStatusLabel;
    private JComboBox<String> langCombo;
    private JComboBox<String> themeCombo;

    private JTextField filterField;
    private JComboBox<String> filterColCombo;
    private JLabel filterLabel;
    private JLabel sortLabel;
    private JComboBox<String> sortColCombo;
    private JCheckBox sortDescBox;
    private JScrollPane tableScrollPane;
    private JScrollPane visualScrollPane;

    private final java.util.Map<String, JButton> buttons = new java.util.LinkedHashMap<>();
    private final Timer refreshTimer;
    private List<Product> currentProducts = Collections.emptyList();
    private List<Product> displayedProducts = Collections.emptyList();
    private int sortColumnIndex = 0;
    private boolean sortDescending = false;
    private boolean showRefreshErrors = false;

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null);
        buildUI();
        ThemeManager.get().registerListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ThemeManager.get().unregisterListener(MainFrame.this);
            }
        });
        applyLocale();
        applyTheme();
        refreshCollection();
        refreshTimer = new Timer(5000, e -> refreshCollection());
        refreshTimer.start();
    }

    private void buildUI() {
        rootPanel = new JPanel(new BorderLayout(5, 5));
        setContentPane(rootPanel);

        rootPanel.add(buildHeader(), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tableTabPanel = buildTableTab();
        tabs.addTab("", tableTabPanel);

        visPanel = new VisualizationPanel();
        visPanel.setOnProductClick(this::onVisualProductClick);
        visualScrollPane = new JScrollPane(visPanel);
        tabs.addTab("", visualScrollPane);
        rootPanel.add(tabs, BorderLayout.CENTER);

        rootPanel.add(buildCommandPanel(), BorderLayout.EAST);
        rootPanel.add(buildStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        curUserLabel = new JLabel();
        curUserLabel.setFont(curUserLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(curUserLabel, BorderLayout.WEST);

        rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        langLabel = new JLabel();
        langCombo = new JComboBox<>(LocaleManager.LOCALE_NAMES);
        for (int i = 0; i < LocaleManager.AVAILABLE_LOCALES.length; i++) {
            if (LocaleManager.AVAILABLE_LOCALES[i].equals(LocaleManager.get().getCurrentLocale())) {
                langCombo.setSelectedIndex(i);
                break;
            }
        }
        langCombo.addActionListener(e -> {
            LocaleManager.get().setLocale(LocaleManager.AVAILABLE_LOCALES[langCombo.getSelectedIndex()]);
            applyLocale();
            applyTheme();
        });

        themeLabel = new JLabel();
        themeCombo = new JComboBox<>();
        themeCombo.addActionListener(e -> {
            int idx = themeCombo.getSelectedIndex();
            if (idx >= 0 && idx < ThemeManager.get().getAvailableThemes().size()) {
                ThemeManager.get().setTheme(ThemeManager.get().getAvailableThemes().get(idx));
            }
        });

        JButton logoutButton = new JButton();
        logoutButton.addActionListener(e -> logout());
        buttons.put("logout", logoutButton);

        rightHeaderPanel.add(langLabel);
        rightHeaderPanel.add(langCombo);
        rightHeaderPanel.add(themeLabel);
        rightHeaderPanel.add(themeCombo);
        rightHeaderPanel.add(logoutButton);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel buildTableTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        tableControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filterLabel = new JLabel();
        filterField = new JTextField(16);
        filterColCombo = new JComboBox<>();
        sortLabel = new JLabel();
        sortColCombo = new JComboBox<>();
        sortDescBox = new JCheckBox();

        IntStream.range(0, COL_KEYS.length).forEach(i -> {
            filterColCombo.addItem("");
            sortColCombo.addItem("");
        });

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyTableView(); }
            public void removeUpdate(DocumentEvent e) { applyTableView(); }
            public void changedUpdate(DocumentEvent e) { applyTableView(); }
        });
        filterColCombo.addActionListener(e -> applyTableView());
        sortColCombo.addActionListener(e -> {
            sortColumnIndex = Math.max(0, sortColCombo.getSelectedIndex());
            applyTableView();
        });
        sortDescBox.addActionListener(e -> {
            sortDescending = sortDescBox.isSelected();
            applyTableView();
        });

        tableControlsPanel.add(filterLabel);
        tableControlsPanel.add(filterField);
        tableControlsPanel.add(filterColCombo);
        tableControlsPanel.add(sortLabel);
        tableControlsPanel.add(sortColCombo);
        tableControlsPanel.add(sortDescBox);
        panel.add(tableControlsPanel, BorderLayout.NORTH);

        rebuildTableColumns();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col < 0) {
                    return;
                }
                if (sortColumnIndex == col) {
                    sortDescending = !sortDescending;
                } else {
                    sortColumnIndex = col;
                }
                sortColCombo.setSelectedIndex(sortColumnIndex);
                sortDescBox.setSelected(sortDescending);
                applyTableView();
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onUpdate();
                }
            }
        });
        tableScrollPane = new JScrollPane(table);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCommandPanel() {
        commandPanel = new JPanel();
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
        commandPanel.setPreferredSize(new Dimension(205, 0));

        String[][] cmds = {
                {"button.refresh", "refresh"},
                {"button.help", "help"},
                {"button.add", "add"},
                {"button.update", "update"},
                {"button.delete", "delete"},
                {"button.clear", "clear"},
                {"button.info", "info"},
                {"button.reorder", "reorder"},
                {"button.remove_last", "remove_last"},
                {"button.remove_at", "remove_at"},
                {"button.filter_owner", "filter_owner"},
                {"button.count_unit", "count_unit"},
                {"button.print_desc", "print_desc"},
        };
        for (String[] cmd : cmds) {
            JButton button = new JButton();
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(170, 30));
            button.setActionCommand(cmd[1]);
            button.addActionListener(this::handleCommand);
            buttons.put(cmd[1], button);
            commandPanel.add(button);
            commandPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        return commandPanel;
    }

    private JPanel buildStatusPanel() {
        statusPanel = new JPanel(new BorderLayout());
        syncStatusLabel = new JLabel();
        statusPanel.add(syncStatusLabel, BorderLayout.WEST);
        return statusPanel;
    }

    private void applyLocale() {
        setTitle(LocaleManager.s("app.title"));
        curUserLabel.setText(LocaleManager.s("label.cur_user") + NetworkClient.get().getCurrentUser());
        langLabel.setText(LocaleManager.s("label.language"));
        themeLabel.setText(LocaleManager.s("label.theme"));

        setButtonText("refresh", "button.refresh");
        setButtonText("help", "button.help");
        setButtonText("add", "button.add");
        setButtonText("update", "button.update");
        setButtonText("delete", "button.delete");
        setButtonText("clear", "button.clear");
        setButtonText("info", "button.info");
        setButtonText("reorder", "button.reorder");
        setButtonText("remove_last", "button.remove_last");
        setButtonText("remove_at", "button.remove_at");
        setButtonText("filter_owner", "button.filter_owner");
        setButtonText("count_unit", "button.count_unit");
        setButtonText("print_desc", "button.print_desc");
        setButtonText("logout", "button.logout");

        filterLabel.setText(LocaleManager.s("filter.label"));
        sortLabel.setText(LocaleManager.s("sort.label"));
        sortDescBox.setText(LocaleManager.s("sort.desc"));

        if (tabs != null) {
            tabs.setTitleAt(0, LocaleManager.s("tab.table"));
            tabs.setTitleAt(1, LocaleManager.s("tab.visual"));
        }

        rebuildThemeCombo();
        rebuildTableColumns();
        rebuildColumnCombos();
        applyTableView();
        visPanel.repaint();
    }

    private void rebuildThemeCombo() {
        int previous = themeCombo.getSelectedIndex();
        themeCombo.removeAllItems();
        List<Theme> themes = ThemeManager.get().getAvailableThemes();
        for (Theme theme : themes) {
            themeCombo.addItem(LocaleManager.s(theme.displayNameKey()));
        }
        int selectedIndex = IntStream.range(0, themes.size())
                .filter(i -> themes.get(i).id().equals(ThemeManager.get().getTheme().id()))
                .findFirst()
                .orElse(Math.max(previous, 0));
        if (selectedIndex >= 0 && selectedIndex < themes.size()) {
            themeCombo.setSelectedIndex(selectedIndex);
        }
    }

    private void setButtonText(String id, String key) {
        JButton button = buttons.get(id);
        if (button != null) {
            button.setText(LocaleManager.s(key));
        }
    }

    private void rebuildTableColumns() {
        String[] headers = Arrays.stream(COL_KEYS).map(LocaleManager::s).toArray(String[]::new);
        tableModel.setColumnIdentifiers(headers);
    }

    private void rebuildColumnCombos() {
        int selectedFilter = Math.max(0, filterColCombo.getSelectedIndex());
        int selectedSort = Math.max(0, sortColCombo.getSelectedIndex());
        filterColCombo.removeAllItems();
        sortColCombo.removeAllItems();
        for (String key : COL_KEYS) {
            String label = LocaleManager.s(key);
            filterColCombo.addItem(label);
            sortColCombo.addItem(label);
        }
        filterColCombo.setSelectedIndex(Math.min(selectedFilter, COL_KEYS.length - 1));
        sortColCombo.setSelectedIndex(Math.min(selectedSort, COL_KEYS.length - 1));
    }

    void refreshCollection() {
        SwingWorker<Response, Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() {
                return NetworkClient.get().fetchCollectionResponse();
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response == null) {
                        onRefreshFailed();
                        return;
                    }
                    List<Product> list = extractProducts(response);
                    currentProducts = list;
                    applyTableView();
                    visPanel.setProducts(list);
                    syncStatusLabel.setText(LocaleManager.s("status.synced"));
                    showRefreshErrors = true;
                } catch (Exception ex) {
                    onRefreshFailed();
                }
            }
        };
        worker.execute();
    }

    private void onRefreshFailed() {
        syncStatusLabel.setText(LocaleManager.s("status.sync_failed"));
        if (showRefreshErrors) {
            showErrorByKey("msg.server_down");
            showRefreshErrors = false;
        }
    }

    private List<Product> extractProducts(Response response) {
        Object data = response.getData();
        if (data instanceof ShowData showData) {
            return new ArrayList<>(showData.getProducts());
        }
        return Collections.emptyList();
    }

    private void applyTableView() {
        if (filterField == null || filterColCombo == null || sortColCombo == null) {
            return;
        }
        String text = filterField.getText().trim().toLowerCase(Locale.ROOT);
        int filterColumn = Math.max(0, filterColCombo.getSelectedIndex());
        sortColumnIndex = Math.max(0, sortColCombo.getSelectedIndex());
        sortDescending = sortDescBox.isSelected();

        Stream<Product> stream = currentProducts.stream();
        if (!text.isEmpty()) {
            stream = stream.filter(product -> valueForColumn(product, filterColumn).toLowerCase(Locale.ROOT).contains(text));
        }

        Comparator<Product> comparator = Comparator.comparing(product -> (Comparable<?>) sortComparableValue(product, sortColumnIndex),
                (left, right) -> ((Comparable) left).compareTo(right));
        if (sortDescending) {
            comparator = comparator.reversed();
        }

        displayedProducts = stream.sorted(comparator).collect(Collectors.toList());
        updateTableUI(displayedProducts);
    }

    private Comparable<?> sortComparableValue(Product product, int column) {
        return switch (column) {
            case 0 -> product.getId();
            case 1 -> product.getName();
            case 2 -> product.getCoordinates().getX();
            case 3 -> product.getCoordinates().getY();
            case 4 -> product.getCreationDate();
            case 5 -> product.getPrice();
            case 6 -> product.getUnitOfMeasure() != null ? product.getUnitOfMeasure().name() : "";
            case 7 -> product.getOwner() != null ? product.getOwner().getName() : "";
            case 8 -> product.getCreatorUsername() != null ? product.getCreatorUsername() : "";
            default -> product.getId();
        };
    }

    private String valueForColumn(Product product, int column) {
        return switch (column) {
            case 0 -> String.valueOf(product.getId());
            case 1 -> product.getName();
            case 2 -> String.valueOf(product.getCoordinates().getX());
            case 3 -> String.valueOf(product.getCoordinates().getY());
            case 4 -> String.valueOf(product.getCreationDate().getTime());
            case 5 -> String.valueOf(product.getPrice());
            case 6 -> product.getUnitOfMeasure() != null ? product.getUnitOfMeasure().name() : "";
            case 7 -> product.getOwner() != null ? product.getOwner().getName() : "";
            case 8 -> product.getCreatorUsername() != null ? product.getCreatorUsername() : "";
            default -> "";
        };
    }

    private void updateTableUI(List<Product> products) {
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
    }

    private Product getSelectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= displayedProducts.size()) {
            return null;
        }
        return displayedProducts.get(row);
    }

    private void handleCommand(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "refresh" -> refreshCollection();
            case "help" -> onHelp();
            case "add" -> onAdd();
            case "update" -> onUpdate();
            case "delete" -> onDelete();
            case "clear" -> onClear();
            case "info" -> onInfo();
            case "reorder" -> sendSimple("reorder");
            case "remove_last" -> sendSimple("remove_last");
            case "remove_at" -> onRemoveAt();
            case "filter_owner" -> onFilterOwner();
            case "count_unit" -> onCountUnit();
            case "print_desc" -> onPrintDescending();
        }
    }

    private void onHelp() {
        showInfo(buildHelpText());
    }

    private void onAdd() {
        ProductDialog dlg = new ProductDialog(this, null);
        dlg.setVisible(true);
        Product product = dlg.getResult();
        if (product == null) {
            return;
        }
        exeCommand(() -> NetworkClient.get().send("add", "", product), Response::getMessage);
    }

    private void onUpdate() {
        Product sel = getSelectedProduct();
        if (sel == null) {
            showInfoByKey("msg.no_sel");
            return;
        }
        if (!Objects.equals(sel.getCreatorUsername(), NetworkClient.get().getCurrentUser())) {
            showErrorByKey("msg.not_owner");
            return;
        }
        ProductDialog dlg = new ProductDialog(this, sel);
        dlg.setVisible(true);
        Product product = dlg.getResult();
        if (product == null) {
            return;
        }
        exeCommand(() -> NetworkClient.get().send("update", String.valueOf(sel.getId()), product), Response::getMessage);
    }

    private void onDelete() {
        Product sel = getSelectedProduct();
        if (sel == null) {
            showInfoByKey("msg.no_sel");
            return;
        }
        onDeleteProduct(sel);
    }

    private void onDeleteProduct(Product product) {
        int confirm = JOptionPane.showConfirmDialog(this,
                LocaleManager.s("msg.confirm_del"), LocaleManager.s("button.delete"),
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        exeCommand(() -> NetworkClient.get().send("remove_by_id", String.valueOf(product.getId()), null), Response::getMessage);
    }

    private void onClear() {
        int confirm = JOptionPane.showConfirmDialog(this,
                LocaleManager.s("msg.confirm_clear"), LocaleManager.s("button.clear"),
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        exeCommand(() -> NetworkClient.get().send("clear", "", null), Response::getMessage);
    }

    private void onInfo() {
        exeCommand(() -> NetworkClient.get().send("info", "", null), this::formatInfoResponse);
    }

    private String formatInfoResponse(Response response) {
        if (response.getData() instanceof CollectionInfo info) {
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, LocaleManager.get().getCurrentLocale());
            NumberFormat numberFormat = NumberFormat.getIntegerInstance(LocaleManager.get().getCurrentLocale());
            return LocaleManager.s("info.template")
                    .replace("{type}", info.getCollectionType())
                    .replace("{date}", dateFormat.format(info.getCreationDate()))
                    .replace("{size}", numberFormat.format(info.getSize()));
        }
        return response.getMessage();
    }

    private void onRemoveAt() {
        String input = JOptionPane.showInputDialog(this, LocaleManager.s("dlg.idx_prompt"));
        if (input == null || input.isBlank()) {
            return;
        }
        exeCommand(() -> NetworkClient.get().send("remove_at", input.trim(), null), Response::getMessage);
    }

    private void onFilterOwner() {
        String input = JOptionPane.showInputDialog(this, LocaleManager.s("dlg.owner_prompt"));
        if (input == null || input.isBlank()) {
            return;
        }
        String ownerName = input.trim();
        exeCommand(() -> NetworkClient.get().send("filter_by_owner", ownerName, null),
                response -> formatFilterOwnerResponse(ownerName, response));
    }

    @SuppressWarnings("unchecked")
    private String formatFilterOwnerResponse(String ownerName, Response response) {
        List<Product> filtered = response.getData() instanceof List<?> list ? (List<Product>) list : Collections.emptyList();
        if (filtered.isEmpty()) {
            return LocaleManager.s("msg.owner_not_found").replace("{owner}", ownerName);
        }
        return LocaleManager.s("msg.filter_found")
                .replace("{owner}", ownerName)
                .replace("{count}", NumberFormat.getIntegerInstance(LocaleManager.get().getCurrentLocale()).format(filtered.size()))
                + "\n\n" + filtered.stream().map(Product::toString).collect(Collectors.joining("\n"));
    }

    private void onCountUnit() {
        String[] options = Arrays.stream(UnitOfMeasure.values()).map(Enum::name).toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(
                this,
                LocaleManager.s("dlg.unit_prompt"),
                LocaleManager.s("button.count_unit"),
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (chosen == null) {
            return;
        }
        exeCommand(() -> NetworkClient.get().send("count_by_unit_of_measure", chosen, null),
                response -> formatCountResponse(chosen, response));
    }

    private String formatCountResponse(String unit, Response response) {
        if (response.getData() instanceof Long count) {
            return LocaleManager.s("msg.count_unit_result")
                    .replace("{unit}", unit)
                    .replace("{count}", NumberFormat.getIntegerInstance(LocaleManager.get().getCurrentLocale()).format(count));
        }
        return response.getMessage();
    }

    @SuppressWarnings("unchecked")
    private void onPrintDescending() {
        exeCommand(() -> NetworkClient.get().send("print_field_descending_unit_of_measure", "", null),
                response -> {
                    if (response.getData() instanceof List<?> list) {
                        return LocaleManager.s("msg.print_desc_result") + "\n" + ((List<String>) list).stream().collect(Collectors.joining("\n"));
                    }
                    return response.getMessage();
                });
    }

    private void sendSimple(String cmd) {
        exeCommand(() -> NetworkClient.get().send(cmd, "", null), Response::getMessage);
    }

    private void exeCommand(Callable<Response> task, Function<Response, String> successFormatter) {
        SwingWorker<Response, Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        showInfo(successFormatter.apply(response));
                    } else {
                        showError(response.getMessage());
                    }
                    refreshCollection();
                } catch (Exception ex) {
                    showErrorByKey("msg.server_down");
                }
            }
        };
        worker.execute();
    }

    private String buildHelpText() {
        String[] keys = {
                "help.line.help", "help.line.info", "help.line.show", "help.line.add",
                "help.line.update", "help.line.remove_by_id", "help.line.clear", "help.line.remove_at",
                "help.line.remove_last", "help.line.reorder", "help.line.count_unit",
                "help.line.filter_owner", "help.line.print_desc"
        };
        return Arrays.stream(keys).map(LocaleManager::s).collect(Collectors.joining("\n"));
    }

    private void onVisualProductClick(Product product) {
        boolean isOwner = Objects.equals(product.getCreatorUsername(), NetworkClient.get().getCurrentUser());
        String[] options = isOwner
                ? new String[]{LocaleManager.s("vis.action.info"), LocaleManager.s("vis.action.edit"),
                LocaleManager.s("vis.action.delete"), LocaleManager.s("dlg.cancel")}
                : new String[]{LocaleManager.s("vis.action.info"), LocaleManager.s("dlg.cancel")};
        int choice = JOptionPane.showOptionDialog(
                this,
                LocaleManager.s("vis.action.prompt"),
                LocaleManager.s("vis.info_title"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice == 0) {
            VisualizationPanel.showProductInfo(this, product);
        } else if (isOwner && choice == 1) {
            selectProduct(product);
            onUpdate();
        } else if (isOwner && choice == 2) {
            selectProduct(product);
            onDeleteProduct(product);
        }
    }

    private void selectProduct(Product product) {
        int row = displayedProducts.indexOf(product);
        if (row >= 0) {
            table.getSelectionModel().setSelectionInterval(row, row);
            tabs.setSelectedIndex(0);
        }
    }

    private void showInfoByKey(String key) {
        showInfo(LocaleManager.s(key));
    }

    private void showErrorByKey(String key) {
        showError(LocaleManager.s(key));
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

    @Override
    public void applyTheme() {
        Theme theme = ThemeManager.get().getTheme();
        ThemeStyler.stylePanel(rootPanel, theme, ThemeRole.BACKGROUND);
        ThemeStyler.stylePanel(headerPanel, theme, ThemeRole.SURFACE);
        rightHeaderPanel.setOpaque(false);
        ThemeStyler.stylePanel(tableTabPanel, theme, ThemeRole.SURFACE);
        ThemeStyler.stylePanel(tableControlsPanel, theme, ThemeRole.SURFACE);
        ThemeStyler.stylePanel(commandPanel, theme, ThemeRole.SURFACE);
        ThemeStyler.stylePanel(statusPanel, theme, ThemeRole.SURFACE);
        ThemeStyler.styleLabel(curUserLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(langLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(themeLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(filterLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(sortLabel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(syncStatusLabel, theme, ThemeRole.MUTED_TEXT);
        ThemeStyler.styleLabelsRecursively(tableControlsPanel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleComboBox(langCombo, theme);
        ThemeStyler.styleComboBox(themeCombo, theme);
        ThemeStyler.styleTextComponent(filterField, theme);
        ThemeStyler.styleComboBox(filterColCombo, theme);
        ThemeStyler.styleComboBox(sortColCombo, theme);
        ThemeStyler.styleCheckBox(sortDescBox, theme);
        ThemeStyler.styleScrollPane(tableScrollPane, theme);
        ThemeStyler.styleScrollPane(visualScrollPane, theme);
        ThemeStyler.styleTable(table, theme);
        ThemeStyler.styleTabbedPane(tabs, theme);
        visPanel.applyTheme();

        buttons.forEach((id, button) -> ThemeStyler.styleButton(button, theme, false));
        buttons.forEach((id, button) -> ThemeStyler.refreshButtonState(button, theme, false));
        rootPanel.repaint();
    }
}
