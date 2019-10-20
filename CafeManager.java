package GUIProject;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;


class MainFrame extends JFrame  {
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension ScreenSize = kit.getScreenSize();
	JTabbedPane tab;
	JPanel home, customer, stock, sales, product, statistics; 
	
	public MainFrame() {
		setSize(1300,1000);
		//setSize(ScreenSize.width/3*2,ScreenSize.height/3*2);
		setLocation(ScreenSize.width/2-getWidth()/2, ScreenSize.height/2-getHeight()/2);
		setTitle("mainframe");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new CardLayout());
		//setResizable(false);
		
		tab = new JTabbedPane();
		
		tab.add("홈 화면", new HomePanel());
		tab.add("고객관리", new CustomerPanel());
		tab.add("재고관리", new StockPanel());
		tab.add("매출관리", new SalesPanel());
		//tab.add("통계", new StatisticsPanel());
		tab.setFont(new Font("굴림체",Font.PLAIN,17));
		add(tab);

		setVisible(true);
	}
}

public class CafeManager {
	public static void main(String[] args) throws SQLException {
		new LoginFrame();
	}
}