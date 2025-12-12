package ui;

import model.Product;
import service.ProductService;
import service.ProductServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.regex.Pattern;

public class InventoryUI extends JFrame {
    private JTextField txtId, txtSku, txtName, txtCategory, txtPrice, txtQuantity, txtSupplier;
    private JButton btnAdd, btnLoad, btnUpdate, btnDelete, btnClear;
    private JTable table;
    private DefaultTableModel tableModel;
    private ProductService productService;

    // Validation patterns
    private final Pattern skuPattern = Pattern.compile("^[A-Z0-9\\-]{3,20}$");
    private final Pattern namePattern = Pattern.compile("^[\\p{L}0-9 _\\-]{2,60}$");
    private final Pattern pricePattern = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    private final Pattern qtyPattern = Pattern.compile("^\\d{1,6}$");

    public InventoryUI() {
        super("Inventory Management");

        // Modern UI Look
        getContentPane().setBackground(new Color(245, 245, 245));

        productService = new ProductServiceImpl();
        initComponents();
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500)); // Responsive minimum size
    }

    private void initComponents() {

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(240, 240, 240));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtId = new JTextField(5);
        txtId.setEditable(false);

        txtSku = new JTextField(15);
        txtName = new JTextField(20);
        txtCategory = new JTextField(15);
        txtPrice = new JTextField(10);
        txtQuantity = new JTextField(10);
        txtSupplier = new JTextField(15);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; form.add(txtId, gbc);

        gbc.gridx = 2; form.add(new JLabel("SKU:"), gbc);
        gbc.gridx = 3; form.add(txtSku, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; form.add(txtName, gbc);

        gbc.gridx = 2; form.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3; form.add(txtCategory, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; form.add(txtPrice, gbc);

        gbc.gridx = 2; form.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3; form.add(txtQuantity, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Supplier:"), gbc);
        gbc.gridx = 1; form.add(txtSupplier, gbc);

        // Buttons with colors
        btnAdd = createButton("Add", new Color(46, 204, 113));
        btnLoad = createButton("Load", new Color(52, 152, 219));
        btnUpdate = createButton("Update", new Color(243, 156, 18));
        btnDelete = createButton("Delete", new Color(231, 76, 60));
        btnClear = createButton("Clear", new Color(149, 165, 166));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttons.setBackground(new Color(245, 245, 245));
        buttons.add(btnAdd);
        buttons.add(btnLoad);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        String[] cols = {"ID", "SKU", "Name", "Category", "Price", "Quantity", "Supplier"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        JScrollPane tablePane = new JScrollPane(table);

        setLayout(new BorderLayout());
        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        add(tablePane, BorderLayout.SOUTH);

        // Action listeners
        btnAdd.addActionListener(e -> onAdd());
        btnLoad.addActionListener(e -> loadAll());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) fillFormFromSelected();
            }
        });

        loadAll();
    }

    private JButton createButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(110, 40));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }

    private void onAdd() {
        try {
            Product p = collectAndValidate(false);
            if (p == null) return;
            productService.addProduct(p);
            JOptionPane.showMessageDialog(this, "Product added (ID: " + p.getId() + ")");
            loadAll();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        try {
            Product p = collectAndValidate(true);
            if (p == null) return;
            boolean ok = productService.updateProduct(p);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Product updated.");
                loadAll();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "No product updated.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + ex.getMessage());
        }
    }

    private void onDelete() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(sel, 0).toString());
        int choice = JOptionPane.showConfirmDialog(this, "Delete product ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = productService.deleteProduct(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Deleted.");
                loadAll();
            } else JOptionPane.showMessageDialog(this, "Delete failed.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
        }
    }

    private void loadAll() {
        try {
            tableModel.setRowCount(0);
            List<Product> list = productService.getAllProducts();

            for (Product p : list) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getSku(), p.getName(), p.getCategory(),
                    p.getPrice(), p.getQuantity(), p.getSupplier()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage());
        }
    }

    private void fillFormFromSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) return;

        txtId.setText(tableModel.getValueAt(sel, 0).toString());
        txtSku.setText(tableModel.getValueAt(sel, 1).toString());
        txtName.setText(tableModel.getValueAt(sel, 2).toString());
        txtCategory.setText(tableModel.getValueAt(sel, 3).toString());
        txtPrice.setText(tableModel.getValueAt(sel, 4).toString());
        txtQuantity.setText(tableModel.getValueAt(sel, 5).toString());
        txtSupplier.setText(tableModel.getValueAt(sel, 6).toString());
    }

    private void clearForm() {
        txtId.setText("");
        txtSku.setText("");
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtSupplier.setText("");
    }

    private Product collectAndValidate(boolean requireId) {
        String idStr = txtId.getText().trim();
        String sku = txtSku.getText().trim().toUpperCase();
        String name = txtName.getText().trim();
        String category = txtCategory.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String qtyStr = txtQuantity.getText().trim();
        String supplier = txtSupplier.getText().trim();

        if (requireId && idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return null;
        }

        if (!skuPattern.matcher(sku).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid SKU.");
            return null;
        }

        if (!namePattern.matcher(name).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid Name.");
            return null;
        }

        if (!pricePattern.matcher(priceStr).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid Price.");
            return null;
        }

        if (!qtyPattern.matcher(qtyStr).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity.");
            return null;
        }

        double price = Double.parseDouble(priceStr);
        int qty = Integer.parseInt(qtyStr);

        Product p = new Product(sku, name, category, price, qty, supplier);

        if (requireId) p.setId(Integer.parseInt(idStr));

        return p;
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");

            SwingUtilities.invokeLater(() -> {
                InventoryUI ui = new InventoryUI();
                ui.setVisible(true);
            });

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "SQLite JDBC driver missing.");
        }
    }
}
