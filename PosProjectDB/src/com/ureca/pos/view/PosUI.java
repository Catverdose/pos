package com.ureca.pos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.ureca.pos.model.dto.Book;
import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.model.service.PosService;

public class PosUI extends JFrame {
	private static final Color BG = new Color(245, 247, 250);
	private static final Color PANEL_BG = Color.WHITE;
	private static final Color TEXT = new Color(31, 41, 55);
	private static final Color SUB_TEXT = new Color(107, 114, 128);
	private static final Color PRIMARY = new Color(219, 234, 254);
	private static final Color PRIMARY_DARK = new Color(147, 197, 253);
	private static final Color DANGER = new Color(254, 226, 226);
	private static final Color DANGER_DARK = new Color(248, 113, 113);
	private static final Font BASE_FONT = new Font("Malgun Gothic", Font.PLAIN, 13);
	private static final Font BUTTON_FONT = new Font("Malgun Gothic", Font.BOLD, 14);
	private static final Font TITLE_FONT = new Font("Malgun Gothic", Font.BOLD, 22);
	private static final Font SECTION_FONT = new Font("Malgun Gothic", Font.BOLD, 15);
	private static final Dimension STARTUP_SIZE = new Dimension(1100, 800);
	private static final Dimension MINIMUM_SIZE = new Dimension(960, 700);

	private PosService service;

	private JTextField searchPhoneTf;
	private JTextField addNameTf;
	private JTextField addAddressTf;
	private JTextField addPhoneTf;
	private JTextField addPointTf;

	private JTextField stockGoodsIdTf;
	private JTextField stockAmountTf;
	private JTextField expiryGoodsIdTf;
	private JTextField productNameTf;
	private JTextField productPublisherTf;
	private JTextField productPriceTf;
	private JTextField productStockTf;
	private JTextField productExpireDateTf;
	private JTextField deleteGoodsIdTf;

	private JTextField payCustIdTf;
	private JTextField payGoodsIdTf;
	private JTextField payQuantityTf;
	private JTextField payTotalPriceTf;

	private JTextArea logTa;
	
	private JTable productTable;
	private DefaultTableModel tableModel;

	public PosUI() {
		super("상품 POS");
		setupLookAndFeel();
		buildView();
		pack();
		setMinimumSize(MINIMUM_SIZE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setModel(PosService service) {
		this.service = service;
		refreshProductTable();
	}

	public void open() {
		SwingUtilities.invokeLater(() -> setVisible(true));
	}

	private void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("Label.font", BASE_FONT);
			UIManager.put("Button.font", BASE_FONT);
			UIManager.put("TextField.font", BASE_FONT);
			UIManager.put("TextArea.font", BASE_FONT);
			UIManager.put("TabbedPane.font", BASE_FONT);
			UIManager.put("Table.font", BASE_FONT);
			UIManager.put("TableHeader.font", BASE_FONT);
		} catch (Exception e) {
		}
	}

	private void buildView() {
		JPanel root = new JPanel(new BorderLayout(0, 14));
		root.setBackground(BG);
		root.setBorder(new EmptyBorder(18, 20, 18, 20));
		root.setPreferredSize(STARTUP_SIZE);

		root.add(headerPanel(), BorderLayout.NORTH);

		JTabbedPane tabs = new JTabbedPane();
		tabs.setBackground(BG);
		tabs.addTab("회원", tabScrollPane(customerPanel()));
		tabs.addTab("상품 관리", tabScrollPane(productPanel())); // 명칭 직관화
		tabs.addTab("결제", tabScrollPane(paymentPanel()));
		root.add(tabs, BorderLayout.CENTER);

		logTa = new JTextArea(4, 20);
		logTa.setEditable(false);
		logTa.setLineWrap(true);
		logTa.setWrapStyleWord(true);
		logTa.setForeground(TEXT);
		logTa.setBackground(new Color(249, 250, 251));
		logTa.setBorder(new EmptyBorder(10, 10, 10, 10));

		JScrollPane logScroll = new JScrollPane(logTa);
		logScroll.setBorder(BorderFactory.createTitledBorder("처리 결과"));
		root.add(logScroll, BorderLayout.SOUTH);

		setContentPane(root);
	}

	private JScrollPane tabScrollPane(JPanel panel) {
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(BG);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return scrollPane;
	}

