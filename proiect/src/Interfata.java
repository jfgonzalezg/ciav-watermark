import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Interfata extends Frame implements ActionListener{

	public Interfata(String titlu) {
		super(titlu);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	public void initializare() {
		setLayout(new FlowLayout());
		setSize(300, 100);

		Button b = new Button("Browse");
		add(b);
		b.addActionListener(this);
	}	

	//metoda interfetei ActionListener
	public void actionPerformed(ActionEvent e) {
		FileDialog fd = new FileDialog(this, "Browse", FileDialog.LOAD);
		//stabilim directorul curent
		fd.setDirectory("."); 

		//numele implicit
		//fd.setFile("test.jpg");

		//specificam filtrul
		fd.setFilenameFilter(new FilenameFilter() {
			public boolean accept(File dir, String numeFis) {
				return (numeFis.endsWith(".jpg"));
			}
		});
		fd.show(); //facem vizibila fereastra de dialog
		
		System.out.println("Fisierul ales este: " + fd.getFile());
	}
	
}
