import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Imagi  {

	BufferedImage img, img_output;
	int imgCols;
	int imgRows;
	int Rgb[][][];
	int vect[];
	int[] OneDim = new int[imgCols*imgRows];
	int N=8;

	// utilizate pentru a crea poza de output
	Interface interf = new Interface();
	String sursa_iomagine=null;
	Date dateNow = new Date ();

	public void start1(){
		int[][][] Dct = new int[imgCols][imgRows][4];

		double[][][] working3D = copyToDouble(Rgb);
		double[][][] working3DDCT = copyToDouble(Rgb);

		//Facem DCT + watermark
		double[][] redPlane = extractPlane(working3D,1);
		redPlane = extractPlane(working3D,1);		
		forwardXformPlane(redPlane,1);		
		insertPlane(working3D,redPlane,1);

		redPlane = extractPlane(working3D,2);
		forwardXformPlane(redPlane,1);		
		insertPlane(working3D,redPlane,2);

		redPlane = extractPlane(working3D,3);		
		forwardXformPlane(redPlane,1);		
		insertPlane(working3D,redPlane,3);

		//Facem DCT a 2 oara fara watermark pt a scoate watermarkul
		redPlane = extractPlane(working3DDCT,1);		
		forwardXformPlane(redPlane,0);
		insertPlane(working3DDCT,redPlane,1);

		redPlane = extractPlane(working3DDCT,2);
		forwardXformPlane(redPlane,0);
		insertPlane(working3DDCT,redPlane,2);

		redPlane = extractPlane(working3DDCT,3);
		forwardXformPlane(redPlane,0);
		insertPlane(working3DDCT,redPlane,3);

		for (int i=0; i<working3D.length; i++)
		{
			for (int j=0; j<working3D.length; j++)
			{
				for (int k=0; k<4; k++)
				{
					working3DDCT[i][j][k]=working3D[i][j][k]-working3DDCT[i][j][k];					
				}
			}
		}

		// inversam watermarkul pt a vedea ce am adaugat
		redPlane = extractPlane(working3DDCT,1);
		inverseXformPlane(redPlane);
		insertPlane(working3DDCT,redPlane,1);

		redPlane = extractPlane(working3DDCT,2);
		inverseXformPlane(redPlane);
		insertPlane(working3DDCT,redPlane,2);

		redPlane = extractPlane(working3DDCT,3);
		inverseXformPlane(redPlane);
		insertPlane(working3DDCT,redPlane,3);

		Dct = copyToInt(working3DDCT);		

		creazaImagine(Dct);		

		// inversa dct + watermark pt a obtine imaginea finala
		redPlane = extractPlane(working3D,1);
		inverseXformPlane(redPlane);
		insertPlane(working3D,redPlane,1);

		redPlane = extractPlane(working3D,2);
		inverseXformPlane(redPlane);
		insertPlane(working3D,redPlane,2);

		redPlane = extractPlane(working3D,3);
		inverseXformPlane(redPlane);
		insertPlane(working3D,redPlane,3);

		Dct = copyToInt(working3D);		

		creazaImagine(Dct);
		
	}

	private void creazaImagine(int[][][] Dct) {

		// vrem sa adaugam _watermarked		
		int dotPos = sursa_iomagine.lastIndexOf(".");
		String strExtension = sursa_iomagine.substring(dotPos + 1);
		String strFilename = sursa_iomagine.substring(0, dotPos);		

		// vrem sa adaugam si data		  
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		StringBuilder nowYYYYMMDD = new StringBuilder( dateformatYYYYMMDD.format( dateNow ) );

		this.sursa_iomagine = strFilename + "_watermarked_" + nowYYYYMMDD + "." + strExtension;


		OneDim = convertToOneDim(Dct, imgCols, imgRows);	

		int k=0;
		for(int i=0; i < imgRows; i++)
		{
			for(int j=0; j < imgCols; j++)
			{
				img_output.setRGB(i, j, OneDim[k]);
				k++;
			}
		}

		try {
			ImageIO.write(img_output, "JPEG", new java.io.File(sursa_iomagine));			
		} catch (IOException e) {			
			e.printStackTrace();
		}		

		interf.afis(sursa_iomagine,1); // afiseaza imag in interfata
		//this.getContentPane().add(panel);
		interf.label1.setText("Imagine creata! ");

	}

	public Imagi(Interface interf) {
		this.interf = interf;
	}	

	public void Ima(int col, int row)
	{
		Rgb = new int[col][row][4];
		vect = new int[col*row];
		img_output = new BufferedImage(col, row, 1);		
	}

	public void loadImage(String imag) {

		this.sursa_iomagine = imag;	
		try {
			InputStream is = new BufferedInputStream(
					new FileInputStream(imag));

			img = ImageIO.read(is);
		} catch (IOException e) {
			interf.label1.setText("Incarcare esuata !!!!");

		}

		interf.label1.setText("Imagine incarcata! ");
		imgCols = img.getWidth();
		imgRows = img.getHeight();
		Ima(imgCols, imgRows);
		RgbComp();
		Rgb=convertToThreeDim(vect, imgCols, imgRows);
	}

	public void RgbComp() {
		int h=imgRows;
		int w=imgCols;

		int k=0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int pixel = img.getRGB(i, j);

				Rgb[i][j][0] = (pixel >> 24) & 0xff; 	//alpha
				Rgb[i][j][1] = (pixel >> 16) & 0xff;	//red
				Rgb[i][j][2] = (pixel >> 8) & 0xff;		//green
				Rgb[i][j][3] = (pixel) & 0xff;			//blue
				vect[k]= img.getRGB(i, j);
				k++;
			}
		}
		interf.label1.setText("Impartire Rgb ...");
	}

	public double[][] extractPlane(
			double[][][] threeDPixDouble,
			int plane){

		int numImgRows = threeDPixDouble.length;
		int numImgCols = threeDPixDouble[0].length;	    

		double[][] output =new double[numImgRows][numImgCols];

		for(int row = 0;row < numImgRows;row++){
			for(int col = 0;col < numImgCols;col++){
				output[row][col] =
					threeDPixDouble[row][col][plane];
			}
		}
		return output;
	}

	double[][][] copyToDouble(int[][][] threeDPix){
		int imgRows = threeDPix.length;
		int imgCols = threeDPix[0].length;

		double[][][] new3D = new double[imgRows][imgCols][4];
		for(int row = 0;row < imgRows;row++){
			for(int col = 0;col < imgCols;col++){
				new3D[row][col][0] = threeDPix[row][col][0];
				new3D[row][col][1] = threeDPix[row][col][1];
				new3D[row][col][2] = threeDPix[row][col][2];
				new3D[row][col][3] = threeDPix[row][col][3];
			}
		}
		return new3D;
	}

	void forwardXformPlane(double[][] plane,int sel){
		int pixRows = plane.length;
		int pixCols = plane[0].length;

		for(int segRow = 0;segRow < pixRows/8;segRow++){
			for(int segCol = 0;segCol < pixCols/8;segCol++){
				double[][] the8x8Plane = 
					get8x8Block(plane,segRow,segCol);
				forwardXform8x8Block(the8x8Plane);

				if (sel==1){ //facem si watermark daca sel = 1 //sel=0 numai DCT
					double[][] temp = the8x8Plane;					
					the8x8Plane=modifica8x8Plane(temp);
				}
				insert8x8Block(plane,the8x8Plane,segRow,segCol);
			}
		}
	}

	private double[][] modifica8x8Plane(double[][] temp) {

		Random val_random = new Random();
		int valoare = val_random.nextInt(100);
		System.out.println(valoare);	

		double suma = 0.0;

		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++)
				suma+=temp[i][j];

		double media = suma/64;			

		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++)
			{
				if(temp[i][j]>(media-0.2*media) && 
						temp[i][j]<(media+0.2*media))
				{					
					temp[i][j]=temp[i][j] + valoare;
				}
			}
		return temp;
	}

	void forwardXform8x8Block(double[][] the8x8Block){

		int imgRows = 8;
		int imgCols = 8;

		for(int row = 0;row < imgRows;row++){
			double[] theRow = extractRow(the8x8Block,row);

			double[] theXform = new double[theRow.length];
			ForwardDCT.transform(theRow,theXform);

			insertRow(the8x8Block,theXform,row);
		}

		for(int col = 0;col < imgCols;col++){
			double[] theCol = extractCol(the8x8Block,col);

			double[] theXform = new double[theCol.length];
			ForwardDCT.transform(theCol,theXform);

			insertCol(the8x8Block,theXform,col);
		}	
	}

	void insert8x8Block(double[][] colorPlane,
			double[][] the8x8Plane,
			int segRow,
			int segCol){
		for(int bigRow = segRow * 8,smallRow = 0;
		bigRow < (segRow * 8 + 8);
		bigRow++,smallRow++){
			for(int bigCol = segCol * 8,smallCol = 0;
			bigCol < segCol * 8 + 8;
			bigCol++,smallCol++){
				colorPlane[bigRow][bigCol] = 
					the8x8Plane[smallRow][smallCol];
			}
		}
	}

	void insertCol(double[][] colorPlane,
			double[] theCol,
			int col){
		int numRows = colorPlane.length;	
		for(int row = 0;row < numRows;row++){
			colorPlane[row][col] = theCol[row];
		}
	}

	double[] extractCol(double[][] colorPlane,int col){
		int numRows = colorPlane.length;
		double[] output = new double[numRows];
		for(int row = 0;row < numRows;row++){
			output[row] = colorPlane[row][col];
		}
		return output;
	}

	double[] extractRow(double[][] colorPlane,int row){

		int numCols = colorPlane[0].length;
		double[] output = new double[numCols];
		for(int col = 0;col < numCols;col++){
			output[col] = colorPlane[row][col];
		}
		return output;
	}

	void insertRow(double[][] colorPlane,
			double[] theRow,
			int row){
		int numCols = colorPlane[0].length;	
		for(int col = 0;col < numCols;col++){
			colorPlane[row][col] = theRow[col];
		}
	}

	double[][] get8x8Block(double[][] colorPlane,
			int segRow,
			int segCol){
		double[][] the8x8Block = new double[8][8];
		for(int bigRow = segRow * 8,smallRow = 0;
		bigRow < (segRow * 8 + 8);
		bigRow++,smallRow++){
			for(int bigCol = segCol * 8,smallCol = 0;
			bigCol < segCol * 8 + 8;
			bigCol++,smallCol++){
				the8x8Block[smallRow][smallCol] = 
					colorPlane[bigRow][bigCol];
			}
		}
		return the8x8Block;
	}

	public void insertPlane(double[][][] threeDPixDouble,
			double[][] colorPlane,
			int plane){

		int numImgRows = threeDPixDouble.length;
		int numImgCols = threeDPixDouble[0].length;

		for(int row = 0;row < numImgRows;row++){
			for(int col = 0;col < numImgCols;col++){
				threeDPixDouble[row][col][plane] = 
					colorPlane[row][col];
			}
		}
	}

	int[] convertToOneDim(
			int[][][] data,int imgCols,int imgRows){

		int[] oneDPix = new int[imgCols * imgRows * 4];

		for(int row = 0,cnt = 0;row < imgRows;row++){
			for(int col = 0;col < imgCols;col++){
				oneDPix[cnt] = ((data[row][col][0] << 24)
						& 0xFF000000)
						| ((data[row][col][1] << 16)
								& 0x00FF0000)
								| ((data[row][col][2] << 8)
										& 0x0000FF00)
										| ((data[row][col][3])
												& 0x000000FF);
				cnt++;
			}

		}

		return oneDPix;
	}

	public int[][][] copyToInt(double[][][] threeDPixDouble){
		int imgRows = threeDPixDouble.length;
		int imgCols = threeDPixDouble[0].length;

		int[][][] new3D = new int[imgRows][imgCols][4];
		for(int row = 0;row < imgRows;row++){
			for(int col = 0;col < imgCols;col++){
				new3D[row][col][0] = 
					(int)threeDPixDouble[row][col][0];
				new3D[row][col][1] = 
					(int)threeDPixDouble[row][col][1];
				new3D[row][col][2] = 
					(int)threeDPixDouble[row][col][2];
				new3D[row][col][3] = 
					(int)threeDPixDouble[row][col][3];
			}
		}
		return new3D;
	}

	int[][][] convertToThreeDim(
			int[] oneDPix,int imgCols,int imgRows){

		int[][][] data =
			new int[imgRows][imgCols][4];

		for(int row = 0;row < imgRows;row++){

			int[] aRow = new int[imgCols];
			for(int col = 0; col < imgCols;col++){
				int element = row * imgCols + col;
				aRow[col] = oneDPix[element];
			}

			for(int col = 0;col < imgCols;col++){
				//Alpha data
				data[row][col][0] = (aRow[col] >> 24)
				& 0xFF;
				//Red data
				data[row][col][1] = (aRow[col] >> 16)
				& 0xFF;
				//Green data
				data[row][col][2] = (aRow[col] >> 8)
				& 0xFF;
				//Blue data
				data[row][col][3] = (aRow[col])
				& 0xFF;
			}
		}
		return data;
	}

	void clipToZero(double[][] colorPlane){
		int numImgRows = colorPlane.length;
		int numImgCols = colorPlane[0].length;

		for(int row = 0;row < numImgRows;row++){
			for(int col = 0;col < numImgCols;col++){
				if(colorPlane[row][col] < 0){
					colorPlane[row][col] = 0;
				}
			}
		}
	}

	void clipTo255(double[][] colorPlane){
		int numImgRows = colorPlane.length;
		int numImgCols = colorPlane[0].length;

		for(int row = 0;row < numImgRows;row++){
			for(int col = 0;col < numImgCols;col++){
				if(colorPlane[row][col] > 255){
					colorPlane[row][col] = 255;
				}
			}
		}
	}

	void inverseXformPlane(double[][] plane){
		int pixRows = plane.length;
		int pixCols = plane[0].length;

		for(int segRow = 0;segRow < pixRows/8;segRow++){

			for(int segCol = 0;segCol < pixCols/8;segCol++){
				double[][] the8x8Block = 
					get8x8Block(plane,segRow,segCol);
				inverseXform8x8Plane(the8x8Block);
				insert8x8Block(plane,the8x8Block,segRow,segCol);
			}
		}
	}

	void inverseXform8x8Plane(double[][] the8x8Block){

		int imgRows = 8;
		int imgCols = 8;

		for(int col = 0;col < imgCols;col++){
			double[] theXform = extractCol(the8x8Block,col);

			double[] theCol = new double[theXform.length];
			InverseDCT.transform(theXform,theCol);

			insertCol(the8x8Block,theCol,col);
		}

		for(int row = 0;row < imgRows;row++){
			double[] theXform = extractRow(the8x8Block,row);

			double[] theRow = new double[theXform.length];			
			InverseDCT.transform(theXform,theRow);

			insertRow(the8x8Block,theRow,row);
		}

		clipToZero(the8x8Block);
		clipTo255(the8x8Block);
	}



	/*public double[] zigZag(double[][] m) {
		double[] zz = new double[N*N];
		for (int i=0;i<N;i++) {
			for (int j=0;j<N;j++) 
				zz[zigzag[i][j]]=m[i][j];
		}      	
		return zz;
	} */ 

	/*creeaza coeficientii mat. zig-zag in zigzag[][] 
    pe care ii foloseste in zigZag() pentru a ordona coef 
    mat 8*8 in zig zag */
	/*private static double[][] makeZigZagMatrix(double[] imput) {

		// initializam N ca radical din lungimea vectorului de intrare
		// se poate initializa direct cu 8 pentru matrici de 8x8
		int N = (int)Math.sqrt(imput.length);

		double[][] zz = new double[N][N];
		int index1=0;
		int index2=N*(N-1)/2;       
		int i,j;
		for (int k=0;k<N;k++) {
			if (k%2==0) {
				i=0;
				j=k;
				while (j>-1) {
					zz[i][j]=imput[index1];
					index1++;
					i++;
					j--;
				}
				i=N-1;
				j=k;
				while (j<N) {
					zz[i][j]=imput[index2];
					index2++;
					i--;
					j++;
				}
			}
			else {
				i=k;
				j=0;
				while (i>-1) {
					zz[i][j]=imput[index1];
					index1++;
					j++;
					i--;
				}
				i=k;
				j=N-1;
				while (i<N) {
					zz[i][j]=imput[index2];
					index2++;
					i++;
					j--;
				}
			}
		}
		return zz;
	}*/

	/*public static double[][] ZigZag(double[][] imput){		

		double[][]output = new double[8][8]; 
		double[]temp = new double[64];

		// copiem matricea de imput intr-un vector
		temp = convert2dTo1d(imput);

		// ordonam coeficientii matricei 8x8 in ordine crescatoare
		//Arrays.sort(temp);
		//afiseazaArray1D(temp);

		// creem matricea de output, cu coeficientii in zig-zag din vectorul temp care este sortat
		output = makeZigZagMatrix(temp);
		//afiseazaArray2D(output);

		return output;		
	}*/

	/*public static double[] convert2dTo1d(double[][] imput){

		int k=0;
		double[] temp = new double[imput.length * imput.length] ;
		for (int i=0; i< 8; i++)
		{
			for (int j=0; j< 8; j++)
			{
				temp[k] = imput[i][j];	
				k++;
			}
		}
		return temp;		
	}
	 */






}//sfarsitul clasei



