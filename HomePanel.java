package GUIProject;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;

public class HomePanel extends JPanel implements MouseListener, ActionListener {
	JPanel center_p, south_p, btn_p;
	JButton product_btn;
	JButton [] b = new JButton[3];
	Calendar calendar;
	TodoPanel todo_p;
	
	Vector<String> model;
	String p_date;
	String p_todo;
	
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	public HomePanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0,15,15,15));
		
		calendar = new Calendar();
		todo_p = new TodoPanel();
		center_p = new JPanel(new BorderLayout());
		south_p = new JPanel(new BorderLayout());
		btn_p = new JPanel(new GridLayout(4,1));
		product_btn = new JButton("상품관리");
		
		for(int i=1 ; i<calendar.date_lbl.length ; i++) {
			for(int j=0 ; j<calendar.date_lbl[i].length ; j++) {
				calendar.date_lbl[i][j].addMouseListener(this);
			}
		}
		
		for(int i=0 ; i<b.length ; i++) {
			String [] str = {"삽입","수정","삭제"};
			b[i] = new JButton(str[i]);
			b[i].addActionListener(this);
			btn_p.add(b[i]);
		}
		product_btn.addActionListener(this);
		btn_p.add(product_btn);
		
		center_p.add(calendar,BorderLayout.CENTER);
		south_p.add(todo_p,BorderLayout.CENTER);
		south_p.add(btn_p,BorderLayout.EAST);
		
		System.out.println(calendar.selectedDate);
		viewData();
		viewTodomark();
		
		add(center_p,BorderLayout.CENTER);
		add(south_p,BorderLayout.SOUTH);
	}
	
	class TodoPanel extends JPanel {
		JLabel date;
		JTextArea todo;
		
		public TodoPanel() {
			setLayout(new BorderLayout());
			
			date = new JLabel();
			todo = new JTextArea();
			
			date.setText(calendar.today+" 일의 주요업무");
			date.setFont(new Font("굴림체",Font.ITALIC,20));
			todo.setFont(new Font("굴림체",Font.PLAIN,17));
			todo.setPreferredSize(new Dimension(100,235));
			
			add(date,BorderLayout.NORTH);
			add(todo,BorderLayout.CENTER);
		}
	}
	
	public void viewTodomark() {	//todo
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Date date2;
		try {
			makeConnection();
			stmt=con.createStatement();
			String sql;
			sql = "SELECT * from calendar WHERE date>='"+calendar.ym_lbl.getText()+"-01' AND date<=LAST_DAY('"+calendar.today+"')";
			rs=stmt.executeQuery(sql);
			rs.next();
			for(int i=1 ; i<calendar.date_lbl.length ; i++) {
				for(int j=0 ; j<calendar.date_lbl[i].length ; j++) {
					if(!calendar.date_lbl[i][j].getText().equals("")) {
						try {
							if(!rs.isAfterLast()) {
								date = f.parse(calendar.ym_lbl.getText()+"-"+calendar.date_lbl[i][j].getText());
								date2 = f.parse(rs.getString("date"));
								if(date.equals(date2)) {
									calendar.date_lbl[i][j].setIcon(new ImageIcon("stamp.png"));
									rs.next();
								}
								else {
									calendar.date_lbl[i][j].setIcon(null);
								}
							}
							else calendar.date_lbl[i][j].setIcon(null);
						} catch (ParseException e) {e.printStackTrace();}	
					}
				}
			}			
		} catch(SQLException e) {System.out.println(e.getMessage());}
		disConnection();
		
	}
	
	
	public void selectDate(JLabel selected_lbl) {
		for(int i=1 ; i<calendar.date_lbl.length ; i++) {
			for(int j=0 ; j<calendar.date_lbl[i].length ; j++) {
				calendar.date_lbl[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
		}
		selected_lbl.setBorder(BorderFactory.createLineBorder(Color.YELLOW,3));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		for(int i=1 ; i<calendar.date_lbl.length ; i++) {
			for(int j=0 ; j<calendar.date_lbl[i].length ; j++) {
				if(e.getSource()==calendar.date_lbl[i][j] & !calendar.date_lbl[i][j].getText().equals("")) {
					calendar.selectedDate = calendar.ym_lbl.getText()+"-"+calendar.date_lbl[i][j].getText();
					todo_p.date.setText(calendar.selectedDate+" 일의 주요업무");
					selectDate(calendar.date_lbl[i][j]);
					viewData();
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==product_btn) new ProductFrame();
		else if(e.getSource()==b[0]) insertData();
		else if(e.getSource()==b[1]) updateData();
		else if(e.getSource()==b[2]) deleteData();
	}
	
	public void insertData() {
		try {
			makeConnection();
			stmt=con.createStatement();
			String sql;
			setData();
			sql = "SELECT COUNT(*) FROM calendar WHERE date'"+toMySQL(calendar.selectedDate)+"'";
			rs=stmt.executeQuery(sql);
			rs.next();
			if(!rs.getString("COUNT(*)").equals("0")) {
				sql = "INSERT INTO calendar VALUES ('"+p_date+"','"+p_todo+"')";
				System.out.println(sql);
				inputSQL(sql);
				viewData();
				viewTodomark();
			}
			else updateData();
		} catch(SQLException sqle) {System.out.println("insertData Error :"+sqle.getMessage());}
		disConnection();
	}
	
	public void updateData() {
		setData();
		String sql;
		sql = "UPDATE calendar SET todo='"+p_todo+"' WHERE date='"+p_date+"'";
		inputSQL(sql);
		viewData();
	}
	
	public void deleteData() {
		setData();
		String sql;
		sql = "DELETE FROM calendar WHERE date='"+p_date+"'";
		inputSQL(sql);
		viewData();
		viewTodomark();
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
	
	public void viewData() {
		String sql;
		try {
			makeConnection();
			stmt=con.createStatement();
			sql = "SELECT COUNT(*) FROM calendar WHERE date='"+toMySQL(calendar.selectedDate)+"'";
			rs=stmt.executeQuery(sql);
			rs.next();
			if(!rs.getString("COUNT(*)").equals("0")) {
				sql = "SELECT todo FROM calendar WHERE date='"+toMySQL(calendar.selectedDate)+"'";
				rs=stmt.executeQuery(sql);
				todo_p.todo.removeAll();
				rs.next();
				p_todo = fromMySQL(rs.getString("todo"));
				todo_p.todo.setText(p_todo);
			}
			else todo_p.todo.setText("");
		} catch(SQLException sqle) {System.out.println("viewData SQL Error :"+sqle.getMessage());}
		disConnection();
	}
	
	public void setData() {
		p_date = calendar.selectedDate;
		p_todo = toMySQL(todo_p.todo.getText());
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

class Calendar extends Calendar_Panel {
	String selectedDate;
	
	public Calendar() {
		setBorder(BorderFactory.createEmptyBorder(0,0,15,15));
		for(int i=0 ; i<date_lbl[0].length ; i++) {	//요일 크기 설정
			//date_lbl[0][i].
		}
		
		calendarSet();
		selectedDate = today;
		ym_lbl.setFont(new Font("돋움체",Font.BOLD,20));
		today_p.setVisible(false);
	}
	
	public void calendarSet() {
		for(int i=1 ; i<date_lbl.length ; i++) {
			for(int j=0 ; j<date_lbl[i].length ; j++) {
				//date_lbl[i][j].mer	text여백설정
				date_lbl[i][j].setHorizontalAlignment(JLabel.RIGHT);
				date_lbl[i][j].setVerticalAlignment(JLabel.TOP);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			makeConnection();
			stmt = con.createStatement();
			String sql;
			if(e.getSource()==downMon_btn) {
				sql = "SELECT DATE_FORMAT(DATE_ADD('"+ym_lbl.getText()+"-01',INTERVAL +1 MONTH),'%Y-%m') AS ym FROM dual";
				rs = stmt.executeQuery(sql);
				rs.next();
				ym_lbl.setText(fromMySQL(rs.getString("ym")));
				initCalendar();
				calendarSet();
			}
			else {
				sql = "SELECT DATE_FORMAT(DATE_ADD('"+ym_lbl.getText()+"-01',INTERVAL -1 MONTH),'%Y-%m') AS ym FROM dual";
				rs = stmt.executeQuery(sql);
				rs.next();
				ym_lbl.setText(fromMySQL(rs.getString("ym")));
				initCalendar();
				calendarSet();
			}
		} catch(SQLException sqle) {System.out.println("calendar : change month error");}
	}
}

class Calendar_Panel extends JPanel implements ActionListener {
	int row = 7;
	int column = 7;
	JPanel p, ym_p, date_p, today_p;
	JButton upMon_btn, downMon_btn;
	JLabel ym_lbl, today_lbl;
	JLabel [][] date_lbl = new JLabel[row][column];
	Font f = new Font("돋움체",Font.PLAIN,14);
	String today;
	
	
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	public Calendar_Panel() {
		FlowLayout fl = new FlowLayout();
		fl.setHgap(5);
		ym_p = new JPanel();
		date_p = new JPanel(new GridLayout(row,column));
		today_p = new JPanel();
		
		setLayout(new BorderLayout());
		
		try{
			makeConnection();
			stmt=con.createStatement();
			String sql;
			
			sql = "SELECT DATE_FORMAT(CURDATE(),'%Y-%m-%d') AS today FROM dual";
			rs=stmt.executeQuery(sql);
			rs.next();
			today=fromMySQL(rs.getString("today"));
			today_lbl = new JLabel("오늘날짜 : "+today);
			today_lbl.setHorizontalAlignment(JLabel.CENTER);
			today_lbl.setFont(f);
			
			sql = "SELECT DATE_FORMAT(CURDATE(),'%Y-%m') AS today FROM dual";
			rs=stmt.executeQuery(sql);
			rs.next();
			ym_lbl = new JLabel(fromMySQL(rs.getString("today")));
			ym_lbl.setHorizontalAlignment(JLabel.CENTER);
			ym_lbl.setFont(f);
			
			upMon_btn = new JButton("<");
			downMon_btn = new JButton(">");
			upMon_btn.addActionListener(this);
			downMon_btn.addActionListener(this);
			
			ym_p.add(upMon_btn);
			ym_p.add(ym_lbl);
			ym_p.add(downMon_btn);
			today_p.add(today_lbl);
			
			date_lbl[0][0] = new JLabel("일");
			date_lbl[0][1] = new JLabel("월");
			date_lbl[0][2] = new JLabel("화");
			date_lbl[0][3] = new JLabel("수");
			date_lbl[0][4] = new JLabel("목");
			date_lbl[0][5] = new JLabel("금");
			date_lbl[0][6] = new JLabel("토");
			for(int i=0 ; i<date_lbl[0].length ; i++) {
				date_lbl[0][i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				date_lbl[0][i].setHorizontalAlignment(JLabel.CENTER);
				date_lbl[0][i].setFont(f);
				date_p.add(date_lbl[0][i]);
			}
			
			for(int i=1 ; i<date_lbl.length ; i++) {
				for(int j=0 ; j<date_lbl[i].length ; j++) {
					date_lbl[i][j] = new JLabel();
					date_p.add(date_lbl[i][j]);
				}
			}
			initCalendar();
			for(int i=1 ; i<date_lbl.length ; i++) {
				for(int j=0 ; j<date_lbl[i].length ; j++) {
					if(!date_lbl[i][j].getText().equals("")) {
						SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
						Date date1 = f.parse(ym_lbl.getText()+"-"+date_lbl[i][j].getText());
						Date date2 = f.parse(today);
						if(date1.equals(date2))
							date_lbl[i][j].setBorder(BorderFactory.createLineBorder(Color.YELLOW,3));
					}
				}
			}
			
			add(ym_p,BorderLayout.NORTH);
			add(date_p,BorderLayout.CENTER);
			add(today_p,BorderLayout.SOUTH);
		} catch(SQLException sqle) {System.out.println("Calendar_Panel : sql error");} 
		catch (ParseException e) {e.printStackTrace();}
		disConnection();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			makeConnection();
			stmt = con.createStatement();
			String sql;
			if(e.getSource()==downMon_btn) {
				sql = "SELECT DATE_FORMAT(DATE_ADD('"+ym_lbl.getText()+"-01',INTERVAL +1 MONTH),'%Y-%m') AS ym FROM dual";
				rs = stmt.executeQuery(sql);
				rs.next();
				ym_lbl.setText(fromMySQL(rs.getString("ym")));
				initCalendar();
			}
			else {
				sql = "SELECT DATE_FORMAT(DATE_ADD('"+ym_lbl.getText()+"-01',INTERVAL -1 MONTH),'%Y-%m') AS ym FROM dual";
				rs = stmt.executeQuery(sql);
				rs.next();
				ym_lbl.setText(fromMySQL(rs.getString("ym")));
				initCalendar();
			}
		} catch(SQLException sqle) {System.out.println("calendar : change month error");}
	}
	
	public void initCalendar() {
		try {
			String sql;
			makeConnection();
			stmt=con.createStatement();
			sql = "SELECT ym\r\n" + 
					"     , MIN(CASE dw WHEN 1 THEN d END) Sun\r\n" + 
					"     , MIN(CASE dw WHEN 2 THEN d END) Mon\r\n" + 
					"     , MIN(CASE dw WHEN 3 THEN d END) Tue\r\n" + 
					"     , MIN(CASE dw WHEN 4 THEN d END) Wed\r\n" + 
					"     , MIN(CASE dw WHEN 5 THEN d END) Thu\r\n" + 
					"     , MIN(CASE dw WHEN 6 THEN d END) Fri\r\n" + 
					"     , MIN(CASE dw WHEN 7 THEN d END) Sat\r\n" + 
					"  FROM (SELECT date_format(dt,'%Y%m') ym\r\n" + 
					"             , Week(dt) w\r\n" + 
					"             , Day(dt) d\r\n" + 
					"             , DayofWeek(dt) dw\r\n" + 
					"          FROM (SELECT '"+ym_lbl.getText()+"-01' + INTERVAL a*10 + b DAY dt\r\n" + 
					"                  FROM (SELECT 0 a\r\n" + 
					"                        UNION ALL SELECT 1\r\n" + 
					"                        UNION ALL SELECT 2\r\n" + 
					"                        UNION ALL SELECT 3\r\n" + 
					"                        ) a\r\n" + 
					"                     , (SELECT 0 b\r\n" + 
					"                        UNION ALL SELECT 1\r\n" + 
					"			UNION ALL SELECT 2\r\n" + 
					"                        UNION ALL SELECT 3\r\n" + 
					"			UNION ALL SELECT 4\r\n" + 
					"			UNION ALL SELECT 5\r\n" + 
					"			UNION ALL SELECT 6\r\n" + 
					"			UNION ALL SELECT 7\r\n" + 
					"			UNION ALL SELECT 8\r\n" + 
					"			UNION ALL SELECT 9\r\n" + 
					"                        ) b\r\n" + 
					"                 WHERE DayOfYear('"+ym_lbl.getText()+"-01') + a*10 + b <= DayOfYear(LAST_DAY('"+ym_lbl.getText()+"-01'))\r\n" + 
					"                ) a\r\n" + 
					"        ) a\r\n" + 
					" GROUP BY ym, w\r\n" + 
					";";
			rs = stmt.executeQuery(sql);
							
			for(int i=1 ; i<date_lbl.length ; i++) {
				if(rs.next()) {
					for(int j=0 ; j<date_lbl[i].length ; j++) {
						switch(j){
							case 0:
								if(rs.getString("Sun")==null) {date_lbl[i][j].setText(""); break;}
								date_lbl[i][j].setText(fromMySQL(rs.getString("Sun")));
								date_lbl[i][j].setForeground(Color.RED);;
								break;
							case 1:
								if(rs.getString("Mon")==null) {date_lbl[i][j].setText(""); break;}
								date_lbl[i][j].setText(fromMySQL(rs.getString("Mon")));
								break;
							case 2:
								if(rs.getString("Tue")==null) {date_lbl[i][j].setText(""); break;}
								date_lbl[i][j].setText(fromMySQL(rs.getString("Tue")));
								break;
							case 3:
								if(rs.getString("Wed")==null) {date_lbl[i][j].setText(""); break;}
								date_lbl[i][j].setText(fromMySQL(rs.getString("Wed")));
								break;
							case 4:
								if(rs.getString("Thu")==null) {date_lbl[i][j].setText(""); break;}
								date_lbl[i][j].setText(fromMySQL(rs.getString("Thu")));
								break;
							case 5:
								if(rs.getString("Fri")==null) {date_lbl[i][j].setText(""); break;}
								date_lbl[i][j].setText(fromMySQL(rs.getString("Fri")));
								break;
							case 6:
								if(rs.getString("Sat")==null) {date_lbl[i][j].setText(""); break;}
								date_lbl[i][j].setText(fromMySQL(rs.getString("Sat")));
								date_lbl[i][j].setForeground(Color.BLUE);
								break;
						}
						date_lbl[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
						date_lbl[i][j].setHorizontalAlignment(JLabel.CENTER);
						date_lbl[i][j].setFont(f);
					}
				}
				else for(int j=0 ; j<date_lbl[i].length ; j++) {date_lbl[i][j].setText("");}
				for(int j=0 ; j<date_lbl[i].length ; j++) {
					date_lbl[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
					date_lbl[i][j].setHorizontalAlignment(JLabel.CENTER);
					date_lbl[i][j].setFont(f);
				}
			}
		} catch(SQLException sqle) {System.out.println("Calendar : init error");}
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