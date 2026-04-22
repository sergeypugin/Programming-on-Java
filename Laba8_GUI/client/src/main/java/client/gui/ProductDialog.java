package client.gui;

import client.locale.LocaleManager;
import common.data.Coordinates;
import common.data.Person;
import common.data.Product;
import common.data.UnitOfMeasure;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Диалог добавления / редактирования продукта.
 * Возвращает Product или null (если отменили).
 */
public class ProductDialog extends JDialog {

    private Product result = null;

    private JTextField nameField;
    private JTextField xField;
    private JTextField yField;
    private JTextField priceField;
    private JComboBox<UnitOfMeasure> unitCombo;
    private JTextField ownerNameField;
    private JTextField ownerHeightField;
    private JTextField ownerWeightField;

    /**
     * @param parent  родительский фрейм
     * @param initial null - режим добавления, иначе - редактирование
     */
    public ProductDialog(Frame parent, Product initial) {
        super(parent, true);
        String title = initial == null
                ? LocaleManager.s("dlg.add.title")
                : LocaleManager.s("dlg.edit.title");
        setTitle(title);
        setSize(430, 480);
        setLocationRelativeTo(parent);
//        setResizable(false); // Мне так нравится
        buildUI(initial);
    }

    private void buildUI(Product initial) {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 4, 5, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0; c.gridx = 0;

        addRow(form, c, 0, LocaleManager.s("dlg.name"),
                nameField = new JTextField(16));
        addRow(form, c, 1, LocaleManager.s("dlg.x"),
                xField = new JTextField(16));
        addRow(form, c, 2, LocaleManager.s("dlg.y"),
                yField = new JTextField(16));
        addRow(form, c, 3, LocaleManager.s("dlg.price"),
                priceField = new JTextField(16));
        unitCombo = new JComboBox<>(UnitOfMeasure.values());
        addRow(form, c, 4, LocaleManager.s("dlg.unit"), unitCombo);

        JSeparator sep = new JSeparator();
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2;
        c.insets = new Insets(8, 0, 4, 0);
        form.add(sep, c);
        c.gridwidth = 1;
        c.insets = new Insets(4, 4, 4, 4);

        addRow(form, c, 6, LocaleManager.s("dlg.owner_null"),
                ownerNameField = new JTextField(16));
        addRow(form, c, 7, LocaleManager.s("dlg.owner_height"),
                ownerHeightField = new JTextField(16));
        addRow(form, c, 8, LocaleManager.s("dlg.owner_weight"),
                ownerWeightField = new JTextField(16));

        JLabel hint = new JLabel(LocaleManager.s("dlg.owner_hint"));
        hint.setForeground(Color.GRAY);
        hint.setFont(hint.getFont().deriveFont(10f));
        c.gridx = 0; c.gridy = 9; c.gridwidth = 2;
        form.add(hint, c);

        root.add(new JScrollPane(form), BorderLayout.CENTER);

        // Заполнить если редактирование
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

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton ok = new JButton(LocaleManager.s("dlg.ok"));
        JButton cancel = new JButton(LocaleManager.s("dlg.cancel"));
        ok.addActionListener(e -> onOk());
        cancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(ok);
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
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
            if (name.isEmpty()) throw new IllegalArgumentException(LocaleManager.s("val.name_empty"));

            double x = parseDouble(xField.getText().trim());
            float y = (float) parseDouble(yField.getText().trim());
            if (y <= -93) throw new IllegalArgumentException(LocaleManager.s("val.y_range"));
            long price = parseLong(priceField.getText().trim());
            if (price <= 0) throw new IllegalArgumentException(LocaleManager.s("val.price_pos"));

            UnitOfMeasure unit = (UnitOfMeasure) unitCombo.getSelectedItem();

            Person owner = null;
            String ownerName = ownerNameField.getText().trim();
            if (!ownerName.isEmpty()) {
                int height = (int) parseLong(ownerHeightField.getText().trim());
                float weight = (float) parseDouble(ownerWeightField.getText().trim());
                if (height <= 0 || weight <= 0)
                    throw new IllegalArgumentException(LocaleManager.s("val.hw_pos"));
                owner = new Person(ownerName, height, weight);
            }

            result = new Product(name, new Coordinates(x, y), price, unit, owner);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, LocaleManager.s("val.check_numbers"),
                    LocaleManager.s("msg.error"), JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    LocaleManager.s("msg.error"), JOptionPane.ERROR_MESSAGE);
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

    /** @return заполненный продукт или null */
    public Product getResult() {
        return result;
    }
}
