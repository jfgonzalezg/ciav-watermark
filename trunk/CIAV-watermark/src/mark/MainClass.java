package mark;

public class MainClass {

	static String NumeImag = "tren.jpg";
	
	public static void main(String[] args) {
				
		Imagi img = new Imagi();
		img.loadImage(NumeImag);
		img.start1(); // face dct si idct si >> test.jpg		

	}
}
