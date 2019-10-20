package GUIProject;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class LoginFrame extends JFrame implements ActionListener {
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension ScreenSize = kit.getScreenSize();
	private JPanel inp, bp;
	private JLabel idl, pwl;
	private JTextField idtf;
	private JPasswordField pwpf;
	private JButton enter;
	private String id = "admin";
	private String pw = "1234";
	int i = 0;
	
	public LoginFrame() {
		setSize(200,150);
		setLocation(ScreenSize.width/2-getWidth()/2, ScreenSize.height/2-getHeight()/2);
		setTitle("�α���");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);
		
		inp = new JPanel();
		idl = new JLabel(" ID");
		pwl = new JLabel(" Password");
		idtf = new JTextField(10);
		pwpf = new JPasswordField(10);
		
		bp = new JPanel();
		enter = new JButton("Enter");
		
		inp.setLayout(new GridLayout(0,2));
		inp.add(idl);
		inp.add(idtf);
		inp.add(pwl);
		inp.add(pwpf);
		pwpf.addActionListener(this);
		add(inp,BorderLayout.CENTER);
		
		enter.addActionListener(this);
		bp.add(enter);
		add(bp,BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String ckpw = String.valueOf(pwpf.getPassword());
		if(id.equals(idtf.getText())) {
			if(pw.equals(ckpw)) {
				JOptionPane.showMessageDialog(null, "�α��� ����");
				dispose();
				new MainFrame();
			}
			else { JOptionPane.showMessageDialog(null, "�������� �ʴ� ID�̰ų� ��й�ȣ�� ��ġ���� �ʽ��ϴ�."); }
		}
		else { JOptionPane.showMessageDialog(null, "�������� �ʴ� ID�̰ų� ��й�ȣ�� ��ġ���� �ʽ��ϴ�."); }
	}
}
