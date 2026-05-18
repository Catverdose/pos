package com.ureca.employee.view;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.ureca.employee.model.dto.Employee;
import com.ureca.employee.model.dto.Engineer;
import com.ureca.employee.model.dto.Manager;
import com.ureca.employee.model.service.EmployeeService;

public class EmployeeUI {
	private static final int EMP_STATE=1;
	private static final int MGR_STATE=2;
	private static final int ENG_STATE=3;
	private JFrame main;
	private MessageDialog  dialog;
	private JButton insertBt, updateBt, deleteBt, findBt
					, exitBt, sendBt;
	private JLabel addInfoL;
	private JTextField empnoTf, nameTf, salaryTf, addInfoTf;
	private Checkbox empCheck, mgrCheck, engCheck;
	private JTable  empTable;
	private DefaultTableModel empModel;
	private JScrollPane empPan;
	private JPanel addInfoPan;
	private String[] header ={"사원번호","사원이름","급여",""};
	private EmployeeService  model;
	
	private ActionListener  buttonHandler = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			try {
				if(src == insertBt) {
					insert();
				}else if (src == findBt) {
					findEmployee();
				}else if (src == deleteBt) {
					delete();
				}else if (src == updateBt) {
					update();
				}else if (src == exitBt) {
					model.close();
				} else {
				}
			} catch (NumberFormatException  err) {
				dialog.show("급여는 정수로 입력해 주세요");
			} catch (Exception  err) {
				err.printStackTrace();
				dialog.show(err.getMessage());
			}	
		}
	};
	private ItemListener checkHandler = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED){
				Object source = e.getSource();
				if(source==empCheck)showType(EMP_STATE);
				else if(source==mgrCheck)showType(MGR_STATE);
				else if(source==engCheck)showType(ENG_STATE);
			}
		}
	};
	private MouseAdapter listHandler = new MouseAdapter() {
		public void mouseClicked(java.awt.event.MouseEvent e) {
			String empno = (String)empTable.getValueAt(empTable.getSelectedRow(), 0);
			System.out.println(empno);
			empnoTf.setText(empno);
		};
	};
	public void findEmployee(){
		Employee  emp = model.search(empnoTf.getText());
		nameTf.setText(emp.getName());
		salaryTf.setText(""+emp.getSalary());
		if (emp instanceof Manager) {
			Manager mgr = (Manager) emp;
			addInfoTf.setText(mgr.getPosition());
			mgrCheck.setState(true);
			showType(MGR_STATE);
		}else if(emp instanceof Engineer) {
			Engineer eng = (Engineer) emp;
			addInfoTf.setText(eng.getSkill());
			engCheck.setState(true);
			showType(ENG_STATE);
		}else {
			empCheck.setState(true);
			showType(EMP_STATE);
		}
	}
	public void setModel(EmployeeService  model){ 
		this.model = model;
		showList();
	}
	public void delete(){
		String  empno= empnoTf.getText();
		model.remove(empno);
		showList();
		clear();
	}
	public void update() {
		String  empno= empnoTf.getText();
		String  ename=nameTf.getText();
		String salary = salaryTf.getText();
		String addInfo = addInfoTf.getText();
		if(empCheck.getState()){
			model.update(new Employee(empno, ename, Integer.parseInt(salary)));
		}else if(mgrCheck.getState()){
			model.update(new Manager( empno,ename, Integer.parseInt(salary),addInfo));
		}else if(engCheck.getState()){
			model.update(new Engineer(empno, ename, Integer.parseInt(salary),addInfo));
		}
		showList();
		clear();
	}
	public void insert(){
		String  empno= empnoTf.getText();
		String  ename=nameTf.getText();
		String salary = salaryTf.getText();
		String addInfo = addInfoTf.getText();
		if(empCheck.getState()){
			model.add(new Employee(empno, ename, Integer.parseInt(salary)));
		}else if(mgrCheck.getState()){
			model.add(new Manager( empno,ename, Integer.parseInt(salary),addInfo));
		}else if(engCheck.getState()){
			model.add(new Engineer(empno, ename, Integer.parseInt(salary),addInfo));
		}
		showList();
		clear();
	}
	public void clear(){
		empnoTf.setText("");
		nameTf.setText("");
		salaryTf.setText("");
		addInfoTf.setText("");
	}
	public void showType(int state){
		switch (state) {
		case ENG_STATE:
			addInfoL.setText("보유기술");
			addInfoPan.setVisible(true);
			break;
		case MGR_STATE:
			addInfoL.setText("직        위");
			addInfoPan.setVisible(true);
			break;
		default:
			addInfoPan.setVisible(false);
		}
	}
	
	public EmployeeUI(){
		main = new JFrame("사원 관리 프로그램");
		dialog = new MessageDialog(main);
		insertBt = new JButton("등록");			updateBt = new JButton("수정");
		deleteBt = new JButton("삭제");			findBt = new JButton("검색");
		exitBt = new JButton("종료");			sendBt = new JButton("전송");
		
		empnoTf = new JTextField();				nameTf= new JTextField();
		salaryTf = new JTextField();			addInfoTf = new JTextField();
		
		JLabel empnoL = new JLabel("사원번호");			
		JLabel nameL = new JLabel("사원이름");
		JLabel salaryL = new JLabel("급     여");			
		addInfoL = new JLabel();			
		JLabel listL = new JLabel("사원목록", JLabel.CENTER);
		JLabel mgrL = new JLabel("사원관리",JLabel.CENTER);
		
		CheckboxGroup  search = new CheckboxGroup();
		empCheck = new Checkbox("Employee", false, search);
		mgrCheck = new Checkbox("Manager", false, search);
		engCheck = new Checkbox("Engineer", false, search);
		
		empModel = new DefaultTableModel(header, 10);
		empTable = new JTable(empModel);
		empPan = new JScrollPane(empTable);

		//이벤트 연결 
		insertBt.addActionListener(buttonHandler) ;
		findBt.addActionListener(buttonHandler);
		deleteBt.addActionListener(buttonHandler);
		updateBt.addActionListener(buttonHandler);
		exitBt.addActionListener(buttonHandler);
		sendBt.addActionListener(buttonHandler);
	
		empTable.addMouseListener(listHandler);
		
		empCheck.addItemListener(checkHandler);
		mgrCheck.addItemListener(checkHandler);
		engCheck.addItemListener(checkHandler);
		
		//화면 셋팅 
		JPanel updatePan = new JPanel(new GridLayout(6,1,5,5));  //왼쪽 화면을 위한 Pan
		
		JPanel checkPan = new JPanel(new GridLayout(1,3,5,5));
		JPanel empnoPan = new JPanel(new GridLayout(1,2,5,5));
		JPanel enamePan = new JPanel(new GridLayout(1,2,5,5));
		JPanel salPan = new JPanel(new GridLayout(1,2,5,5));
		addInfoPan = new JPanel(new GridLayout(1,2,5,5));
		JPanel buttonPan = new JPanel(new GridLayout(1,4,5,5));
		
		checkPan.add(empCheck);
		checkPan.add(mgrCheck);
		checkPan.add(engCheck);
		empnoPan.add(empnoL);
		empnoPan.add(empnoTf);
		enamePan.add(nameL);			
		enamePan.add(nameTf);
		salPan.add(salaryL);			
		salPan.add(salaryTf);
		addInfoPan.add(addInfoL);
		addInfoPan.add(addInfoTf);
		buttonPan.add(insertBt);		
		buttonPan.add(updateBt);
		buttonPan.add(deleteBt);		
		buttonPan.add(findBt);
		buttonPan.add(exitBt);
		buttonPan.add(sendBt);
		
		updatePan.add(checkPan);			
		updatePan.add(empnoPan);		
		updatePan.add(enamePan);
		updatePan.add(salPan);
		updatePan.add(addInfoPan);
		updatePan.add(buttonPan);
		
		
		JPanel left = new JPanel(new BorderLayout(5,5));
		left.add(mgrL,"North");			left.add(updatePan,"Center");
		
		JPanel right = new JPanel(new BorderLayout(5, 5));
		right.add(listL,"North");			right.add(empPan);
		
		showType(EMP_STATE);
		empCheck.setState(true);
		main.setLayout(new GridLayout(1, 2, 5, 5));
		main.add(left);					main.add(right);
		main.setSize(900, 300);
		main.setVisible(true);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void showList(){
		List<Employee> list = model.searchAll();
		if(list!=null){
			String[][]data = new String[list.size()][4];
			for (int i=0; i<list.size(); i++){
				Employee emp = list.get(i);
				data[i][0]  = emp.getEmpno();
				data[i][1]  = emp.getName();
				data[i][2]  = ""+emp.getSalary();
				if (emp instanceof Manager) {
					Manager mgr = (Manager) emp;
					data[i][3] = mgr.getPosition();
				}else if (emp instanceof Engineer) {
					Engineer eng = (Engineer) emp;
					data[i][3] = eng.getSkill();
				}else{
					data[i][3] = "";
				} 
			}
			empModel.setDataVector(data, header);
		}
	}
	
}




































