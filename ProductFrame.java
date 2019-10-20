package GUIProject;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class ProductFrame extends JFrame implements ActionListener, ListSelectionListener {
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension ScreenSize = kit.getScreenSize();
	
	JPanel p, west_p, center_p;

	String p_pd_group;
	String p_pd_id;
	String p_pd_name;
	String p_pd_price;
	String p_isSale;
	String [] items = new String[5];
	
		
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	ProductGroupPanel pg_p;
	ProductTablePanel pt_p;
	
	public ProductFrame() {
		setSize(800,600);
		setLocation(ScreenSize.width/2-getWidth()/2, ScreenSize.height/2-getHeight()/2);
		
		
		BorderLayout bl = new BorderLayout();
		bl.setHgap(10);
		
		p = new JPanel(bl);
		west_p = new JPanel();
		center_p = new JPanel();

		p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		
		pg_p = new ProductGroupPanel();
		pt_p = new ProductTablePanel();
	
		pg_p.pg.groupList_l.addListSelectionListener(this);
		for(int i=0 ; i<pt_p.ptcp.btn.length ; i++) {pt_p.ptcp.btn[i].addActionListener(this);}
		
		west_p.add(pg_p);
		center_p.add(pt_p);
		
		p.add(west_p,BorderLayout.WEST);
		p.add(center_p,BorderLayout.CENTER);
		add(p);
		
		setVisible(true);
	}
	
	class ProductGroupPanel extends JPanel implements ActionListener {
		ProductGroup pg;
		ProductGroupCP pgcp;
		
		
		public ProductGroupPanel() {
			setLayout(new BorderLayout());
			
			pg = new ProductGroup();
			pgcp = new ProductGroupCP();
			viewGroup();
			
			for(int i=0 ; i<pgcp.btn.length ; i++) pgcp.btn[i].addActionListener(this);
			
			add(pg,BorderLayout.CENTER);
			add(pgcp,BorderLayout.SOUTH);
		}
		
		class GroupInputFrame extends JFrame implements ActionListener {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension ScreenSize = kit.getScreenSize();
			
			JPanel p, input_p, btn_p;
			JLabel groupName_lbl;
			JTextField groupName_tf;
			JButton cancel_btn, ok_btn;
			String groupName, updateName;
			boolean isUpdate;
			
			public GroupInputFrame() {
				p = new JPanel(new BorderLayout());
				input_p = new JPanel();
				btn_p = new JPanel();
				
				groupName_lbl = new JLabel("상품 그룹명");
				groupName_tf = new JTextField(10);
				cancel_btn = new JButton("취소");
				ok_btn = new JButton("입력");
				
				input_p.setBorder(new TitledBorder("상품 등록"));
				groupName_tf.addActionListener(this);
				cancel_btn.addActionListener(this);
				ok_btn.addActionListener(this);
				
				input_p.add(groupName_lbl);
				input_p.add(groupName_tf);
				btn_p.add(cancel_btn);
				btn_p.add(ok_btn);
				
				p.add(input_p,BorderLayout.CENTER);
				p.add(btn_p,BorderLayout.SOUTH);
				add(p);
				isUpdate=false;
				
				pack();
				setLocation(ScreenSize.width/2-getWidth()/2, ScreenSize.height/2-getHeight()/2);
				setVisible(true);
			}
			
			public GroupInputFrame(String beforeGroupName) {
				this();
				input_p.setBorder(new TitledBorder("상품 수정"));
				groupName = beforeGroupName;
				groupName_tf.setText(groupName);
				isUpdate=true;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==groupName_tf | e.getSource()==ok_btn) {
					if(!isUpdate) {
						String sql;
						groupName = groupName_tf.getText();
						sql = "INSERT INTO product_group VALUES ('"+toMySQL(groupName)+"')";
						inputSQL(sql);
						viewGroup();
						dispose();
					}
					else {
						String sql;
						updateName = toMySQL(groupName_tf.getText());
						groupName = toMySQL(groupName);
						sql = "UPDATE product_group SET pd_group='"+updateName+"' WHERE pd_group='"+groupName+"'";
						inputSQL(sql);
						sql = "UPDATE product SET pd_group='"+updateName+"' WHERE pd_group='"+groupName+"'";
						inputSQL(sql);
						viewGroup();
						dispose();
					}
				}
				else {viewGroup(); dispose();}
			}
			
		}
		
		public void viewGroup() {
			try {
				makeConnection();
				stmt=con.createStatement();
				String sql;
				sql = "SELECT * FROM product_group";
				rs = stmt.executeQuery(sql);
				pg.model.removeAllElements();
				pg.model.add("전체그룹");
				while(rs.next()) {
					pg.model.add(fromMySQL(rs.getString("pd_group")));
				}
				pg.groupList_l.setListData(pg.model);
			} catch(SQLException sqle) {System.out.println("viewGroup: SQL Error-"+sqle.getMessage());}
			disConnection();
		}
		
		public void deleteData() {
			if(pg.selectedGroup.equals("%")) JOptionPane.showMessageDialog(null, "전체그룹은 삭제할 수 없습니다.");
			else {
				String sql;
				p_pd_group = pg.groupList_l.getSelectedValue();
				int isYes = JOptionPane.showConfirmDialog(null, "그룹과 그룹에 해당하는 상품들을 모두 삭제하시겠습니까?","확인",JOptionPane.YES_NO_OPTION);
				System.out.println("isYes : "+isYes);
				if(isYes==0) {
					sql="DELETE FROM product_group WHERE pd_group='"+toMySQL(p_pd_group)+"'";
					inputSQL(sql);
				}
				viewGroup();
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==pgcp.btn[0]) {new GroupInputFrame();}
			else if(e.getSource()==pgcp.btn[1]) {
				if(!pg.groupList_l.isSelectionEmpty()) new GroupInputFrame(pg.groupList_l.getSelectedValue());
				else JOptionPane.showMessageDialog(null, "그룹을 선택해주세요");
				}
			else if(e.getSource()==pgcp.btn[2]) {
				if(!pg.groupList_l.isSelectionEmpty()) deleteData();
				else JOptionPane.showMessageDialog(null, "그룹을 선택해주세요");
				}
		}
	}
	
	

	class ProductTablePanel extends JPanel {
		JPanel p;
		ProductTable pt;
		ProductTableCP ptcp;

		public ProductTablePanel() {
			p = new JPanel(new BorderLayout());
			pt = new ProductTable();
			ptcp = new ProductTableCP();

			p.add(pt, BorderLayout.CENTER);
			p.add(ptcp, BorderLayout.SOUTH);
			add(p);
		}
	}
	
	class ProductInputFrame extends JFrame implements ActionListener {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension ScreenSize = kit.getScreenSize();
		
		JPanel center_p, south_p;
		String [] lblNames = {"그룹","번호","상품명","가격","판매"};
		int inputNum = lblNames.length;
		JPanel [] p = new JPanel[inputNum];
		JLabel [] lbl = new JLabel[inputNum];
		JTextField [] tf = new JTextField [3];
		JComboBox<String> group_cb;
		JComboBox<String> isSale_cb;
		JButton cancel_btn, ok_btn;
		JCheckBox dup_chkb;
		String beforeid;
		boolean isUpdate;
		
		public ProductInputFrame() {
			setLayout(new BorderLayout());
			
			center_p = new JPanel(new GridLayout(5,1));
			south_p = new JPanel();
			cancel_btn = new JButton("취소");
			ok_btn = new JButton("입력");
			String [] isSale = {"판매가능","판매불가"};
			group_cb = new JComboBox<String>();
			isSale_cb = new JComboBox<String>(isSale);
			dup_chkb = new JCheckBox("중복검사");
			beforeid = new String();
			
			for(int i=0 ; i<p.length ; i++) {
				p[i] = new JPanel();
				lbl[i] = new JLabel(lblNames[i]);
				p[i].add(lbl[i]);
				if(i==0) {
					group_cb = new JComboBox<String>();
					p[i].add(group_cb);
				}
				else if(i>=1 && i<=3) {
					tf[i-1] = new JTextField(10);
					p[i].add(tf[i-1]);
					if(i==1) p[i].add(dup_chkb);
				}
				else p[i].add(isSale_cb);
				center_p.add(p[i]);
			}
			initGroup();
			cancel_btn.addActionListener(this);
			ok_btn.addActionListener(this);
			dup_chkb.addActionListener(this);
			
			south_p.add(cancel_btn);
			south_p.add(ok_btn);
			
			add(center_p,BorderLayout.CENTER);
			add(south_p,BorderLayout.SOUTH);
			isUpdate = false;
			pack();
			setLocation(ScreenSize.width/2-getWidth()/2, ScreenSize.height/2-getHeight()/2);
			setVisible(true);
		}
		
		public ProductInputFrame(String str) {
			this();
			getData();
			beforeid = p_pd_id;
			group_cb.setSelectedItem(p_pd_group);
			tf[0].setText(p_pd_id);
			tf[1].setText(p_pd_name);
			tf[2].setText(p_pd_price);
			if(p_isSale.equals("판매가능")) isSale_cb.setSelectedIndex(0);
			else isSale_cb.setSelectedIndex(1);
			tf[0].setEnabled(false);
			dup_chkb.setSelected(true);
			isUpdate = true;
		}
		
		public void initGroup() {
			try {
				makeConnection();
				stmt=con.createStatement();
				String sql;
				sql = "SELECT * FROM product_group";
				rs=stmt.executeQuery(sql);
				while(rs.next()) {
					group_cb.addItem(fromMySQL(rs.getString("pd_group")));
				}
			} catch(SQLException sqle) {System.out.println("initGroup error : "+sqle.getMessage());}
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
					sql = "SELECT COUNT(*) FROM product WHERE pd_group='"+toMySQL((String)group_cb.getSelectedItem())+"' AND pd_id='"+toMySQL(tf[0].getText())+"'";
					rs = stmt.executeQuery(sql);
					rs.next();
					int checkID=rs.getInt("COUNT(*)");
				
					if(checkID==0 | beforeid.equals(tf[0].getText())) {tf[0].setEnabled(false);}
					else {
						dup_chkb.setSelected(false);
						JOptionPane.showMessageDialog(null, "중복된 번호입니다.");
					}
				} catch(SQLException sqle) {System.out.println("isDuplicated error : "+sqle.getMessage());}
				disConnection();
			}
			else if(!dup_chkb.isSelected()) tf[0].setEnabled(true);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==ok_btn) {
				if(dup_chkb.isSelected()) {
					if(!isUpdate) {
						String sql;
						setData();
						sql = "INSERT INTO product (pd_group,pd_id,pd_name,pd_price,isSale) VALUES ";
						sql += "('"+p_pd_group+"','"+p_pd_id+"','"+p_pd_name+"','"+p_pd_price+"','"+p_isSale+"')";
						inputSQL(sql);
						viewData(p_pd_group);
						dispose();
					}
					else {	//update
						String sql;
						setData();
						sql = "UPDATE product SET pd_id='"+p_pd_id+"' WHERE pd_group='"+p_pd_group+"' AND pd_id='"+beforeid+"'";
						inputSQL(sql);
						sql = "UPDATE product SET pd_name='"+p_pd_name+"' WHERE pd_group='"+p_pd_group+"' AND pd_id='"+p_pd_id+"'";
						inputSQL(sql);
						sql = "UPDATE product SET pd_price='"+p_pd_price+"' WHERE pd_group='"+p_pd_group+"' AND pd_id='"+p_pd_id+"'";
						inputSQL(sql);
						sql = "UPDATE product SET isSale='"+p_isSale+"' WHERE pd_group='"+p_pd_group+"' AND pd_id='"+p_pd_id+"'";
						inputSQL(sql);
						viewData(p_pd_group);
						dispose();
						}
				}
				else JOptionPane.showMessageDialog(null, "중복검사를 해주세요.");
			}
			else if(e.getSource()==cancel_btn) {dispose();}
			else if(e.getSource()==dup_chkb) {isDuplicated();}
		}
		
		public void setData() {
			p_pd_group = toMySQL((String)group_cb.getSelectedItem());
			p_pd_id = tf[0].getText();
			p_pd_name = toMySQL(tf[1].getText());
			p_pd_price = tf[2].getText();
			if(isSale_cb.getSelectedIndex()==0) p_isSale = "1";
			else p_isSale = "0";
		}
		
		public void getData() {
			int row = pt_p.pt.table.getSelectedRow();
			p_pd_group = (String)pt_p.pt.model.getValueAt(row, 0);
			p_pd_id = (String)pt_p.pt.model.getValueAt(row, 1);
			p_pd_name = (String)pt_p.pt.model.getValueAt(row, 2);
			p_pd_price = (String)pt_p.pt.model.getValueAt(row, 3);
			p_isSale = (String)pt_p.pt.model.getValueAt(row, 4);
		}
	}
	
	class ProductGroup extends JPanel {
		JLabel groupList_lbl;
		JList<String> groupList_l;
		Vector<String> model;
		String selectedGroup;
		
		public ProductGroup() {
			setLayout(new BorderLayout());
			
			model = new Vector<String>();
			groupList_lbl = new JLabel("상품 그룹");
			groupList_lbl.setFont(new Font("돋움체",Font.BOLD,20));
			groupList_l = new JList<String>();
			groupList_l.setFont(new Font("돋움체",Font.BOLD,17));
			
			add(groupList_lbl,BorderLayout.NORTH);
			add(groupList_l,BorderLayout.CENTER);
		}
	}

	class ProductGroupCP extends JPanel {
		JButton [] btn = new JButton[3];
		String [] str = {"등록","수정","삭제"};
		
		public ProductGroupCP() {
			
			for(int i=0 ; i<btn.length ; i++) {
				btn[i] = new JButton(str[i]);
				btn[i].setMargin(new Insets(0,0,0,0));
				btn[i].setFont(new Font("돋움체",Font.PLAIN,13));
				add(btn[i]);
			}
		}
	}

	class ProductTable extends JPanel {
		JLabel productList_lbl;
		JTable table;
		JScrollPane jsp;
		DefaultTableModel model;
		
		public ProductTable() {
			setLayout(new BorderLayout());
			
			String [] columnNames = {"그룹","번호","상품명","가격","판매"};
			model = new DefaultTableModel(columnNames,0) {
				@Override
				public boolean isCellEditable(int row, int column) {return false;}
			};
			
			table = new JTable(model);
			jsp = new JScrollPane(table);

			productList_lbl = new JLabel("상품 목록");
			productList_lbl.setFont(new Font("돋움체",Font.BOLD,20));
			
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			model.setColumnIdentifiers(columnNames);
			table.setFillsViewportHeight(true);
			
			add(productList_lbl,BorderLayout.NORTH);
			add(jsp,BorderLayout.CENTER);
		}
	}

	class ProductTableCP extends JPanel {
		JButton [] btn = new JButton[3];
		String [] str = {"등록","수정","삭제"};
		
		public ProductTableCP() {
			for(int i=0 ; i<btn.length ; i++) {
				btn[i] = new JButton(str[i]);
				btn[i].setPreferredSize(new Dimension(40,20));
				btn[i].setMargin(new Insets(0,0,0,0));
				btn[i].setFont(new Font("돋움체",Font.PLAIN,13));
				add(btn[i]);
			}
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(pg_p.pg.groupList_l.getSelectedIndex()==0) {pg_p.pg.selectedGroup = "%";}	//전체그룹
		else {pg_p.pg.selectedGroup = pg_p.pg.groupList_l.getSelectedValue();}
		viewData(toMySQL(pg_p.pg.selectedGroup));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==pt_p.ptcp.btn[0]) {new ProductInputFrame();}
		else if(e.getSource()==pt_p.ptcp.btn[1]) {
			if(!(pt_p.pt.table.getSelectedRowCount()==0)) {new ProductInputFrame("UPDATE");}
			else JOptionPane.showMessageDialog(null, "상품목록을 선택해 주세요.");
		}
		else if(e.getSource()==pt_p.ptcp.btn[2]) {
			if(!(pt_p.pt.table.getSelectedRowCount()==0)) {deleteData();}
			else JOptionPane.showMessageDialog(null, "상품목록을 선택해 주세요.");
		}
	}
	
	public void setData() {
		int row = pt_p.pt.table.getSelectedRow();
		p_pd_group = toMySQL((String)pt_p.pt.model.getValueAt(row, 0));
		p_pd_id = (String)pt_p.pt.model.getValueAt(row, 1);
		p_pd_name = toMySQL((String)pt_p.pt.model.getValueAt(row, 2));
		p_pd_price = (String)pt_p.pt.model.getValueAt(row, 3);
		p_isSale = (String)pt_p.pt.model.getValueAt(row, 4);
	}
	
	public void getData() throws SQLException {
		p_pd_group=fromMySQL(rs.getString("pd_group"));
		p_pd_id=rs.getString("pd_id");
		p_pd_name=fromMySQL(rs.getString("pd_name"));
		p_pd_price=rs.getString("pd_price");
		if(rs.getString("isSale").equals("0")) p_isSale="판매불가";
		else p_isSale="판매가능";
		items[0]=p_pd_group;
		items[1]=p_pd_id;
		items[2]=p_pd_name;
		items[3]=p_pd_price;
		items[4]=p_isSale;
	}
	
	public void viewData(String selectedGroup) {
		String sql="";
		sql="SELECT * FROM product WHERE pd_group LIKE'"+selectedGroup+"' ORDER BY pd_group,pd_id";
		try{
			makeConnection();
			stmt=con.createStatement();
			pt_p.pt.model.setRowCount(0);
			rs=stmt.executeQuery(sql);
			while(rs.next()){
				getData();
				pt_p.pt.model.addRow(items);
			}
		}catch(SQLException sqle){System.out.println("viewData: SQL Error-"+sqle.getMessage());}
		disConnection();
	}
	
	public void deleteData() {
		setData();
		String sql;
		sql="DELETE FROM product WHERE pd_group='"+p_pd_group+"' AND pd_id='"+p_pd_id+"'";
		inputSQL(sql);
		viewData(p_pd_group);
	}

	public void inputSQL(String sql) {
		try {
			makeConnection();
			stmt=con.createStatement();
			System.out.println(sql);
			int isExecute = stmt.executeUpdate(sql);
			if(isExecute==1) System.out.println("Execute Successfully");
			else System.out.println("Execute Failed");
		} catch(SQLException sqle) {System.out.println("Added : SQL Error"+sqle.getMessage());}
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
			System.out.println("드라이브 적재 성공");
			con=DriverManager.getConnection(url, id, password);
			System.out.println("데이터베이스 연결 성공");
		}catch(ClassNotFoundException e){
			System.out.println("드라이버를 찾을 수 없습니다");
			e.getStackTrace();
		}catch(SQLException e){
			System.out.println("연결에 실패하였습니다");			
		}
		return con;
	}
	
}