	private JPanel headerPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);

		JLabel title = new JLabel("상품 POS 관리 System");
		title.setFont(TITLE_FONT);
		title.setForeground(TEXT);

		JLabel subtitle = new JLabel("회원 조회, 상품 재고, 결제를 한 화면에서 확인합니다.");
		subtitle.setFont(BASE_FONT);
		subtitle.setForeground(SUB_TEXT);

		JPanel textPanel = new JPanel(new BorderLayout(0, 4));
		textPanel.setOpaque(false);
		textPanel.add(title, BorderLayout.NORTH);
		textPanel.add(subtitle, BorderLayout.CENTER);

		JButton clearLogBt = secondaryButton("로그 지우기");
		clearLogBt.addActionListener(e -> logTa.setText(""));

		panel.add(textPanel, BorderLayout.CENTER);
		panel.add(clearLogBt, BorderLayout.EAST);
		return panel;
	}

	private JPanel customerPanel() {
		JPanel panel = tabPanel();

		JPanel search = sectionPanel("회원 조회");
		searchPhoneTf = textField();
		JButton searchBt = primaryButton("조회");
		searchBt.addActionListener(e -> searchCustomer());
		addRow(search, 0, "전화번호", searchPhoneTf, searchBt);

		JPanel add = sectionPanel("신규 회원 등록");
		addNameTf = textField();
		addAddressTf = textField();
		addPhoneTf = textField();
		addPointTf = textField("0");
		JButton addBt = primaryButton("등록");
		addBt.addActionListener(e -> addCustomer());
		addRow(add, 0, "이름", addNameTf, null);
		addRow(add, 1, "주소", addAddressTf, null);
		addRow(add, 2, "전화번호", addPhoneTf, null);
		addRow(add, 3, "포인트", addPointTf, addBt);

		addSection(panel, search);
		addSection(panel, add);
		return panel;
	}

	private JPanel productPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 14));
		panel.setBackground(BG);
		panel.setBorder(new EmptyBorder(16, 0, 0, 0));

		JPanel addProduct = sectionPanel("신규 상품 추가");
		productNameTf = compactTextField();
		productPublisherTf = compactTextField();
		productPriceTf = compactTextField();
		productStockTf = compactTextField("0");
		productExpireDateTf = compactTextField("2026-12-31");
		JButton addProductBt = primaryButton("상품 추가");
		addProductBt.addActionListener(e -> addProduct());
		addRow(addProduct, 0, "상품명", productNameTf, null);
		addRow(addProduct, 1, "제조사", productPublisherTf, null);
		addRow(addProduct, 2, "단가", productPriceTf, null);
		addRow(addProduct, 3, "초기 재고", productStockTf, null);
		addRow(addProduct, 4, "유통기한", productExpireDateTf, addProductBt);

		JPanel stock = sectionPanel("상품 입고 및 조회");
		stockGoodsIdTf = compactTextField();
		stockAmountTf = compactTextField();
		JButton stockBt = primaryButton("재고 추가");
		stockBt.addActionListener(e -> updateStock());
		addRow(stock, 0, "상품 아이디", stockGoodsIdTf, null);
		addRow(stock, 1, "입고 수량", stockAmountTf, stockBt);

		JPanel expiry = sectionPanel("유통기한 확인");
		expiryGoodsIdTf = compactTextField();
		JButton expiryBt = primaryButton("확인");
		expiryBt.addActionListener(e -> checkExpiry());
		addRow(expiry, 0, "상품 아이디", expiryGoodsIdTf, expiryBt);

		JPanel delete = sectionPanel("상품 삭제");
		deleteGoodsIdTf = compactTextField();
		JButton deleteBt = dangerButton("삭제");
		deleteBt.addActionListener(e -> deleteProduct());
		addRow(delete, 0, "상품 아이디", deleteGoodsIdTf, deleteBt);

		JPanel sideControls = new JPanel();
		sideControls.setLayout(new BoxLayout(sideControls, BoxLayout.Y_AXIS));
		sideControls.setOpaque(false);
		prepareSideSection(stock);
		prepareSideSection(expiry);
		prepareSideSection(delete);
		sideControls.add(stock);
		sideControls.add(Box.createVerticalStrut(10));
		sideControls.add(expiry);
		sideControls.add(Box.createVerticalStrut(10));
		sideControls.add(delete);

		JPanel controls = new JPanel(new GridLayout(1, 2, 12, 0));
		controls.setOpaque(false);
		controls.add(addProduct);
		controls.add(sideControls);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(PANEL_BG);
		tablePanel.setBorder(BorderFactory.createTitledBorder("실시간 매대 상품 재고 현황"));
		
		String[] columns = {"상품 ID", "상품명(도서명)", "출판사(제조사)", "단가", "잔여 재고", "유통기한"};
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
		};
		productTable = new JTable(tableModel);
		productTable.setRowHeight(22);
		productTable.setFillsViewportHeight(true);
		productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && productTable.getSelectedRow() >= 0) {
				deleteGoodsIdTf.setText(String.valueOf(productTable.getValueAt(productTable.getSelectedRow(), 0)));
			}
		});
		JScrollPane scrollPane = new JScrollPane(productTable);
		scrollPane.setPreferredSize(new Dimension(820, 220));
		tablePanel.add(scrollPane, BorderLayout.CENTER);

		panel.add(controls, BorderLayout.NORTH);
		panel.add(tablePanel, BorderLayout.CENTER);
		return panel;
	}

	private JPanel paymentPanel() {
		JPanel panel = tabPanel();

		JPanel payment = sectionPanel("결제 처리");
		payCustIdTf = textField();
		payGoodsIdTf = textField();
		payQuantityTf = textField();
		payTotalPriceTf = textField();
		payTotalPriceTf.setEditable(false);
		payTotalPriceTf.setBackground(new Color(249, 250, 251));
		JButton payBt = primaryButton("결제");
		payBt.addActionListener(e -> processPayment());
		addRow(payment, 0, "회원 아이디", payCustIdTf, null);
		addRow(payment, 1, "상품명/아이디", payGoodsIdTf, null);
		addRow(payment, 2, "수량", payQuantityTf, null);
		addRow(payment, 3, "총 금액(자동)", payTotalPriceTf, payBt);
		installPaymentTotalCalculator();

		addSection(panel, payment);
		return panel;
	}

	private JPanel tabPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(BG);
		panel.setBorder(new EmptyBorder(16, 0, 0, 0));
		return panel;
	}

	private void addSection(JPanel parent, JPanel section) {
		section.setAlignmentX(LEFT_ALIGNMENT);
		section.setMaximumSize(new Dimension(Integer.MAX_VALUE, section.getPreferredSize().height));
		parent.add(section);
		parent.add(Box.createVerticalStrut(14));
	}

	private void prepareSideSection(JPanel section) {
		section.setAlignmentX(LEFT_ALIGNMENT);
		section.setMaximumSize(new Dimension(Integer.MAX_VALUE, section.getPreferredSize().height));
	}

	private JPanel sectionPanel(String titleText) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(PANEL_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(229, 231, 235)),
				new EmptyBorder(14, 18, 14, 18)));

		JLabel title = new JLabel(titleText);
		title.setFont(SECTION_FONT);
		title.setForeground(TEXT);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 10, 0);
		panel.add(title, gbc);

		GridBagConstraints wrapper = new GridBagConstraints();
		wrapper.gridx = 0;
		wrapper.gridy = GridBagConstraints.RELATIVE;
		wrapper.weightx = 1;
		wrapper.fill = GridBagConstraints.HORIZONTAL;
		wrapper.insets = new Insets(0, 0, 12, 0);
		panel.putClientProperty("wrapperConstraints", wrapper);
		return panel;
	}

	private void addRow(JPanel panel, int row, String label, JTextField field, JButton button) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridy = row + 1;

		JLabel labelComp = new JLabel(label);
		labelComp.setFont(BASE_FONT);
		labelComp.setForeground(SUB_TEXT);

		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(labelComp, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(field, gbc);

		gbc.gridx = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(button == null ? spacerButtonSlot() : button, gbc);
	}

	private JTextField textField() {
		return textField("");
	}

	private JTextField textField(String text) {
		return textField(text, 260);
	}

	private JTextField compactTextField() {
		return compactTextField("");
	}

	private JTextField compactTextField(String text) {
		return textField(text, 125);
	}

	private JTextField textField(String text, int width) {
		JTextField field = new JTextField(text, 20);
		field.setForeground(TEXT);
		field.setPreferredSize(new Dimension(width, 32));
		field.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(209, 213, 219)),
				new EmptyBorder(4, 8, 4, 8)));
		return field;
	}

	private JButton primaryButton(String text) {
		JButton button = new JButton(text);
		button.setFont(BUTTON_FONT);
		button.setForeground(Color.BLACK);
		button.setBackground(PRIMARY);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(104, 34));
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PRIMARY_DARK),
				new EmptyBorder(6, 12, 6, 12)));
		return button;
	}

	private JButton secondaryButton(String text) {
		JButton button = new JButton(text);
		button.setFont(BUTTON_FONT);
		button.setForeground(new Color(17, 24, 39));
		button.setBackground(Color.WHITE);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(112, 34));
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(209, 213, 219)),
				new EmptyBorder(6, 12, 6, 12)));
		return button;
	}

	private JButton dangerButton(String text) {
		JButton button = new JButton(text);
		button.setFont(BUTTON_FONT);
		button.setForeground(Color.BLACK);
		button.setBackground(DANGER);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(104, 34));
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(DANGER_DARK),
				new EmptyBorder(6, 12, 6, 12)));
		return button;
	}

	private JLabel spacerButtonSlot() {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(84, 32));
		return label;
	}

	private void refreshProductTable() {
		if (service == null || tableModel == null) return;
		try {
			tableModel.setRowCount(0); 
			List<Book> books = service.getAllProducts();
			for (Book b : books) {
				tableModel.addRow(new Object[]{
					b.getBookid(),
					b.getBookname(),  
					b.getPublisher(),
					b.getPrice(),
					b.getStock(),
					b.getExpireDate()
				});
			}
		} catch (Exception e) {
			log("대시보드 표 갱신 실패: " + e.getMessage());
		}
	}

	private void searchCustomer() {
		runSafe(() -> {
			String phone = required(searchPhoneTf, "전화번호");
			Customer customer = service.searchCustomerByPhone(phone);
			if (customer == null) {
				log("회원을 찾지 못했습니다. 전화번호=" + phone);
				return;
			}
			payCustIdTf.setText(String.valueOf(customer.getCustid()));
			log("회원 조회 성공: " + customer);
		});
	}

	private void addCustomer() {
		runSafe(() -> {
			Customer customer = new Customer(
					0,
					required(addNameTf, "이름"),
					required(addAddressTf, "주소"),
					required(addPhoneTf, "전화번호"),
					parseInt(addPointTf, "포인트"));
			service.addCustomer(customer);
			log("회원 등록 완료: " + customer.getName() + " / " + customer.getPhone());
			searchPhoneTf.setText(customer.getPhone());
		});
	}

	private void addProduct() {
		runSafe(() -> {
			Book book = new Book(
					0,
					required(productNameTf, "상품명"),
					required(productPublisherTf, "제조사"),
					parsePositiveInt(productPriceTf, "단가"),
					parseNonNegativeInt(productStockTf, "초기 재고"),
					requiredDate(productExpireDateTf, "유통기한"));

			service.addProduct(book);
			log("상품 등록 완료: 상품 아이디=" + book.getBookid() + " / " + book.getBookname() + " / 재고=" + book.getStock());
			clearProductInputs();
			refreshProductTable();
		});
	}

	private void deleteProduct() {
		runSafe(() -> {
			int goodsId = parseInt(deleteGoodsIdTf, "상품 아이디");
			Book book = service.findProductById(goodsId);
			if (book == null) {
				log("상품 삭제 취소: 상품 아이디 " + goodsId + "를 찾을 수 없습니다.");
				showError("상품을 찾을 수 없습니다.");
				return;
			}

			int orderCount = service.countProductOrders(goodsId);
			if (orderCount > 0) {
				log("상품 삭제 차단: " + productSummary(book) + " / 판매 이력 " + orderCount + "건");
				showError("판매 이력 " + orderCount + "건이 있어 결제 기록 보호를 위해 삭제할 수 없습니다.");
				return;
			}

			int answer = JOptionPane.showConfirmDialog(
					this,
					productSummary(book) + "\n\n이 상품을 삭제하면 복구할 수 없습니다.\n정말 삭제할까요?",
					"상품 삭제 확인",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (answer != JOptionPane.YES_OPTION) {
				log("상품 삭제 취소: " + productSummary(book));
				return;
			}

			service.deleteProductSafely(goodsId);
			deleteGoodsIdTf.setText("");
			log("상품 삭제 완료: " + productSummary(book));
			refreshProductTable();
		});
	}

	private void updateStock() {
		runSafe(() -> {
			int goodsId = parseInt(stockGoodsIdTf, "상품 아이디");
			int amount = parseInt(stockAmountTf, "입고 수량");
			service.updateProductStock(goodsId, amount);
			log("재고 추가 완료: 상품 아이디=" + goodsId + ", 수량=" + amount);
			
			refreshProductTable(); 
		});
	}

	private void checkExpiry() {
		runSafe(() -> {
			int goodsId = parseInt(expiryGoodsIdTf, "상품 아이디");
			boolean available = service.checkExpiry(goodsId);
			log(available
					? "판매 가능: 상품 아이디=" + goodsId
					: "판매 불가 또는 조회 실패: 상품 아이디=" + goodsId);
		});
	}

	private void installPaymentTotalCalculator() {
		DocumentListener listener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updatePaymentTotalQuietly();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updatePaymentTotalQuietly();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updatePaymentTotalQuietly();
			}
		};
		payGoodsIdTf.getDocument().addDocumentListener(listener);
		payQuantityTf.getDocument().addDocumentListener(listener);
	}

	private void updatePaymentTotalQuietly() {
		if (service == null || payTotalPriceTf == null) return;

		try {
			String productText = payGoodsIdTf.getText().trim();
			String quantityText = payQuantityTf.getText().trim();
			if (productText.isEmpty() || quantityText.isEmpty()) {
				clearPaymentTotal();
				return;
			}

			int quantity = Integer.parseInt(quantityText);
			if (quantity <= 0) {
				clearPaymentTotal();
				return;
			}

			Book product = resolvePaymentProduct(productText);
			if (product == null) {
				clearPaymentTotal();
				return;
			}

			int totalPrice = calculatePaymentTotal(product, quantity);
			payTotalPriceTf.setText(String.valueOf(totalPrice));
			payTotalPriceTf.setToolTipText(product.getBookname() + " / 단가=" + product.getPrice() + " / 재고=" + product.getStock());
		} catch (RuntimeException e) {
			clearPaymentTotal();
		}
	}

	private void clearPaymentTotal() {
		payTotalPriceTf.setText("");
		payTotalPriceTf.setToolTipText(null);
	}

	private void processPayment() {
		runSafe(() -> {
			int custId = parseInt(payCustIdTf, "회원 아이디");
			String productText = required(payGoodsIdTf, "상품명 또는 상품 아이디");
			Book product = resolvePaymentProduct(productText);
			if (product == null) {
				throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
			}

			int quantity = parsePositiveInt(payQuantityTf, "수량");
			if (quantity > product.getStock()) {
				throw new IllegalArgumentException("재고가 부족합니다. 현재 재고=" + product.getStock());
			}

			int totalPrice = calculatePaymentTotal(product, quantity);
			payTotalPriceTf.setText(String.valueOf(totalPrice));

			boolean paid = service.processPayment(custId, product.getBookid(), quantity, totalPrice);
			
			if (paid) {
				log("결제 완료: " + product.getBookname() + " / 수량=" + quantity + " / 총금액=" + totalPrice);
				refreshProductTable();
			} else {
				log("결제 실패: 회원, 상품, 재고를 확인하세요.");
			}
		});
	}

	private void runSafe(Runnable action) {
		if (service == null) {
			showError("서비스가 연결되지 않았습니다.");
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
			throw new IllegalArgumentException(label + "을 입력하세요.");
		}
		return value;
	}

	private String requiredDate(JTextField field, String label) {
		String value = required(field, label);
		try {
			LocalDate.parse(value);
			return value;
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(label + "은 yyyy-MM-dd 형식으로 입력하세요.");
		}
	}

	private int parseInt(JTextField field, String label) {
		try {
			return Integer.parseInt(required(field, label));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(label + "은 숫자로 입력하세요.");
		}
	}

	private int parsePositiveInt(JTextField field, String label) {
		int value = parseInt(field, label);
		if (value <= 0) {
			throw new IllegalArgumentException(label + "는 1 이상으로 입력하세요.");
		}
		return value;
	}

	private int parseNonNegativeInt(JTextField field, String label) {
		int value = parseInt(field, label);
		if (value < 0) {
			throw new IllegalArgumentException(label + "는 0 이상으로 입력하세요.");
		}
		return value;
	}

	private Book resolvePaymentProduct(String productText) {
		String value = productText.trim();
		if (value.isEmpty()) return null;

		Book product = null;
		try {
			product = service.findProductById(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			// 숫자가 아니면 상품명으로 조회합니다.
		}

		return product != null ? product : service.findProductByName(value);
	}

	private int calculatePaymentTotal(Book product, int quantity) {
		long total = (long) product.getPrice() * quantity;
		if (total > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("총 금액이 너무 큽니다.");
		}
		return (int) total;
	}

	private String productSummary(Book book) {
		return "상품 아이디 " + book.getBookid() + " / " + book.getBookname();
	}

	private void clearProductInputs() {
		productNameTf.setText("");
		productPublisherTf.setText("");
		productPriceTf.setText("");
		productStockTf.setText("0");
		productExpireDateTf.setText("2026-12-31");
	}

	private void log(String message) {
		logTa.append(message + System.lineSeparator());
		logTa.setCaretPosition(logTa.getDocument().getLength());
	}

	private void showError(String message) {
		String text = message == null || message.trim().isEmpty() ? "알 수 없는 오류가 발생했습니다." : message;
		log("오류: " + text);
		JOptionPane.showMessageDialog(this, text, "오류", JOptionPane.ERROR_MESSAGE);
	}
}
