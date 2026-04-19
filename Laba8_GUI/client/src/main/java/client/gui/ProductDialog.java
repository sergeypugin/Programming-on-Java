package client.gui;

import client.locale.LocaleManager;
import common.data.Coordinates;
import common.data.Person;
import common.data.Product;
import common.data.UnitOfMeasure;

import javax.swing.*;
import java.awt.*;

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
     * @param initial null — режим добавления, иначе — редактирование
     */
    public ProductDialog(Frame parent, Product initial) {
        super(parent, true);
        String title = initial == null
                ? LocaleManager.s("dlg.add.title")
                : LocaleManager.s("dlg.edit.title");
        setTitle(title);
        setSize(420, 460);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI(initial);
    }

    private void buildUI(Product initial) {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0; c.gridx = 0;

        // Название
        addRow(form, c, 0, LocaleManager.s("dlg.name"),
                nameField = new JTextField(16));

        // Координата X
        addRow(form, c, 1, LocaleManager.s("dlg.x"),
                xField = new JTextField(16));

        // Координата Y
        addRow(form, c, 2, LocaleManager.s("dlg.y"),
                yField = new JTextField(16));

        // Цена
        addRow(form, c, 3, LocaleManager.s("dlg.price"),
                priceField = new JTextField(16));

        // Единица измерения
        unitCombo = new JComboBox<>(UnitOfMeasure.values());
        addRow(form, c, 4, LocaleManager.s("dlg.unit"), unitCombo);

        // Владелец
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

        JLabel hint = new JLabel("(рост и вес — только если указан владелец)");
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
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton ok = new JButton(LocaleManager.s("dlg.ok"));
        JButton cancel = new JButton(LocaleManager.s("dlg.cancel"));
        ok.addActionListener(e -> onOk());
        cancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(ok);
        btnPanel.add(ok);
        btnPanel.add(cancel);
        root.add(btnPanel, BorderLayout.SOUTH);

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
            if (name.isEmpty()) throw new IllegalArgumentException("Название не может быть пустым");

            double x = Double.parseDouble(xField.getText().trim());
            float y = Float.parseFloat(yField.getText().trim());
            if (y <= -93) throw new IllegalArgumentException("Y должно быть больше -93");
            long price = Long.parseLong(priceField.getText().trim());
            if (price <= 0) throw new IllegalArgumentException("Цена должна быть > 0");

            UnitOfMeasure unit = (UnitOfMeasure) unitCombo.getSelectedItem();

            Person owner = null;
            String ownerName = ownerNameField.getText().trim();
            if (!ownerName.isEmpty()) {
                int height = Integer.parseInt(ownerHeightField.getText().trim());
                float weight = Float.parseFloat(ownerWeightField.getText().trim());
                if (height <= 0 || weight <= 0)
                    throw new IllegalArgumentException("Рост и вес должны быть > 0");
                owner = new Person(ownerName, height, weight);
            }

            result = new Product(name, new Coordinates(x, y), price, unit, owner);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Проверьте числовые поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** @return заполненный продукт или null */
    public Product getResult() {
        return result;
    }
}
