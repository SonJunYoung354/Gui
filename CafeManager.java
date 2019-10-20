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
		
		tab.add("Ȩ ȭ��", new HomePanel());
		tab.add("������", new CustomerPanel());
		tab.add("������", new StockPanel());
		tab.add("�������", new SalesPanel());
		//tab.add("���", new StatisticsPanel());
		tab.setFont(new Font("����ü",Font.PLAIN,17));
		add(tab);

		setVisible(true);
	}
}

public class CafeManager {
	public static void main(String[] args) throws SQLException {
		new LoginFrame();
	}
}