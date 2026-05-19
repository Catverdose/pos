package com.ureca.pos.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.model.service.PosService;

public class PosUI extends JFrame {
	private PosService service;

	private JTextField searchPhoneTf;
	private JTextField addNameTf;
	private JTextField addAddressTf;
	private JTextField addPhoneTf;
	private JTextField addPointTf;

	private JTextField stockBookIdTf;
	private JTextField stockAmountTf;
	private JTextField expiryBookIdTf;

	private JTextField payCustIdTf;
	private JTextField payBookIdTf;
	private JTextField payQuantityTf;
	private JTextField payTotalPriceTf;

	private JTextArea logTa;

	public PosUI() {
		super("POS DB Simple UI");
		buildView();
		setSize(720, 520);
		setMinimumSize(new Dimension(640, 460));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setModel(PosService service) {
		this.service = service;
	}

	public void open() {
		SwingUtilities.invokeLater(() -> setVisible(true));
	}

	private void buildView() {
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Customer", customerPanel());
		tabs.addTab("Product", productPanel());
		tabs.addTab("Payment", paymentPanel());

		logTa = new JTextArea();
		logTa.setEditable(false);
		logTa.setLineWrap(true);
		logTa.setWrapStyleWord(true);

		JScrollPane logScroll = new JScrollPane(logTa);
		logScroll.setPreferredSize(new Dimension(720, 130));
		logScroll.setBorder(BorderFactory.createTitledBorder("Result"));

		add(tabs, BorderLayout.CENTER);
		add(logScroll, BorderLayout.SOUTH);
	}

	private JPanel customerPanel() {
		JPanel panel = formPanel();

		searchPhoneTf = new JTextField(18);
		JButton searchBt = new JButton("Search by phone");
		searchBt.addActionListener(e -> searchCustomer());

		addNameTf = new JTextField(18);
		addAddressTf = new JTextField(18);
		addPhoneTf = new JTextField(18);
		addPointTf = new JTextField("0", 18);
		JButton addBt = new JButton("Add customer");
		addBt.addActionListener(e -> addCustomer());

		addRow(panel, 0, "Phone", searchPhoneTf, searchBt);
		addRow(panel, 1, "Name", addNameTf, null);
		addRow(panel, 2, "Address", addAddressTf, null);
		addRow(panel, 3, "New phone", addPhoneTf, null);
		addRow(panel, 4, "Point", addPointTf, addBt);
		return panel;
	}

	private JPanel productPanel() {
		JPanel panel = formPanel();

		stockBookIdTf = new JTextField(18);
		stockAmountTf = new JTextField(18);
		JButton stockBt = new JButton("Add stock");
		stockBt.addActionListener(e -> updateStock());

		expiryBookIdTf = new JTextField(18);
		JButton expiryBt = new JButton("Check expiry");
		expiryBt.addActionListener(e -> checkExpiry());

		addRow(panel, 0, "Stock book id", stockBookIdTf, null);
		addRow(panel, 1, "Stock amount", stockAmountTf, stockBt);
		addRow(panel, 2, "Expiry book id", expiryBookIdTf, expiryBt);
		return panel;
	}

	private JPanel paymentPanel() {
		JPanel panel = formPanel();

		payCustIdTf = new JTextField(18);
		payBookIdTf = new JTextField(18);
		payQuantityTf = new JTextField(18);
		payTotalPriceTf = new JTextField(18);
		JButton payBt = new JButton("Pay");
		payBt.addActionListener(e -> processPayment());

		addRow(panel, 0, "Customer id", payCustIdTf, null);
		addRow(panel, 1, "Book id", payBookIdTf, null);
		addRow(panel, 2, "Quantity", payQuantityTf, null);
		addRow(panel, 3, "Total price", payTotalPriceTf, payBt);
		return panel;
	}

	private JPanel formPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		return panel;
	}

	private void addRow(JPanel panel, int row, String label, JTextField field, JButton button) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.gridy = row;

		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel(label), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(field, gbc);

		gbc.gridx = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(button == null ? new JLabel() : button, gbc);
	}

	private void searchCustomer() {
		runSafe(() -> {
			String phone = required(searchPhoneTf, "Phone");
			Customer customer = service.searchCustomerByPhone(phone);
			if (customer == null) {
				log("No customer found. phone=" + phone);
				return;
			}
			payCustIdTf.setText(String.valueOf(customer.getCustid()));
			log("Customer: " + customer);
		});
	}

	private void addCustomer() {
		runSafe(() -> {
			Customer customer = new Customer(
					0,
					required(addNameTf, "Name"),
					required(addAddressTf, "Address"),
					required(addPhoneTf, "New phone"),
					parseInt(addPointTf, "Point"));
			service.addCustomer(customer);
			log("Customer added. phone=" + customer.getPhone());
			searchPhoneTf.setText(customer.getPhone());
		});
	}

	private void updateStock() {
		runSafe(() -> {
			int bookId = parseInt(stockBookIdTf, "Stock book id");
			int amount = parseInt(stockAmountTf, "Stock amount");
			service.updateProductStock(bookId, amount);
			log("Stock updated. bookId=" + bookId + ", amount=" + amount);
		});
	}

	private void checkExpiry() {
		runSafe(() -> {
			int bookId = parseInt(expiryBookIdTf, "Expiry book id");
			boolean available = service.checkExpiry(bookId);
			log("Book " + bookId + (available ? " is available." : " is expired or not found."));
		});
	}

	private void processPayment() {
		runSafe(() -> {
			int custId = parseInt(payCustIdTf, "Customer id");
			int bookId = parseInt(payBookIdTf, "Book id");
			int quantity = parseInt(payQuantityTf, "Quantity");
			int totalPrice = parseInt(payTotalPriceTf, "Total price");
			boolean paid = service.processPayment(custId, bookId, quantity, totalPrice);
			log(paid ? "Payment completed." : "Payment failed.");
		});
	}

	private void runSafe(Runnable action) {
		if (service == null) {
			showError("Service is not connected.");
			return;
		}
		try {
			action.run();
		} catch (RuntimeException e) {
			showError(e.getMessage());
		}
	}

	private String required(JTextField field, String label) {
		String value = field.getText().trim();
		if (value.isEmpty()) {
			throw new IllegalArgumentException(label + " is required.");
		}
		return value;
	}

	private int parseInt(JTextField field, String label) {
		try {
			return Integer.parseInt(required(field, label));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(label + " must be a number.");
		}
	}

	private void log(String message) {
		logTa.append(message + System.lineSeparator());
		logTa.setCaretPosition(logTa.getDocument().getLength());
	}

	private void showError(String message) {
		String text = message == null || message.trim().isEmpty() ? "Unknown error." : message;
		log("ERROR: " + text);
		JOptionPane.showMessageDialog(this, text, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
