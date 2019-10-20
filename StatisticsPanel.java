package GUIProject;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.event.*;

public class StatisticsPanel extends JPanel {
	public StatisticsPanel() {
		setLayout(null);
		add(new GraphSelect());
		add(new SalesGraph());
		add(new SalesGO());
		add(new SSalesTable());
	}
}

class GraphSelect extends JPanel implements ActionListener {
	JComboBox<String> gl;
	String [] gList = {"매출 통계","회원 통계"}; 
	
	public GraphSelect() {
		setLayout(null);
		setBounds(25,25,120,30);
		
		
		gl = new JComboBox<String>(gList);
		gl.setBounds(0,0,120,30);
		gl.setSelectedIndex(0);
		gl.addActionListener(this);
		
		//add(gp[0]);
		
		add(gl);
	}
		
	public void actionPerformed(ActionEvent e) {
		if(gl.getSelectedIndex() == 0) {
			//gp[0].setVisible(true);
			//gp[1].setVisible(false);
		}
		else { 
			//gp[1].setVisible(true);
			//gp[0].setVisible(false);
		}
	}
}

class SalesGO extends GraphOption implements ActionListener, ItemListener, ChangeListener {
	JComboBox<String> typeSelect;
	String [] termType = {"년, 월별","기간선택"};
	JCheckBox mcb;
	JLabel yl,ml;
	JSpinner ys, ms;
	JComboBox<String> sd, ed;
	
	public SalesGO() {
		typeSelect = new JComboBox<String>(termType);
		mcb = new JCheckBox("월별 그래프");
		ys = new JSpinner(new SpinnerNumberModel(2018,2000,9999,1));
		ms = new JSpinner(new SpinnerNumberModel(11,1,12,1));
		yl = new JLabel("년");
		ml = new JLabel("월");
		
		mcb.addActionListener(this);
		ms.setEnabled(false);
		typeSelect.setBounds(0,0,100,30);
		ys.setBounds(110,0,60,30);
		yl.setBounds(180,0,30,30);
		ms.setBounds(200,0,40,30);
		ml.setBounds(250,0,30,30);
		mcb.setBounds(300,0,100,30);
		
		add(typeSelect);
		add(ys);
		add(yl);
		add(ms);
		add(ml);
		add(mcb);
	}

	@Override	//combobox
	public void itemStateChanged(ItemEvent e) {
		typeSelect.getSelectedItem();
	}
	
	@Override	//checkbox
	public void actionPerformed(ActionEvent e) {
		if(mcb.isSelected()) { ms.setEnabled(true); }
		else { ms.setEnabled(false); }
	}

	@Override	//spinner
	public void stateChanged(ChangeEvent e) {
		
	}
}

class SalesGraph extends Graph {
	JLabel [] y1Guide = new JLabel[7];
	JLabel [] y2Guide = new JLabel[6];
	int [] x = new int[12];
	int [] y = new int[12];
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		int [] amount = {152000,167500,196200,123400,160000,
						132000,85000,122580,213000,232000,
						192300,120000};
		int [] frequency = {50,23,24,61,64,29,10,6,32,12,23,56};
		int y1maximum = 240000;//findMaximum(amount);
		int y2maximum = 70;//findMaximum(frequency);
		

		g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[] {2,2},0));
		for(int i=0 ; i<=6 ; i++) {
			/*y1Guide[i] = new JLabel(String.valueOf(y1maximum/6*i));
			y1Guide[i].setHorizontalAlignment(JLabel.RIGHT);
			y1Guide[i].setBounds(0,50+640-(650/6*i),45,20);*/
			g2.drawLine(50,50+650-(660/6*i),800,50+650-(660/6*i));
			
			//add(y1Guide[i]);
			//if(i==6) continue;
			
			/*y2Guide[i] = new JLabel(String.valueOf(y2maximum/5*i));
			y2Guide[i].setHorizontalAlignment(JLabel.LEFT);
			y2Guide[i].setBounds(805,50+640-(650/5*i),45,20);*/
			g2.drawLine(50,50+650-(660/5*i),800,50+650-(660/5*i));

			//add(y2Guide[i]);
			g2.drawString(String.valueOf(y1maximum/6*i),0,50+650-(660/6*i));
			if(i==6) continue;
			g2.drawString(String.valueOf(y2maximum/5*i),805,50+650-(660/5*i));
		}
		
		g2.setColor(new Color(134,229,127));
		g2.setStroke(new BasicStroke(1));
		for(int i=0 ; i<amount.length ; i++) {
			double ratio = (double)amount[i]/(double)y1maximum*100*6.5;
			Shape r = new Rectangle2D.Double(75+i*60,50+650-ratio,35,ratio);
			g2.fill(r);
		}
		g2.setColor(Color.BLACK);
		for(int i=0 ; i<frequency.length ; i++) {
			double ratio = (double)frequency[i]/(double)y2maximum*100*6.5;
			x[i] = (int)(75+17.5+60*i);
			y[i] = (int)(50+660-ratio);
			g2.drawString((i+1)+" 월",x[i]-15,50+660+10);	//x
		}
		g2.setColor(Color.ORANGE);
		g2.setStroke(new BasicStroke(2));
		g2.drawPolyline(x,y,12);
		
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));
		g2.draw(new Line2D.Double(50,700,800,700));	//x
		g2.draw(new Line2D.Double(50,40,50,700));	//left y1
		g2.draw(new Line2D.Double(800,40,800,700));	//right y2
	}
	
	public SalesGraph() {
		
	}
	
	int findMaximum(int [] values) {
		int max;
		max=values[0];
		for(int i=0 ; i<values.length ; i++) {	if(max<values[i]) {	max=values[i];	}	}
		return max;
	}
}

class SSalesTable extends GraphTable {
	public SSalesTable() {
		String [] attribute = {"월","금액","증감","횟수","증감"};
		String [][] instance = {{"1월","152000","","50",""},
								{"2월","167500","","23",""},
								{"3월","196200","","24",""},
								{"4월","123400","","61",""},
								{"5월","160000","","64",""},
								{"6월","132000","","29",""},
								{"7월","85000","","10",""},
								{"8월","122580","","6",""},
								{"9월","213000","","32",""},
								{"10월","232000","","12",""},
								{"11월","192300","","23",""},
								{"12월","120000","","56",""}};
		table = new JTable(instance, attribute);
		jsp = new JScrollPane(table);
		
		table.setFont(new Font("돋움체",Font.PLAIN,14));
		
		add(jsp,BorderLayout.CENTER);
	}
}

class GraphOption extends JPanel {
	public GraphOption() {
		setLayout(null);
		setBounds(150,25,400,30);
	}
}

class Graph extends JPanel {
	
	public Graph() {
		setLayout(new BorderLayout());
		setBounds(25,80,850,800);
	}
}

/*class GraphTableOption extends JPanel {
	public GraphTableOption() {
		setLayout(null);
		setBounds(850,25,400,30);
	}
}
*/
class GraphTable extends JPanel {
	JTable table;
	JScrollPane jsp;
	
	public GraphTable() {
		setLayout(new BorderLayout());
		setBounds(900,25,350,750);
	}
}