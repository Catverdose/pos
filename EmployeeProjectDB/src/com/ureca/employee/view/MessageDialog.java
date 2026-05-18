package com.ureca.employee.view;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MessageDialog {
	private JFrame main, owner;
	private JButton confirm;
	private JLabel  msgL;
	private int cx, cy;
	{
		main = new JFrame("알림");
		confirm = new JButton("확인");
		msgL = new JLabel();
		msgL.setHorizontalAlignment(JLabel.CENTER);
		//이벤트 처리 
		confirm.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		} );
		
		JPanel  south = new JPanel();
		south.add(confirm);
		main.add(msgL,"Center");
		main.add(south,"South");
		Toolkit tool = main.getToolkit();
		Dimension  screen = tool.getScreenSize();
		cx =(int)screen.getWidth()  >>1;
		cy= (int)screen.getHeight() >> 1;
	}
	public MessageDialog(){}
	public MessageDialog(JFrame owner){ 		this.owner = owner;	}
	public void hide(){	 main.setVisible(false); }
	public void show(String msg){
		msgL.setText(msg); 
		main.pack();       //배치된 컴포넌트 크기에 맞게 화면의 크기를 조절하는 메서드 
		int  mx=0, my=0;
		if(owner == null){   //화면 정중앙에 배치
			mx = cx - (main.getWidth()>>1) ;
			my = cy - (main.getHeight()>>1) ;
		}else{
			mx = owner.getX()+ (owner.getWidth()>>1) - (main.getWidth()>>1); 
			my = owner.getY()+ (owner.getHeight()>>1) - (main.getHeight()>>1); 
		}
		main.setLocation(mx, my);
		main.setVisible(true);
	}
	public static void main(String[] args) {
		MessageDialog  md1 = new MessageDialog();
		md1.show("화면 중앙에 배치되는 알림창");
		JFrame frame = new JFrame("");
		frame.setSize(200,200);
		frame.setLocation(300, 300);
		frame.setVisible(true);
		MessageDialog  md2 = new MessageDialog(frame);
		md2.show("frmae 중앙에 배치되는 알림창");
	}
}



































