package GUIProject;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class StockPanel extends JPanel implements ActionListener {
	StockCP scp;
	StockTable st;

	String p_date;
	String p_id;
	String p_name;
	String p_lastSt;
	String p_warehousing;
	String p_used;
	String p_amount;
	String [] items = new String[6];
	
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	public StockPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0,15,15,15));
		
		scp = new StockCP();
		st = new StockTable();
		
		
		System.out.println(scp.date.getText());
		viewData(scp.date.getText());
		System.out.println(st.model.getRowCount());
		if(st.model.getRowCount()==0) {inheritanceData(); viewData(scp.date.getText());}
			
		add(scp,BorderLayout.NORTH);
		for(int i=0 ; i<scp.btn.length ; i++) scp.btn[i].addActionListener(this);
		scp.select_btn.addActionListener(this);
		add(st,BorderLayout.CENTER);
	}
	
	class StockTable extends JPanel {
		JTable table;
		JScrollPane jsp;
		DefaultTableModel model;
		String [] columnNames = {"번호","품명","전일재고","입고","사용","최종재고"};
		
		public StockTable() {
			setLayout(new BorderLayout());
			
			int [] columnWidth = {36,335,36,36,36,36};
			model = new DefaultTableModel(columnNames,0) {
				@Override
				public boolean isCellEditable(int row, int column) {return false;}
			};
			table = new JTable(model);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setFont(new Font("돋움체",Font.PLAIN,12));
			model.setColumnIdentifiers(columnNames);
			table.setFillsViewportHeight(true);
			for(int i=0 ; i<table.getColumnCount() ; i++) 
			table.getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
			jsp = new JScrollPane(table);
			
			add(jsp,BorderLayout.CENTER);
		}
	}
	
	//stockControlPanel
	class StockCP extends JPanel implements ActionListener{
		JPanel crud,cud,r;
		JButton [] btn = new JButton[3];
		JButton calendar_btn,select_btn;
		JTextField date;
		Font f = new Font("굴림체",Font.PLAIN,14);
		
		public StockCP() {
			setLayout(new BorderLayout());
			String [] str = {"삽  입","수  정","삭  제"};
			
			crud = new JPanel();
			
			cud = new JPanel();
			r = new JPanel();
			
			BorderLayout bl = new BorderLayout();
			
			bl.setHgap(40);
			crud.setLayout(bl);
			date = new JTextField(10);
			setToday();	//오늘날짜로 초기화
			date.setFont(f);
			date.setEditable(false);
			date.addActionListener(this);
			r.add(date);
			calendar_btn = new JButton(new ImageIcon("calendar.png"));
			calendar_btn.setBackground(Color.WHITE);
			calendar_btn.addActionListener(this);
			select_btn = new JButton("조  회");
			select_btn.setFont(f);
			r.add(calendar_btn);
			r.add(select_btn);
			
			for(int i=0 ; i<btn.length ; i++) {
				btn[i] = new JButton(str[i]);
				btn[i].setFont(f);
				cud.add(btn[i]);
			}
			add(crud,BorderLayout.WEST);
			crud.add(cud,BorderLayout.WEST);
			crud.add(r,BorderLayout.CENTER);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==calendar_btn) { new SelectDate(); }
		}
		
		public void setToday() {
			try {
				makeConnection();
				stmt = con.createStatement();
				String sql;
				sql = "SELECT DATE_FORMAT(CURDATE(),'%Y-%m-%d') AS today FROM dual";
				rs=stmt.executeQuery(sql);
				rs.next();
				date.setText(fromMySQL(rs.getString("today")));
			} catch(SQLException sqle) {System.out.println("default date set error");}
			disConnection();
		}
	}
	
	class SelectDate extends Calendar_Frame {
		@Override
		public void mouseClicked(MouseEvent e) {
			for(int i=1 ; i<calendar_p.date_lbl.length ; i++) {
				for(int j=0 ; j<calendar_p.date_lbl[i].length ; j++) {
					if(e.getSource()==calendar_p.date_lbl[i][j]) {
						selectedDate = calendar_p.ym_lbl.getText()+"-"+calendar_p.date_lbl[i][j].getText();
						scp.date.setText(selectedDate);;
						dispose();
					}
				}
			}
		}
	}
	
	class StockInputFrame extends JFrame implements ActionListener {
		final int ELEMENTS_NUM = 5;
		JPanel input_p, btn_p;
		JPanel [] ip_p = new JPanel[ELEMENTS_NUM];
		String [] elements = {"날짜", "번호", "이름", "입고", "사용"};
		JLabel [] lbl = new JLabel[ELEMENTS_NUM];
		JTextField [] tf = new JTextField[ELEMENTS_NUM];
		JButton cancel, ok;
		JCheckBox dup_chkb;
		String sql;
		boolean isUpdate;
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension ScreenSize = kit.getScreenSize();
		
		public StockInputFrame() {
			setTitle("재고입력");
			setLayout(new BorderLayout());
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			input_p = new JPanel();
			input_p.setLayout(new GridLayout(4,1));
			
			for(int i=0 ; i<ELEMENTS_NUM ; i++) {
				FlowLayout fl = new FlowLayout();
				fl.setAlignment(FlowLayout.LEFT);
				ip_p[i] = new JPanel();
				lbl[i] = new JLabel(elements[i]);
				tf[i] = new JTextField(10);
				ip_p[i].setLayout(fl);
				ip_p[i].add(lbl[i]);
				ip_p[i].add(tf[i]);
				input_p.add(ip_p[i]);
			}
			
			dup_chkb = new JCheckBox("중복검사");
			dup_chkb.addActionListener(this);
			ip_p[1].add(dup_chkb);
			tf[3].addActionListener(this);
			
			btn_p = new JPanel();
			cancel = new JButton("취소");
			ok = new JButton("입력");
			
			cancel.addActionListener(this);
			ok.addActionListener(this);
			
			btn_p.add(cancel);
			btn_p.add(ok);
			isUpdate = false;
			
			add(input_p, BorderLayout.CENTER);
			add(btn_p, BorderLayout.SOUTH);
			
			pack();
			setLocation(ScreenSize.width/2-getWidth()/2, ScreenSize.height/2-getHeight()/2);
			setVisible(true);
		}
		
		public StockInputFrame(String str) {
			this();
			p_date = scp.date.getText();
			int row = st.table.getSelectedRow();
			for(int i=0 ; i<st.columnNames.length ; i++) {
				items[i] = (String)st.model.getValueAt(row, i);
			}
			setTitle("재고수정");
			tf[0].setText(p_date);
			tf[1].setText(items[0]);
			tf[2].setText(items[1]);
			tf[3].setText(items[3]);
			tf[4].setText(items[4]);
			for(int i=0 ; i<3 ; i++) tf[i].setEnabled(false);
			isUpdate = true;
			dup_chkb.setSelected(true);
			dup_chkb.setEnabled(false);
		}
		public void actionPerformed(ActionEvent e) {
			makeConnection();
			try {
				stmt = con.createStatement();
				if(e.getSource()==dup_chkb) {isDuplicated();}
				if(e.getSource()==cancel) {dispose();}
				else if(e.getSource()==ok||e.getSource()==tf[3]) {
					if(dup_chkb.isSelected()) {
						setData();
						if(isUpdate) {setData(); updateData(); dispose();}
						else {
							String sql;
							sql = "INSERT INTO stock (date,st_id,st_name,lastSt,warehousing,used,amount) VALUES ";
							sql += "('"+p_date+"','"+p_id+"','"+toMySQL(p_name)+"','0','"+p_warehousing+"','"+p_used+"','"+p_warehousing+"')";
							insertData(sql);
							dispose();}
						}
					else {JOptionPane.showMessageDialog(null, "중복검사를 해주세요");}
				}
			} catch(SQLException sqle) { System.out.println("stmt connection err");}
			disConnection();
		}
		
		public void setData() {
			p_date = tf[0].getText();
			p_id = tf[1].getText();
			p_name = tf[2].getText();
			p_warehousing = tf[3].getText();
			p_used = tf[4].getText();
		}

		public void isDuplicated() {
			try{
				if(scp.date.getText().equals("")) {JOptionPane.showMessageDialog(null, "값을 입력해주세요");}
				else {
					sql = "SELECT COUNT(*) FROM stock WHERE st_id='"+tf[1].getText()+"' AND date='"+tf[0].getText()+"'";
					
					rs = stmt.executeQuery(sql);
					rs.next();
					int checkID=rs.getInt("COUNT(*)");
					System.out.println(sql);
				
					if(checkID==0) {tf[0].setEnabled(false); tf[1].setEnabled(false);}
					else {
						dup_chkb.setSelected(false);
						JOptionPane.showMessageDialog(null, "중복된 번호입니다.");
					}
				}
			} catch(SQLException sqle) {System.out.println("중복검사 : SQL Error");}
			disConnection();
		}
	}
	
	class SelectCalendar extends Calendar_Frame {
		@Override
		public void mouseClicked(MouseEvent e) {
			for(int i=1 ; i<calendar_p.date_lbl.length ; i++) {
				for(int j=0 ; j<calendar_p.date_lbl[i].length ; j++) {
					if(e.getSource()==calendar_p.date_lbl[i][j]) {
						selectedDate = calendar_p.ym_lbl.getText()+"-"+calendar_p.date_lbl[i][j].getText();
						scp.date.setText(selectedDate);;
						dispose();
					}
				}
			}
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			makeConnection();
			stmt = con.createStatement();
			if(e.getSource()==scp.btn[0]) {new StockInputFrame();}
			else if(e.getSource()==scp.btn[1]) {
				if(st.table.getSelectedRowCount()!=0) new StockInputFrame("UPDATE");
				else {JOptionPane.showMessageDialog(null, "행을 선택해주세요");}
			}
			else if(e.getSource()==scp.btn[2]) { 
				if(st.table.getSelectedRowCount()!=0) deleteData(); 
				else JOptionPane.showMessageDialog(null, "행을 선택해주세요");
			}
			if(e.getSource()==scp.select_btn) {
				viewData(scp.date.getText());
			}
		}
		catch(SQLException ex) { System.out.println(ex.getMessage());}
		disConnection();
		
		if(e.getSource()==scp.date) {}
	}
	
	public void inheritanceData() {	//매일 접속하지 않았을 때 버그발생(전일의 데이터가 갱신이 안됐을떄)
		try {
			makeConnection();
			stmt = con.createStatement();
			String sql;
			int row_count=0;
			
			p_date = scp.date.getText();
			sql = "SELECT * FROM stock WHERE date=DATE_ADD('"+p_date+"',INTERVAL -1 DAY)";
			System.out.println(sql);
			rs = stmt.executeQuery(sql);
			while(rs.next()) row_count++;
			rs.beforeFirst();
			
			String [] tmp = new String[row_count];
			for(int i=0 ; rs.next() ; i++) {
				getData();
				p_date = scp.date.getText();
				System.out.println(p_date+" : "+p_id+" : "+p_name+" : "+p_lastSt+" : "+p_warehousing+" : "+p_used+" : "+p_amount);
				tmp[i] = "INSERT INTO stock (date,st_id,st_name,lastSt,warehousing,used,amount) VALUES ";
				tmp[i] += "('"+p_date+"','"+p_id+"','"+toMySQL(p_name)+"','"+p_amount+"','0','0','"+p_amount+"')";
			}
			for(int i=0 ; i<tmp.length ; i++) insertData(tmp[i]);
		} catch(SQLException sqle) {System.out.println("inheritance Data error");}
		disConnection();
	}
	
	public void getData() throws SQLException {
		p_date = fromMySQL(rs.getString("date"));
		p_id = fromMySQL(rs.getString("st_id"));
		p_name = fromMySQL(rs.getString("st_name"));
		p_lastSt = rs.getInt("lastSt")+"";
		p_warehousing = rs.getInt("warehousing")+"";
		p_used = rs.getInt("used")+"";
		p_amount = rs.getInt("amount")+"";
		items[0] = p_id;
		items[1] = p_name;
		items[2] = p_lastSt;
		items[3] = p_warehousing;
		items[4] = p_used;
		items[5] = p_amount;
	}
	
	public void viewData(String date) {
		String sql="";
		sql="SELECT * FROM stock WHERE date='"+date+"'";
		try{
			makeConnection();
			stmt=con.createStatement();
			st.model.setRowCount(0);
			rs=stmt.executeQuery(sql);
			while(rs.next()){
				getData();
				st.model.addRow(items);
			}
		}catch(SQLException sqle){System.out.println("viewData: SQL Error-"+sqle.getMessage());}
		disConnection();
	}
	
	public void insertData(String sql) {
		try {
			makeConnection();
			stmt=con.createStatement();
			System.out.println(sql);
			int isAdded = stmt.executeUpdate(sql);
			if(isAdded==1) System.out.println("Added Successfully");
			else System.out.println("Added Failed");
			viewData(scp.date.getText());
		} catch(SQLException sqle) {System.out.println("Added : SQL Error"+sqle.getMessage());}
		disConnection();
	}
	
	public void updateData() throws SQLException {
		String sql;
		sql = "UPDATE stock SET warehousing='"+p_warehousing+"' WHERE st_id='"+p_id+"'";
		System.out.println(sql);
		stmt.executeUpdate(sql);
		sql = "UPDATE stock SET used='"+p_used+"' WHERE st_id='"+p_id+"'";
		System.out.println(sql);
		stmt.executeUpdate(sql);
		viewData(scp.date.getText());
	}
	
	public void deleteData() throws SQLException {	
		String sql;
		int row = st.table.getSelectedRow();
		p_id = (String)st.model.getValueAt(row, 0);
		p_date = scp.date.getText();	//bug. date값은 변경될 수 있다. -> String tabledate를 선언하여 해결할 수 있다. 
		sql = "DELETE FROM stock WHERE st_id='"+p_id+"' AND date='"+p_date+"'";
		System.out.println(sql);
		stmt.executeUpdate(sql);
		viewData(scp.date.getText());
	}
	
	public void disConnection() {
		try {
			rs.close();
			stmt.close();
			con.close();
		}
		catch(SQLException e) {System.out.println(e.getMessage());}
	}
	
	public String toMySQL(String str){
		try{
			if (str != null)
				return new String(str.getBytes("KSC5601"), "8859_1");
			else
				return null;
		} catch (Exception e) {e.printStackTrace();return null;}
	}

	public String fromMySQL(String str){
		try{
			if (str != null)
				return new String(str.getBytes("8859_1"),"KSC5601");
			else
				return null;
		} catch (Exception e) {e.printStackTrace();return null;}
	}
	
	public Connection makeConnection(){
		String url="jdbc:mysql://localhost/store_db";
		String id="root";
		String password="1234";
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection(url, id, password);
		}catch(ClassNotFoundException e){
			System.out.println("드라이버를 찾을 수 없습니다");
			e.getStackTrace();
		}catch(SQLException e){
			System.out.println("연결에 실패하였습니다");			
		}
		return con;
	}
}

class Calendar_Frame extends JFrame implements MouseListener {
	Calendar_Panel calendar_p;
	String selectedDate;
	
	public Calendar_Frame() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension ScreenSize = kit.getScreenSize();
		
		calendar_p = new Calendar_Panel();
		setBounds(ScreenSize.width/2-300/2, ScreenSize.height/2-400/2,400,300);
		for(int i=1 ; i<calendar_p.date_lbl.length ; i++) {
			for(int j=0 ; j<calendar_p.date_lbl[i].length ; j++) {
				if(calendar_p.date_lbl[i][j].getText()==null) {}
				else calendar_p.date_lbl[i][j].addMouseListener(this);
			}
		}
		
		add(calendar_p);
		
		setVisible(true);

	}
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
	