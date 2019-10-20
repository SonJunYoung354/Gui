package GUIProject;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class CustomerPanel extends JPanel implements ActionListener {
	CustomerCP ccp;
	CustomerTable ct;
	String [] columnNames = {"회원번호","이름","생일","성별","전화번호","가입일","포인트"};
	
	String p_cust_id;
	String p_cust_name;
	String p_bday;
	String p_sex;
	String p_tel;
	String p_jday;
	String p_point;
	//String p_memo;
	
	String [] items = new String[7];
	
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	public CustomerPanel() {
		BorderLayout bl = new BorderLayout();
		bl.setHgap(15);
		setLayout(bl);
		setBorder(BorderFactory.createEmptyBorder(0,15,15,15));
		
		ccp = new CustomerCP();
		ct = new CustomerTable();
		viewData();
		
		for(int i=0 ; i<ccp.b.length ; i++) ccp.b[i].addActionListener(this);
		
		add(ccp,BorderLayout.NORTH);
		add(ct,BorderLayout.CENTER);
		//add(new MembershipPanel(),BorderLayout.EAST);
	}
	
	class CustomerTable extends JPanel {
		JTable table;
		JScrollPane jsp;
		DefaultTableModel model;
		int [] columnWidth = {};
		
		public CustomerTable() {
			setLayout(new BorderLayout());
			
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
	//CustomerControlPanel
	class CustomerCP extends JPanel {
		JButton [] b = new JButton[3];
		String [] str = {"회원 추가","회원 수정","회원 삭제"};
		JPanel p;
		
		public CustomerCP() {
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
	
	class CustomerInputFrame extends JFrame implements ActionListener {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension ScreenSize = kit.getScreenSize();
		
		JPanel center_p, south_p;
		int columnCount = columnNames.length;
		JPanel [] p = new JPanel[columnCount];
		JLabel [] lbl = new JLabel[columnCount];
		JTextField [] tf = new JTextField[6];
		JComboBox<String> sex_cb;
		JButton cancel_btn, ok_btn;
		JCheckBox dup_chkb;
		String beforeid;
		boolean isUpdate;
		
		
		public CustomerInputFrame() {
			setLayout(new BorderLayout());
			
			center_p = new JPanel(new GridLayout(7,1));
			south_p = new JPanel();
			cancel_btn = new JButton("취소");
			ok_btn = new JButton("입력");
			dup_chkb = new JCheckBox("중복검사");
			beforeid = new String();
			isUpdate = false;
			
			for(int i=0 ; i<p.length ; i++) {
				p[i] = new JPanel();
				lbl[i] = new JLabel(columnNames[i]);
				
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
					sex_cb = new JComboBox<String>();
					sex_cb.addItem("남자");
					sex_cb.addItem("여자");
					p[i].add(sex_cb);
					break;
				case 4:
				case 5:
				case 6:
					tf[i-1] = new JTextField(10);
					p[i].add(tf[i-1]);
				}
			}
			p[0].add(dup_chkb);
			tf[5].addActionListener(this);
			dup_chkb.addActionListener(this);
			cancel_btn.addActionListener(this);
			ok_btn.addActionListener(this);
			
			south_p.add(cancel_btn);
			south_p.add(ok_btn);
			
			add(center_p,BorderLayout.CENTER);
			add(south_p,BorderLayout.SOUTH);
			
			pack();
			setLocation(ScreenSize.width/2-getWidth()/2, ScreenSize.height/2-getHeight()/2);
			setVisible(true);
		}
		
		public CustomerInputFrame(String str) {
			this();
			getData();
			beforeid=p_cust_id;
			tf[0].setText(p_cust_id);
			tf[1].setText(p_cust_name);
			tf[2].setText(p_bday);
			sex_cb.setSelectedItem(p_sex);
			tf[3].setText(p_tel);
			tf[4].setText(p_jday);
			tf[5].setText(p_point);
			tf[0].setEnabled(false);
			dup_chkb.setSelected(true);
			isUpdate = true;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==cancel_btn) dispose();
			else if (e.getSource()==ok_btn || e.getSource()==tf[5]) {
				if(dup_chkb.isSelected()) {
					if(!isUpdate) {
						String sql;
						setData();
						sql = "INSERT INTO customer VALUES ('"+p_cust_id+"','"+p_cust_name+"','"+p_bday+"','";
						sql+= p_sex+"','"+p_tel+"','"+p_jday+"','"+p_point+"')";
						inputSQL(sql);
						viewData();
						dispose();
					}
					else {
						String sql;
						setData();
						sql = "UPDATE customer SET cust_id='"+p_cust_id+"' WHERE cust_id='"+beforeid+"'";
						inputSQL(sql);
						sql = "UPDATE customer SET cust_name='"+p_cust_name+"' WHERE cust_id='"+p_cust_id+"'";
						inputSQL(sql);
						sql = "UPDATE customer SET bday='"+p_bday+"' WHERE cust_id='"+p_cust_id+"'";
						inputSQL(sql);
						sql = "UPDATE customer SET sex='"+p_sex+"' WHERE cust_id='"+p_cust_id+"'";
						inputSQL(sql);
						sql = "UPDATE customer SET tel='"+p_tel+"' WHERE cust_id='"+p_cust_id+"'";
						inputSQL(sql);
						sql = "UPDATE customer SET jday='"+p_jday+"' WHERE cust_id='"+p_cust_id+"'";
						inputSQL(sql);
						sql = "UPDATE customer SET point='"+p_point+"' WHERE cust_id='"+p_cust_id+"'";
						inputSQL(sql);
						viewData();
						dispose();
					}
				}
				else JOptionPane.showMessageDialog(null, "중복검사를 해주세요.");
			}
			else if(e.getSource()==dup_chkb) {isDuplicated();}
		}
		
		public void setData() {
			p_cust_id=tf[0].getText();
			p_cust_name=toMySQL(tf[1].getText());
			p_bday=tf[2].getText();
			if(sex_cb.getSelectedItem().equals("남자")) p_sex="1";
			else if(sex_cb.getSelectedItem().equals("여자")) p_sex="2";
			p_tel=tf[3].getText();
			p_jday=tf[4].getText();
			p_point=tf[5].getText();
		}
		
		public void getData() {
			int row = ct.table.getSelectedRow();
			p_cust_id=(String)ct.model.getValueAt(row, 0);
			p_cust_name=(String)ct.model.getValueAt(row, 1);
			p_bday=(String)ct.model.getValueAt(row, 2);
			p_sex=(String)ct.model.getValueAt(row, 3);
			p_tel=(String)ct.model.getValueAt(row, 4);
			p_jday=(String)ct.model.getValueAt(row, 5);
			p_point=(String)ct.model.getValueAt(row, 6);
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
					sql = "SELECT COUNT(*) FROM customer WHERE cust_id='"+toMySQL((String)tf[0].getText())+"'";
					rs = stmt.executeQuery(sql);
					rs.next();
					int checkID=rs.getInt("COUNT(*)");
				
					if(checkID==0 | beforeid.equals(tf[0].getText())) {tf[0].setEnabled(false);}
					else {
						dup_chkb.setSelected(false);
						JOptionPane.showMessageDialog(null, "중복된 번호입니다.");
					}
				} catch(SQLException sqle) {System.out.println("isDuplicated error : "+sqle.getMessage());}
			}
			else if(!dup_chkb.isSelected()) tf[0].setEnabled(true);
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==ccp.b[0]) {new CustomerInputFrame();}
		else if(e.getSource()==ccp.b[1]) {
			if(!(ct.table.getSelectedRowCount()==0)) new CustomerInputFrame("UPDATE");
			else JOptionPane.showMessageDialog(null, "행을 선택해주세요");
			}
		else if(e.getSource()==ccp.b[2]) {
			if(!(ct.table.getSelectedRowCount()==0)) deleteData();
			else JOptionPane.showMessageDialog(null, "행을 선택해주세요");
		}

	}
	
	public void setData() {
		int row = ct.table.getSelectedRow();
		p_cust_id=(String)ct.model.getValueAt(row, 0);
		p_cust_name=toMySQL((String)ct.model.getValueAt(row, 1));
		p_bday=(String)ct.model.getValueAt(row, 2);
		if(ct.model.getValueAt(row, 3).equals("남자")) p_sex="1";	
		else p_sex="2";
		p_tel=(String)ct.model.getValueAt(row, 4);
		p_jday=(String)ct.model.getValueAt(row, 5);
		p_point=(String)ct.model.getValueAt(row, 6);
		//p_memo;
	}
	
	public void getData() throws SQLException {
		p_cust_id=rs.getString("cust_id");
		p_cust_name=fromMySQL(rs.getString("cust_name"));
		p_bday=rs.getString("bday");
		if(rs.getString("sex").equals("1")) p_sex="남자";	
		else p_sex="여자";
		p_tel=rs.getString("tel");
		p_jday=rs.getString("jday");
		p_point=rs.getString("point");
		//p_memo=rs.getString("memo");
		items[0]=p_cust_id;
		items[1]=p_cust_name;
		items[2]=p_bday;
		items[3]=p_sex;
		items[4]=p_tel;
		items[5]=p_jday;
		items[6]=p_point;
		//p_memo;
	}
	
	public void viewData() {
		String sql;
		sql="SELECT * FROM customer";
		try{
			makeConnection();
			stmt=con.createStatement();
			ct.model.setRowCount(0);
			rs=stmt.executeQuery(sql);
			while(rs.next()){
				getData();
				ct.model.addRow(items);
			}
		}catch(SQLException sqle){System.out.println("viewData SQL Error :"+sqle.getMessage());}
		disConnection();
	}
	
	public void deleteData() {
		setData();
		String sql;
		sql="DELETE FROM customer WHERE cust_id='"+p_cust_id+"' AND cust_name='"+p_cust_name+"'";
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


/*class MembershipPanel extends JPanel {
	JPanel point_p;
		
	public MembershipPanel() {
		setLayout(new BorderLayout());
		
		
		point_p = new JPanel();
		
		add(new Coupon_p(),BorderLayout.NORTH);
		add(point_p,BorderLayout.CENTER);
	}
}

class Coupon_p extends JPanel implements ActionListener {
	JPanel stamp_p, text_p;
	JLabel [] coupon_l = new JLabel[10];
	JButton init_btn;
	
	public Coupon_p() {
		BorderLayout bl = new BorderLayout();
		bl.setHgap(10);
		setLayout(bl);
		setBorder(new TitledBorder("쿠폰"));
		
		stamp_p = new JPanel();
		text_p = new JPanel();
		init_btn = new JButton("초기화");
		init_btn.addActionListener(this);
		
		stamp_p.setPreferredSize(new Dimension(312,126));
		stamp_p.setLayout(new GridLayout(2,5));
		//stamp_p.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		stamp_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		
		for(int i=0 ; i<coupon_l.length ; i++) {
			coupon_l[i] = new JLabel();
			coupon_l[i].setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
			//coupon_l[i].setIcon(new ImageIcon("stamp.png"));	//coupon값에 의존하여 스템프 지정
			coupon_l[i].setBackground(Color.WHITE);
			stamp_p.add(coupon_l[i]);
		}
		
		text_p.add(init_btn);
		
		add(stamp_p,BorderLayout.CENTER);
		add(text_p,BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.print(coupon_l[0].getWidth()+" ");	
		System.out.println(coupon_l[0].getHeight());
		System.out.print(stamp_p.getWidth()+" ");
		System.out.println(stamp_p.getHeight());
	}
}*/