package client.gui;

import client.locale.LocaleManager;
import client.theme.Theme;
import client.theme.ThemeAware;
import client.theme.ThemeManager;
import client.theme.ThemeRole;
import client.theme.ThemeStyler;
import common.data.Coordinates;
import common.data.Person;
import common.data.Product;
import common.data.UnitOfMeasure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Диалог добавления / редактирования продукта.
 */
public class ProductDialog extends JDialog implements ThemeAware {
    private Product result = null;

    private JTextField nameField;
    private JTextField xField;
    private JTextField yField;
    private JTextField priceField;
    private JComboBox<UnitOfMeasure> unitCombo;
    private JTextField ownerNameField;
    private JTextField ownerHeightField;
    private JTextField ownerWeightField;
    private JLabel hintLabel;
    private JPanel rootPanel;
    private JPanel formPanel;
    private JScrollPane formScrollPane;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;

    public ProductDialog(Frame parent, Product initial) {
        super(parent, true);
        setTitle(initial == null ? LocaleManager.s("dlg.add.title") : LocaleManager.s("dlg.edit.title"));
        setSize(430, 480);
        setLocationRelativeTo(parent);
        buildUI(initial);
        ThemeManager.get().registerListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ThemeManager.get().unregisterListener(ProductDialog.this);
            }
        });
        applyTheme();
    }

    private void buildUI(Product initial) {
        rootPanel = new JPanel(new BorderLayout(6, 6));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 4, 5, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0; c.gridx = 0;

        addRow(formPanel, c, 0, LocaleManager.s("dlg.name"), nameField = new JTextField(16));
        addRow(formPanel, c, 1, LocaleManager.s("dlg.x"), xField = new JTextField(16));
        addRow(formPanel, c, 2, LocaleManager.s("dlg.y"), yField = new JTextField(16));
        addRow(formPanel, c, 3, LocaleManager.s("dlg.price"), priceField = new JTextField(16));
        unitCombo = new JComboBox<>(UnitOfMeasure.values());
        addRow(formPanel, c, 4, LocaleManager.s("dlg.unit"), unitCombo);

        JSeparator sep = new JSeparator();
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2;
        c.insets = new Insets(8, 0, 4, 0);
        formPanel.add(sep, c);
        c.gridwidth = 1;
        c.insets = new Insets(4, 4, 4, 4);

        addRow(formPanel, c, 6, LocaleManager.s("dlg.owner_null"), ownerNameField = new JTextField(16));
        addRow(formPanel, c, 7, LocaleManager.s("dlg.owner_height"), ownerHeightField = new JTextField(16));
        addRow(formPanel, c, 8, LocaleManager.s("dlg.owner_weight"), ownerWeightField = new JTextField(16));

        hintLabel = new JLabel(LocaleManager.s("dlg.owner_hint"));
        hintLabel.setFont(hintLabel.getFont().deriveFont(10f));
        c.gridx = 0; c.gridy = 9; c.gridwidth = 2;
        formPanel.add(hintLabel, c);

        formScrollPane = new JScrollPane(formPanel);
        rootPanel.add(formScrollPane, BorderLayout.CENTER);

        if (initial != null) {
            nameField.setText(initial.getName());
            xField.setText(String.valueOf(initial.getCoordinates().getX()));
            yField.setText(String.valueOf(initial.getCoordinates().getY()));
            priceField.setText(String.valueOf(initial.getPrice()));
            unitCombo.setSelectedItem(initial.getUnitOfMeasure());
            if (initial.getOwner() != null) {
                ownerNameField.setText(initial.getOwner().getName());
                ownerHeightField.setText(String.valueOf(initial.getOwner().getHeight()));
                ownerWeightField.setText(String.valueOf(initial.getOwner().getWeight()));
            }
        }

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        okButton = new JButton(LocaleManager.s("dlg.ok"));
        cancelButton = new JButton(LocaleManager.s("dlg.cancel"));
        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(okButton);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; c.gridwidth = 1;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(field, c);
    }

    private void onOk() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException(LocaleManager.s("val.name_empty"));
            }

            double x = parseDouble(xField.getText().trim());
            float y = (float) parseDouble(yField.getText().trim());
            if (y <= -93) {
                throw new IllegalArgumentException(LocaleManager.s("val.y_range"));
            }
            long price = parseLong(priceField.getText().trim());
            if (price <= 0) {
                throw new IllegalArgumentException(LocaleManager.s("val.price_pos"));
            }

            UnitOfMeasure unit = (UnitOfMeasure) unitCombo.getSelectedItem();

            Person owner = null;
            String ownerName = ownerNameField.getText().trim();
            if (!ownerName.isEmpty()) {
                int height = (int) parseLong(ownerHeightField.getText().trim());
                float weight = (float) parseDouble(ownerWeightField.getText().trim());
                if (height <= 0 || weight <= 0) {
                    throw new IllegalArgumentException(LocaleManager.s("val.hw_pos"));
                }
                owner = new Person(ownerName, height, weight);
            }

            result = new Product(name, new Coordinates(x, y), price, unit, owner);
            dispose();
        } catch (NumberFormatException ex) {
            showErrorByKey("val.check_numbers");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private double parseDouble(String text) {
        try {
            Number number = NumberFormat.getNumberInstance(LocaleManager.get().getCurrentLocale()).parse(text);
            return number.doubleValue();
        } catch (ParseException e) {
            throw new NumberFormatException(text);
        }
    }

    private long parseLong(String text) {
        try {
            Number number = NumberFormat.getNumberInstance(LocaleManager.get().getCurrentLocale()).parse(text);
            return number.longValue();
        } catch (ParseException e) {
            throw new NumberFormatException(text);
        }
    }

    private void showErrorByKey(String key) {
        showError(LocaleManager.s(key));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, LocaleManager.s("msg.error"), JOptionPane.ERROR_MESSAGE);
    }

    public Product getResult() {
        return result;
    }

    @Override
    public void applyTheme() {
        Theme theme = ThemeManager.get().getTheme();
        ThemeStyler.stylePanel(rootPanel, theme, ThemeRole.DIALOG_BG);
        ThemeStyler.stylePanel(formPanel, theme, ThemeRole.DIALOG_BG);
        buttonPanel.setOpaque(false);
        ThemeStyler.styleLabelsRecursively(formPanel, theme, ThemeRole.FOREGROUND);
        ThemeStyler.styleLabel(hintLabel, theme, ThemeRole.MUTED_TEXT);
        ThemeStyler.styleScrollPane(formScrollPane, theme);
        ThemeStyler.styleTextComponent(nameField, theme);
        ThemeStyler.styleTextComponent(xField, theme);
        ThemeStyler.styleTextComponent(yField, theme);
        ThemeStyler.styleTextComponent(priceField, theme);
        ThemeStyler.styleTextComponent(ownerNameField, theme);
        ThemeStyler.styleTextComponent(ownerHeightField, theme);
        ThemeStyler.styleTextComponent(ownerWeightField, theme);
        ThemeStyler.styleComboBox(unitCombo, theme);
        ThemeStyler.styleButton(okButton, theme, true);
        ThemeStyler.styleButton(cancelButton, theme, false);
        repaint();
    }
}
