import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.*;

/**
 * @author aa aa
 */

public class Interface extends JFrame implements ActionListener{
	public Interface() {
		initComponents();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	static String sursa=null;
	public String status="Status";

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - aa aa
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuItem2 = new JMenuItem();
		menuItem1 = new JMenuItem();
		menuItem3 = new JMenuItem();
		menuItem4 = new JMenuItem();
		menu2 = new JMenu();
		menuItem5 = new JMenuItem();
		menuItem6 = new JMenuItem();
		label1 = new JLabel();
		panel1 = new JPanel();
		panel2 = new JPanel();
		button1 = new JButton();
		button2 = new JButton();
		button4 = new JButton();
		label = new JLabel();
		label0 = new JLabel();
	    

	      
	    
		//======== this ========
		setTitle("Watermark program");
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== menuBar1 ========
		{

			//======== menu1 ========
			{
				menu1.setText("File");
				
				//---- menuItem1 ----
				menuItem1.setText("Browse");
				menu1.add(menuItem1);
				menuItem1.addActionListener(this);

				//---- menuItem2 ----
				menuItem2.setText("DCT");
				menu1.add(menuItem2);	
				menuItem2.addActionListener(this);

				//---- menuItem3 ----
				menuItem3.setText("DWT");
				menu1.add(menuItem3);
				menu1.addSeparator();
				menuItem3.addActionListener(this);

				//---- menuItem4 ----
				menuItem4.setText("Exit");
				menu1.add(menuItem4);
				menuItem4.addActionListener(this);
			}
			menuBar1.add(menu1);

			//======== menu2 ========
			{
				menu2.setText("Help");

				//---- menuItem5 ----
				menuItem5.setText("About");
				menu2.add(menuItem5);
				menuItem5.addActionListener(this);
				menu2.addSeparator();

				//---- menuItem6 ----
				menuItem6.setText("Manual");
				menuItem6.addActionListener(this);
				menu2.add(menuItem6);
			}
			menuBar1.add(menu2);
		}
		setJMenuBar(menuBar1);

		//---- label1 ----
		label1.setText(status);
		label1.setBackground(Color.white);
		contentPane.add(label1, BorderLayout.SOUTH);

		//======== panel1 ========
		{

			// JFormDesigner evaluation mark
			/*panel1.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), panel1.getBorder())); panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});
*/
			panel1.setLayout(new FlowLayout());

			//---- button1 ----
			button1.setText("Browse");
			button1.addActionListener(this);
			panel1.add(button1);
			

			//---- button2 ----
			button2.setText("DCT");
			button2.addActionListener(this);
			panel1.add(button2);

			//---- button4 ----
			button4.setText("DWT");
			button4.addActionListener(this);
			panel1.add(button4);
			
			
			
			//---- panel2 imaginea dct
			panel2.setBackground(Color.LIGHT_GRAY);
			panel2.add(label0);
			panel2.add(label);
		}
		
		contentPane.add(panel1, BorderLayout.NORTH);
		contentPane.add(panel2, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - aa aa
	private JMenuBar menuBar1;
	private JMenu menu1;
	private JMenuItem menuItem2;
	private JMenuItem menuItem1;
	private JMenuItem menuItem3;
	private JMenuItem menuItem4;
	private JMenu menu2;
	private JMenuItem menuItem5;
	private JMenuItem menuItem6;
	public JLabel label1;
	public JLabel label;
	public JLabel label0;
	private JPanel panel1;
	public JPanel panel2;
	private JButton button1;
	private JButton button2;
	private JButton button4;
	public JProgressBar progress;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	// 
	public void actionPerformed(ActionEvent e) {
		Object eveniment = e.getSource();
		String command = e.getActionCommand();
		//numele comenzii este numele butonului apasat
		//System.out.println(e.toString());
		if (command.equals("Browse")) 
		{				
			FileDialog fd = new FileDialog(this, "Browse", FileDialog.LOAD);
			//stabilim directorul curent
			fd.setDirectory("."); 		

			//specificam filtrul
			fd.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String numeFis) {
					return (numeFis.endsWith(".jpg"));
				}
			});
		fd.show(); //facem vizibila fereastra de dialog
		
		
		if (fd.getFile() != null)
				setSursa(fd.getDirectory()+fd.getFile());

		
		
		if (label != null){
			label.setIcon(null);
		}
		System.out.println(fd.getFile());
		if (fd.getFile() == null && sursa==null)
			label1.setText("Incarcati imaginea ");
		else{
			afis(sursa, 0);
			label1.setText("Imagine incarcata! --> "+fd.getFile());
			}
		}// if Browse
		
		if (command.equals("DCT")) 
		{				
			//System.out.println("Sursa: " + sursa);
			if (sursa==null)
				label1.setText("Incarcati imaginea !");
			else{
				startPrelucrare(sursa, this);
				}
		}// if Start
		
		if (command.equals("DWT")) 
		{				
			//System.out.println("Sursa: " + sursa);
			if (sursa==null)
				label1.setText("Incarcati imaginea !");
			else{
				startPrelucrareDWT(sursa, this);
				}
		}// if Start
		
	}
	
	public void startPrelucrare(String sursa,Interface g)
	{
		Imagi img = new Imagi(g);
		img.loadImage(sursa);
		img.start1(); // face dct si idct si >> test.jpg
	}

	public void startPrelucrareDWT(String sursa,Interface g)
	{
		DwtConv img2 = new DwtConv();
		img2.setInterf(this);
		img2.setSursa_iomagine(sursa);
		img2.MakeDwtWatermark(sursa, sursa+"watermark"+".jpg");
	}
	
	public ImageIcon scale(Image src, double scale) {
	        int w = (int)(scale*src.getWidth(this));
	        int h = (int)(scale*src.getHeight(this));
	        int type = BufferedImage.TYPE_INT_RGB;
	        BufferedImage dst = new BufferedImage(w, h, type);
	        Graphics2D g2 = dst.createGraphics();
	        g2.drawImage(src, 0, 0, w, h, this);
	        g2.dispose();
	        return new ImageIcon(dst);
	    }

	
	// get si set pentru sursa
	public String getSursa() {
		return sursa;
	}
	public void setSursa(String sursa) {
		this.sursa = sursa;
	}
	
	// get si set pentru status
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public void afis(String sursaIomagine, int a) {
		// TODO Auto-generated method stub
		if (a == 1){
			ImageIcon icon = new ImageIcon(sursaIomagine);
			label.setIcon(scale(icon.getImage(), 0.5));
			
		}
		if (a == 0) {
			ImageIcon icon1 = new ImageIcon(sursaIomagine);
			label0.setIcon(scale(icon1.getImage(), 0.5));
		}
		pack();
	}
	
	
	
}
