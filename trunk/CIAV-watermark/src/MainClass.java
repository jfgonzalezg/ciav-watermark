import java.awt.font.NumericShaper;


public class MainClass {

	static String NumeImag = "lena.jpg";
	int i=0;
	public static void main(String[] args) {
		
		
		Imagi img = new Imagi();
		img.loadImage(NumeImag);
	//	img.RgbComp();
		img.start1();
		
		//int all[][][] = new int[img.imgCols][img.imgRows][4];
		
		

	}
}
