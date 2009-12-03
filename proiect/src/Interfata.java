import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Interfata extends Frame implements ActionListener{
	
	static String sursa;
	
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
		
		Button b2 = new Button("Start");
		add(b2);
		b2.addActionListener(this);
	}	

	//metoda interfetei ActionListener
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
		
	//	System.out.println("Fisierul ales este: " + fd.getFile()+ " " +fd.getDirectory());
		setSursa(fd.getDirectory()+fd.getFile());
	//	System.out.println("Sursa este: " + sursa);	
		}// if Browse
		
		if (command.equals("Start")) 
		{				
			System.out.println("Sursa: " + sursa);
			startPrelucrare(sursa);
		}// if Start
		
	}
	
	public void startPrelucrare(String sursa)
	{
	Imagi img = new Imagi();
	img.loadImage(sursa);
	img.start1(); // face dct si idct si >> test.jpg
	}
	

	
	// get si set pentru sursa
	public String getSursa() {
		return sursa;
	}
	public void setSursa(String sursa) {
		this.sursa = sursa;
	}
	
}
