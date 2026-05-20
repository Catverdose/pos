package com.ureca.pos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
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
	private static final Font BASE_FONT = new Font("Malgun Gothic", Font.PLAIN, 13);
	private static final Font BUTTON_FONT = new Font("Malgun Gothic", Font.BOLD, 14);
	private static final Font TITLE_FONT = new Font("Malgun Gothic", Font.BOLD, 22);
	private static final Font SECTION_FONT = new Font("Malgun Gothic", Font.BOLD, 15);
	private static final Dimension STARTUP_SIZE = new Dimension(960, 760);
	private static final Dimension MINIMUM_SIZE = new Dimension(860, 680);

	private PosService service;

	private JTextField searchPhoneTf;
	private JTextField addNameTf;
	private JTextField addAddressTf;
	private JTextField addPhoneTf;
	private JTextField addPointTf;

	private JTextField stockGoodsIdTf;
	private JTextField stockAmountTf;
	private JTextField expiryGoodsIdTf;

	private JTextField payCustIdTf;
	private JTextField payGoodsIdTf;
	private JTextField payQuantityTf;
	private JTextField payTotalPriceTf;

	private JTextArea logTa;
	
	// 🌟 실시간 대시보드 표를 관리하기 위한 Swing 컴포넌트 추가
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
		// 서비스 모델이 주입되는 시점에 표 데이터를 최초 1회 로드합니다.
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
			// Keep Swing's default look and feel if the system one is unavailable.
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
		JPanel panel = tabPanel();

		JPanel stock = sectionPanel("상품 입고 및 조회");
		stockGoodsIdTf = textField();
		stockAmountTf = textField();
		JButton stockBt = primaryButton("재고 추가");
		stockBt.addActionListener(e -> updateStock());
		addRow(stock, 0, "상품 아이디", stockGoodsIdTf, null);
		addRow(stock, 1, "입고 수량", stockAmountTf, stockBt);

		JPanel expiry = sectionPanel("유통기한 확인");
		expiryGoodsIdTf = textField();
		JButton expiryBt = primaryButton("확인");
		expiryBt.addActionListener(e -> checkExpiry());
		addRow(expiry, 0, "상품 아이디", expiryGoodsIdTf, expiryBt);

		// 🌟 [핵심 보완] 실시간 전체 상품 재고 현황판 렌더링 영역 추가
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(PANEL_BG);
		tablePanel.setBorder(BorderFactory.createTitledBorder("실시간 매대 상품 재고 현황"));
		
		String[] columns = {"상품 ID", "상품명(도서명)", "출판사(제조사)", "단가", "잔여 재고", "유통기한"};
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // 더블클릭해도 표 내용이 직접 수정되지 않도록 잠금
			}
		};
		productTable = new JTable(tableModel);
		productTable.setRowHeight(22);
		productTable.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(productTable);
		scrollPane.setPreferredSize(new Dimension(820, 220));
		tablePanel.add(scrollPane, BorderLayout.CENTER);

		addSection(panel, stock);
		addSection(panel, expiry);
		
		tablePanel.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(tablePanel); // 전체 상품 레이아웃에 현황 표 투입
		return panel;
	}

	private JPanel paymentPanel() {
		JPanel panel = tabPanel();

		JPanel payment = sectionPanel("결제 처리");
		payCustIdTf = textField();
		payGoodsIdTf = textField();
		payQuantityTf = textField();
		payTotalPriceTf = textField();
		JButton payBt = primaryButton("결제");
		payBt.addActionListener(e -> processPayment());
		addRow(payment, 0, "회원 아이디", payCustIdTf, null);
		addRow(payment, 1, "상품 아이디", payGoodsIdTf, null);
		addRow(payment, 2, "수량", payQuantityTf, null);
		addRow(payment, 3, "총 금액", payTotalPriceTf, payBt);

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
		JTextField field = new JTextField(text, 20);
		field.setForeground(TEXT);
		field.setPreferredSize(new Dimension(260, 32));
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

	private JLabel spacerButtonSlot() {
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(84, 32));
		return label;
	}

	// 🌟 [추가 기능] 백엔드 데이터베이스 최신 상품 정보를 가져와 표를 갱신하는 실시간 동기화 스크립트
	private void refreshProductTable() {
		if (service == null || tableModel == null) return;
		try {
			tableModel.setRowCount(0); // 기존 테이블 열 목록 클리어
			List<Book> books = service.getAllProducts();
			for (Book b : books) {
				tableModel.addRow(new Object[]{
					b.getBookid(),
					b.getBookname(),  // Book DTO의 정의된 변수명에 맞게 매핑
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

	private void updateStock() {
		runSafe(() -> {
			int goodsId = parseInt(stockGoodsIdTf, "상품 아이디");
			int amount = parseInt(stockAmountTf, "입고 수량");
			service.updateProductStock(goodsId, amount);
			log("재고 추가 완료: 상품 아이디=" + goodsId + ", 수량=" + amount);
			
			refreshProductTable(); // 🌟 데이터 변동이 생겼으므로 표 동기화 강제 새로고침!
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

	private void processPayment() {
		runSafe(() -> {
			int custId = parseInt(payCustIdTf, "회원 아이디");
			int goodsId = parseInt(payGoodsIdTf, "상품 아이디");
			int quantity = parseInt(payQuantityTf, "수량");
			int totalPrice = parseInt(payTotalPriceTf, "총 금액");
			boolean paid = service.processPayment(custId, goodsId, quantity, totalPrice);
			
			if (paid) {
				log("결제 완료");
				refreshProductTable(); // 🌟 결제로 재고가 차감되었으므로 표 실시간 자동 갱신!
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

	private int parseInt(JTextField field, String label) {
		try {
			return Integer.parseInt(required(field, label));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(label + "은 숫자로 입력하세요.");
		}
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
