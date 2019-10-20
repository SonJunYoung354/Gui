package GUIProject;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import GUIProject.CustomerPanel.CustomerInputFrame;

public class SalesPanel extends JPanel implements ActionListener {
	SalesCP scp;
	SalesTable st;
	
	String p_date;
	String p_order_id;
	String p_cust_id;
	String p_cust_name;
	String p_pd_group;
	String p_pd_name;
	String p_pd_price;
	String p_count;
	String p_discount;
	String p_saleprice;
	String p_method;
	String [] items = new String[10];
	
	String p_pd_id;
	
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	public SalesPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0,15,15,15));
		
		scp = new SalesCP();
		st = new SalesTable();
		
		for(int i=0 ; i<scp.cud_p.b.length ; i++) scp.cud_p.b[i].addActionListener(this);
		viewData();
		
		add(scp,BorderLayout.NORTH);
		add(st,BorderLayout.CENTER);
	}
	class SalesTable extends JPanel {
		JTable table;
		JScrollPane jsp;
		DefaultTableModel model;
		
		public SalesTable() {
			setLayout(new BorderLayout());
			
			String [] columnNames = {"날짜","주문번호","회원번호","이름","상품그룹","판매상품","단가","수량","금액","납부방법"};
			model = new DefaultTableModel(columnNames,0) {
				@Override
				public boolean isCellEditable(int row, int column) {return false;}
			};
			table = new JTable(model);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			model.setColumnIdentifiers(columnNames);
			table.setFillsViewportHeight(true);
			table.setFont(new Font("돋움체",Font.PLAIN,12));
			jsp = new JScrollPane(table);
			
			add(jsp,BorderLayout.CENTER);
		}
	}

	class SalesCP extends JPanel {
		CUDPanel cud_p;
		RPanel r_p;
		JPanel p;
		public SalesCP() {
			setLayout(new BorderLayout());
			
			p = new JPanel();
			p.setLayout(new BorderLayout());
			
			cud_p = new CUDPanel();
			r_p = new RPanel();
			
			p.add(cud_p,BorderLayout.NORTH);
			p.add(r_p,BorderLayout.CENTER);
			add(p,BorderLayout.WEST);
		}
	}
	//create update delete panel
	class CUDPanel extends JPanel {
		JButton [] b = new JButton[3];
		String [] str = {"내역 입력","판매 수정","내역 삭제"};
		JPanel p;
		
		public CUDPanel() {
			setLayout(new BorderLayout());
			
			p = new JPanel();
			for(int i=0 ; i<b.length ; i++) {
				b[i] = new JButton(str[i]);
				b[i].setFont(new Font("돋움체",Font.PLAIN,14));
				p.add(b[i]);
			}
			
			add(p,BorderLayout.WEST);
		}
	}
	//read
	class RPanel extends JPanel implements ActionListener {
		JPanel option;
		JButton beforedate_btn, afterdate_btn, select_btn;
		JTextField beforedate_tf, afterdate_tf;
		JComboBox<String> cb[] = new JComboBox[3];
		
		public RPanel() {
			BorderLayout bl = new BorderLayout();
			bl.setHgap(40);
			setLayout(bl);
			
			option = new JPanel();
			beforedate_tf = new JTextField();
			afterdate_tf = new JTextField();
			beforedate_btn = new JButton(new ImageIcon("calendar.png"));
			afterdate_btn = new JButton(new ImageIcon("calendar.png"));
			
			select_btn = new JButton("조  회");
			
			setToday(beforedate_tf);
			setToday(afterdate_tf);
			beforedate_tf.setFont(new Font("굴림체",Font.PLAIN,14));
			beforedate_tf.setEditable(false);
			afterdate_tf.setFont(new Font("굴림체",Font.PLAIN,14));
			afterdate_tf.setEditable(false);
			
			beforedate_btn.setBackground(Color.WHITE);
			beforedate_btn.addActionListener(this);
			afterdate_btn.setBackground(Color.WHITE);
			afterdate_btn.addActionListener(this);
			
			String [] cbName = {"pd_group","pd_name","method"};
			for(int i=0 ; i<cb.length ; i++) {
				String sql="";
				cb[i] = new JComboBox<String>();
				switch(i) {
				case 0:
					sql="SELECT pd_group FROM product_group";
					initCB(cb[i],sql,"pd_group","그룹천제");
					cb[i].addActionListener(this);
					break;
				case 1:
					cb[i].addItem("상품전체");
					break;
				case 2:
					cb[i].addItem("결제천체");
					cb[i].addItem("현금");
					cb[i].addItem("카드");
					cb[i].addItem("앱결제");
				}
				cb[i].setPreferredSize(new Dimension(120,25));
				cb[i].setFont(new Font("굴림체",Font.PLAIN,14));
			}
			
			select_btn.setFont(new Font("굴림체",Font.PLAIN,14));
			
			select_btn.addActionListener(this);
			
			option.add(beforedate_tf);
			option.add(beforedate_btn);
			option.add(afterdate_tf);
			option.add(afterdate_btn);
			option.add(cb[0]);
			option.add(cb[1]);
			option.add(cb[2]);
			option.add(select_btn);
			add(option,BorderLayout.CENTER);
		}
		
		class BeforeCalendar extends Calendar_Frame {
			public BeforeCalendar() {setTitle("이전날짜 설정");}
			public void mouseClicked(MouseEvent e) {
				for(int i=1 ; i<calendar_p.date_lbl.length ; i++) {
					for(int j=0 ; j<calendar_p.date_lbl[i].length ; j++) {
						if(e.getSource()==calendar_p.date_lbl[i][j]) {
							selectedDate = calendar_p.ym_lbl.getText()+"-"+calendar_p.date_lbl[i][j].getText();
							SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
							Date beforedate;
							Date afterdate;
							try {
								beforedate = f.parse(selectedDate);
								afterdate = f.parse(scp.r_p.afterdate_tf.getText());
								
								if(beforedate.compareTo(afterdate)==1) {
									
									JOptionPane.showMessageDialog(null, "이후날짜범위보다 큽니다.");
								}
								else {
									scp.r_p.beforedate_tf.setText(selectedDate);
									dispose();
								}
							} catch (ParseException e1) {e1.printStackTrace();}
						}
					}
				}
			}
		}
		
		class AfterCalendar extends Calendar_Frame {
			public AfterCalendar() {setTitle("이후날짜 설정");}
			public void mouseClicked(MouseEvent e) {
				for(int i=1 ; i<calendar_p.date_lbl.length ; i++) {
					for(int j=0 ; j<calendar_p.date_lbl[i].length ; j++) {
						if(e.getSource()==calendar_p.date_lbl[i][j]) {
							selectedDate = calendar_p.ym_lbl.getText()+"-"+calendar_p.date_lbl[i][j].getText();
							SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
							Date beforedate;
							Date afterdate;
							try {
								beforedate = f.parse(scp.r_p.beforedate_tf.getText());
								afterdate = f.parse(selectedDate);
								if(afterdate.compareTo(beforedate)==-1) {
									System.out.println(selectedDate);
									JOptionPane.showMessageDialog(null, "이전날짜범위보다 작습니다.");
								}
								else {
									scp.r_p.afterdate_tf.setText(selectedDate);
									dispose();
								}
							} catch (ParseException e1) {e1.printStackTrace();}
						}
					}
				}
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == scp.r_p.beforedate_btn) {new BeforeCalendar();}
			else if (e.getSource() == scp.r_p.afterdate_btn) {new AfterCalendar();}
			else if (e.getSource() == cb[0]) {
				String sql;
				sql = "SELECT pd_name FROM product WHERE pd_group = '"+toMySQL((String)cb[0].getSelectedItem())+"'";
				initCB(cb[1],sql,"pd_name","상품전체");
			}
			else if (e.getSource() == scp.r_p.select_btn) {viewData();}
		}
		
	}
	
	class SalesInputFrame extends JFrame implements ActionListener {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension ScreenSize = kit.getScreenSize();
		
		JPanel center_p, south_p;
		JPanel [] p = new JPanel[7];
		JLabel [] lbl = new JLabel[7];
		JTextField [] tf = new JTextField[4];
		JComboBox<String> [] cb = new JComboBox[3];
		JButton cancel_btn, ok_btn;
		JCheckBox dup_chkb;
		String [] lblNames = {"날짜","주문번호","고객번호","상품그룹","상품이름","갯수","걸제방법"};
		String beforedate, beforeid;
		boolean isUpdate;		
		
		public SalesInputFrame() {
			setLayout(new BorderLayout());
			
			center_p = new JPanel(new GridLayout(7,1));
			south_p = new JPanel();
			cancel_btn = new JButton("취소");
			ok_btn = new JButton("입력");
			dup_chkb = new JCheckBox("중복검사");
			beforedate = new String();
			beforeid = new String();
			
			String sql;
			for(int i=0 ; i<p.length ; i++) {
				p[i] = new JPanel();
				lbl[i] = new JLabel(lblNames[i]);
				p[i].add(lbl[i]);
				center_p.add(p[i]);
				switch(i) {
				case 0:
				case 1:
				case 2:
					tf[i] = new JTextField(10);
					p[i].add(tf[i]);
					break;
				case 3:
					cb[0] = new JComboBox<String>();
					sql = "SELECT pd_group FROM product_group";
					initCB(cb[0],sql,"pd_group",null);
					cb[0].addActionListener(this);
					p[i].add(cb[0]);
					break;
				case 4:
					cb[1] = new JComboBox<String>();
					sql = "SELECT pd_name FROM product WHERE pd_group='"+toMySQL((String)cb[0].getSelectedItem())+"'";
					initCB(cb[1],sql,"pd_name",null);
					p[i].add(cb[1]);
					break;
				case 5:
					tf[3] = new JTextField(10);
					p[i].add(tf[3]);
					break;
				case 6:
					String [] method = {"현금","카드","앱결제"};
					cb[2] = new JComboBox<String>(method);
					p[i].add(cb[2]);
				}
			}
			
			p[1].add(dup_chkb);
			south_p.add(cancel_btn);
			south_p.add(ok_btn);
			dup_chkb.addActionListener(this);
			cancel_btn.addActionListener(this);
			ok_btn.addActionListener(this);
			isUpdate = false;
			
			add(center_p,BorderLayout.CENTER);
			add(south_p,BorderLayout.SOUTH);
			
			pack();
			setLocation(ScreenSize.width/2-getWidth()/2, ScreenSize.height/2-getHeight()/2);
			setVisible(true);
		}
		
		public SalesInputFrame(String str) {
			this();
			getData();
			beforedate=p_date;
			beforeid=p_order_id;
			tf[0].setText(p_date);
			tf[1].setText(p_order_id);
			tf[2].setText(p_cust_id);
			cb[0].setSelectedItem(p_pd_group);
			cb[1].setSelectedItem(p_pd_name);
			tf[3].setText(p_count);
			cb[2].setSelectedItem(p_method);
			tf[0].setEnabled(false);
			tf[1].setEnabled(false);
			dup_chkb.setSelected(true);
			isUpdate = true;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==cancel_btn) dispose();
			else if(e.getSource()==ok_btn) {
				if(dup_chkb.isSelected()) {
					if(!isUpdate) {
						String sql;
						setData();
						sql = "INSERT INTO sales VALUES ('"+p_date+"','"+p_order_id+"','"+p_cust_id+"','"+p_pd_group+"','";
						sql+= p_pd_id+"','"+p_count+"','"+p_method+"')";
						inputSQL(sql);
						viewData();
						dispose();
					}
					else {
						String sql;
						setData();
						sql = "UPDATE sales SET cust_id='"+p_cust_id+"' WHERE date='"+p_date+"' AND order_id='"+p_order_id+"'";
						inputSQL(sql);
						sql = "UPDATE sales SET pd_group='"+p_pd_group+"' WHERE date='"+p_date+"' AND order_id='"+p_order_id+"'";
						inputSQL(sql);
						sql = "UPDATE sales SET pd_id='"+p_pd_id+"' WHERE date='"+p_date+"' AND order_id='"+p_order_id+"'";
						inputSQL(sql);
						sql = "UPDATE sales SET count='"+p_count+"' WHERE date='"+p_date+"' AND order_id='"+p_order_id+"'";
						inputSQL(sql);
						sql = "UPDATE sales SET method='"+p_method+"' WHERE date='"+p_date+"' AND order_id='"+p_order_id+"'";
						inputSQL(sql);
						viewData();
						dispose();
					}
				}
			}
			else if(e.getSource()==cb[0]) {
				String sql;
				sql = "SELECT pd_name FROM product WHERE pd_group='"+toMySQL((String)cb[0].getSelectedItem())+"'";
				initCB(cb[1],sql,"pd_name",null);
			}
			else if(e.getSource()==dup_chkb) {isDuplicated();}
		}
		
		public void setData() {
			p_date=tf[0].getText();
			p_order_id=toMySQL(tf[1].getText());
			p_cust_id=tf[2].getText();
			p_pd_group=toMySQL((String)cb[0].getSelectedItem());
			try {
				makeConnection();
				stmt=con.createStatement();
				String sql;
				sql = "SELECT pd_id FROM product WHERE pd_name='"+toMySQL((String)cb[1].getSelectedItem())+"'";
				rs = stmt.executeQuery(sql);
				rs.next();
				p_pd_id=rs.getString("pd_id");
			} catch(SQLException sqle) {System.out.println("name->id error : "+sqle.getMessage());}
			disConnection();
			p_count=tf[3].getText();
			p_method=(String)cb[2].getSelectedItem();
			if(p_method.equals("현금")) p_method="0";
			else if(p_method.equals("카드")) p_method="1";
			else if(p_method.equals("앱결제")) p_method="2";
		}
		
		public void getData() {
			int row = st.table.getSelectedRow();
			p_date=(String)st.model.getValueAt(row, 0);
			p_order_id=(String)st.model.getValueAt(row, 1);
			
			String sql;
			sql = "SELECT * FROM vw_sales WHERE date='"+p_date+"' AND order_id='"+p_order_id+"'";
			try {
				makeConnection();
				stmt=con.createStatement();
				rs = stmt.executeQuery(sql);
				rs.next();
				p_cust_id=rs.getString("cust_id");
				p_pd_group = fromMySQL(rs.getString("pd_group"));
				p_pd_name = rs.getString("pd_name");
				p_count = rs.getString("count");
				p_method = rs.getString("method");
			} catch(SQLException sqle) {System.out.println("setData SQL Error :"+sqle.getMessage());}
			disConnection();
		}
		
		public void isDuplicated() {
			if(tf[0].getText().equals("")) {
				dup_chkb.setSelected(false);
				JOptionPane.showMessageDialog(null, "값을 입력해주세요");
			}
			else if(dup_chkb.isSelected()) {
				try {
					makeConnection();
					stmt=con.createStatement();
					String sql;
					sql = "SELECT COUNT(*) FROM sales WHERE date='"+tf[0].getText()+"' AND order_id='"+toMySQL((String)tf[1].getText())+"'";
					rs = stmt.executeQuery(sql);
					rs.next();
					int checkID=rs.getInt("COUNT(*)");
				
					if(checkID==0 | (beforedate.equals(tf[0].getText()) & beforeid.equals(tf[1].getText()))) 
							{tf[0].setEnabled(false); tf[1].setEnabled(false);}
					else {
						dup_chkb.setSelected(false);
						JOptionPane.showMessageDialog(null, "중복된 번호입니다.");
					}
				} catch(SQLException sqle) {System.out.println("isDuplicated error : "+sqle.getMessage());}
				disConnection();
			}
			else if(!dup_chkb.isSelected()) {tf[0].setEnabled(true); tf[1].setEnabled(true);}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==scp.cud_p.b[0]) {new SalesInputFrame();}
		else if(e.getSource()==scp.cud_p.b[1]) {
			if(!(st.table.getSelectedRowCount()==0)) new SalesInputFrame("UPDATE");
			else JOptionPane.showMessageDialog(null, "행을 선택해주세요");
			}
		else if(e.getSource()==scp.cud_p.b[2]) {
			if(!(st.table.getSelectedRowCount()==0)) deleteData();
			else JOptionPane.showMessageDialog(null, "행을 선택해주세요");
		}
	}
	
	public void setToday(JTextField date) {
		try {
			makeConnection();
			stmt = con.createStatement();
			String sql;
			sql = "SELECT DATE_FORMAT(CURDATE(),'%Y-%m-%d') AS today FROM dual";
			rs=stmt.executeQuery(sql);
			rs.next();
			date.setText(fromMySQL(rs.getString("today")));
		} catch(SQLException sqle) {System.out.println("default date set error"+sqle.getMessage());}
		disConnection();
	}
	
	public void initCB(JComboBox<String> jcb, String sql, String getString, String Default) {
		try {
			makeConnection();
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			jcb.removeAllItems();
			jcb.addItem(Default);
			while(rs.next()) {
				jcb.addItem(fromMySQL(rs.getString(getString)));
			}
		} catch(SQLException sqle) {System.out.println("initCB SQL Error :"+sqle.getMessage());}
		disConnection();
	}
	
	public void setData() {		//날짜, 주문번호에 해당하는 data를 sales table에서 읽어옴
		int row = st.table.getSelectedRow();
		p_date=(String)st.model.getValueAt(row, 0);
		p_order_id=(String)st.model.getValueAt(row, 1);
		
		String sql;
		sql = "SELECT * FROM sales WHERE date='"+p_date+"' AND order_id='"+p_order_id+"'";
		try {
			makeConnection();
			stmt=con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			p_cust_id=rs.getString("cust_id");
			p_pd_group = fromMySQL(rs.getString("pd_group"));
			p_pd_id = rs.getString("pd_id");
			p_count = rs.getString("count");
			p_method = rs.getString("method");
		} catch(SQLException sqle) {System.out.println("setData SQL Error :"+sqle.getMessage());}
		disConnection();
	}
	
	public void getData() throws SQLException {
		p_date=fromMySQL(rs.getString("date"));
		p_order_id=rs.getString("order_id");
		p_cust_id=rs.getString("cust_id");
		p_cust_name=fromMySQL(rs.getString("cust_name"));
		p_pd_group=fromMySQL(rs.getString("pd_group"));
		p_pd_name=fromMySQL(rs.getString("pd_name"));
		p_pd_price=rs.getString("pd_price");
		p_count=rs.getString("count");
		p_saleprice=rs.getString("saleprice");
		p_method=rs.getString("method");
		
		items[0]=p_date;
		items[1]=p_order_id;
		items[2]=p_cust_id;
		items[3]=p_cust_name;
		items[4]=p_pd_group;
		items[5]=p_pd_name;
		items[6]=p_pd_price;
		items[7]=p_count;
		items[8]=p_saleprice;
		if(p_method.equals("0")) items[9]="현금";
		else if(p_method.equals("1")) items[9]="카드";
		else if(p_method.equals("2")) items[9]="앱결제";
	}
	
	public void viewData() {		//옵션지정된 데이터를 받아서 뷰데이터
		String sql;
		sql="SELECT * FROM vw_sales WHERE date>='"+scp.r_p.beforedate_tf.getText()+"' AND date<='"+scp.r_p.afterdate_tf.getText()+"' ";
		if(!(scp.r_p.cb[0].getSelectedIndex()==0)) {
			sql+="AND pd_group='"+toMySQL((String)scp.r_p.cb[0].getSelectedItem())+"' ";
			if(!(scp.r_p.cb[1].getSelectedIndex()==0)) {
				sql+="AND pd_name='"+toMySQL((String)scp.r_p.cb[1].getSelectedItem())+"' ";
			}
		}
		if (!(scp.r_p.cb[2].getSelectedIndex()==0)) {
			sql+="AND method='"+(scp.r_p.cb[2].getSelectedIndex()-1)+"'";
			}
		System.out.println(sql);
		try{
			makeConnection();
			stmt=con.createStatement();
			st.model.setRowCount(0);
			rs=stmt.executeQuery(sql);
			while(rs.next()){
				getData();
				st.model.addRow(items);
			}
		}catch(SQLException sqle){System.out.println("viewData SQL Error :"+sqle.getMessage());}
		disConnection();
	}
	
	public void deleteData() {
		setData();
		String sql;
		sql="DELETE FROM sales WHERE date='"+p_date+"' AND order_id='"+p_order_id+"'";
		inputSQL(sql);
		viewData();
	}

	public void inputSQL(String sql) {
		try {
			makeConnection();
			stmt=con.createStatement();
			System.out.println(sql);
			int isExecute = stmt.executeUpdate(sql);
			if(isExecute==1) System.out.println("Execute Successfully");
			else System.out.println("Execute Failed");
		} catch(SQLException sqle) {System.out.println("Added SQL Error : "+sqle.getMessage());}
		disConnection();
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

