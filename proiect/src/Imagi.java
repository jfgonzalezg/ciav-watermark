

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;


public class Imagi  {

	BufferedImage img, img_output;
	int imgCols;
	int aaaaa;
	int imgRows;
	int Rgb[][][];
	int vect[];
	int[] OneDim = new int[imgCols*imgRows];
	int N=8;
	int zigzag[][];
	
	public void start1(){
		int[][][] Dct = new int[imgCols][imgRows][4];

		double[][][] working3D = copyToDouble(Rgb);
		double[][] redPlane = extractPlane(working3D,1);
		redPlane = extractPlane(working3D,1);
		//Do DCT 
		forwardXformPlane(redPlane);
		//Insert the color plane back into the 3D array. 
		insertPlane(working3D,redPlane,1);

		redPlane = extractPlane(working3D,2);
		//Do DCT 
		forwardXformPlane(redPlane);
		//Insert the color plane back into the 3D array. 
		insertPlane(working3D,redPlane,2);

		redPlane = extractPlane(working3D,3);
		//Do DCT 
		forwardXformPlane(redPlane);
		//Insert the color plane back into the 3D array. 
		insertPlane(working3D,redPlane,3);

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
		
		OneDim = convertToOneDim(Dct, imgCols, imgRows);
		//	clipTo255();
		
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
			ImageIO.write(img_output, "JPEG", new java.io.File("test.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start2(){
		int[][][] Dct = new int[imgCols][imgRows][4];

		double[][][] working3D = copyToDouble(Rgb);
		double[][] redPlane = extractPlane(working3D,1);
		redPlane = extractPlane(working3D,1);
		//Do DCT 
		forwardXformPlane(redPlane);
		//Insert the color plane back into the 3D array. 
		insertPlane(working3D,redPlane,1);

		redPlane = extractPlane(working3D,2);
		//Do DCT 
		forwardXformPlane(redPlane);
		//Insert the color plane back into the 3D array. 
		insertPlane(working3D,redPlane,2);

		redPlane = extractPlane(working3D,3);
		//Do DCT 
		forwardXformPlane(redPlane);
		//Insert the color plane back into the 3D array. 
		insertPlane(working3D,redPlane,3);
		
		Dct = copyToInt(working3D);
		creazaImagine(Dct);
	}

	public void Ima(int col, int row)
	{
		Rgb = new int[col][row][4];
		vect = new int[col*row];
		img_output = new BufferedImage(col, row, 1);
		//zigzag = makeZigZagMatrix();
	}

	public void loadImage(String imag) {

		try {
			img = ImageIO.read(this.getClass().getResource(imag));
		} catch (IOException e) {
		}

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
				System.out.println(Rgb[i][j][0]+" "+Rgb[i][j][1]+" "+Rgb[i][j][2]+" "+Rgb[i][j][3]+ " k="+k);
				k++;
			}
		}

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
			}//end inner loop
		}//end outer loop
		return new3D;
	}//end copyToDouble

	void forwardXformPlane(double[][] plane){
		int pixRows = plane.length;
		int pixCols = plane[0].length;
		//Loop on rows of 8x8 blocks
		for(int segRow = 0;segRow < pixRows/8;segRow++){
			//Loop on cols of 8x8 blocks
			for(int segCol = 0;segCol < pixCols/8;segCol++){
				double[][] the8x8Plane = 
					get8x8Block(plane,segRow,segCol);
				forwardXform8x8Block(the8x8Plane);
			
			//	double[][] temp = the8x8Plane;
				// cream un oob ZigZag
			//	the8x8Plane=ZigZag(temp);
				
				insert8x8Block(plane,the8x8Plane,segRow,segCol);
			}//end inner loop
		}//end outer loop
	}//end forwardXformPlane

	void forwardXform8x8Block(double[][] the8x8Block){

		int imgRows = 8;
		int imgCols = 8;

		//Extract each row from the 8x8-pixel block and perform
		// a forward DCT on the row. Then insert the
		// transformed row back into the block.  At that point,
		// the row no longer contains color pixel data, but
		// has been transformed into a row of spectral data.
		for(int row = 0;row < imgRows;row++){
			double[] theRow = extractRow(the8x8Block,row);

			double[] theXform = new double[theRow.length];
			//Perform the forward transform.
			ForwardDCT.transform(theRow,theXform);

			//Insert the transformed row back into the block.
			insertRow(the8x8Block,theXform,row);
		}//end for loop

		//The block now contains the results of doing the
		// horizontal DCT one row at a time.  The block no
		// longer contains color pixel data.  Rather, it
		// contains spectral data for the horizontal dimension
		// only.

		//Extract each column from the block and perform a
		// forward DCT on the column. Then insert the
		// transformed column back into the block.
		for(int col = 0;col < imgCols;col++){
			double[] theCol = extractCol(the8x8Block,col);

			double[] theXform = new double[theCol.length];
			ForwardDCT.transform(theCol,theXform);

			insertCol(the8x8Block,theXform,col);
		}//end for loop

		//The 8x8 block has now been converted into an 8x8
		// block of spectral coefficient data.

	}//end forwardXform8x8Block

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
			}//end inner loop
		}//end outer loop
	}//end insert8x8Block

	void insertCol(double[][] colorPlane,
			double[] theCol,
			int col){
		int numRows = colorPlane.length;
		double[] output = new double[numRows];
		for(int row = 0;row < numRows;row++){
			colorPlane[row][col] = theCol[row];
		}//end outer loop
	}//end insertCol

	double[] extractCol(double[][] colorPlane,int col){
		int numRows = colorPlane.length;
		double[] output = new double[numRows];
		for(int row = 0;row < numRows;row++){
			output[row] = colorPlane[row][col];
		}//end outer loop
		return output;
	}//end extractCol

	double[] extractRow(double[][] colorPlane,int row){

		int numCols = colorPlane[0].length;
		double[] output = new double[numCols];
		for(int col = 0;col < numCols;col++){
			output[col] = colorPlane[row][col];
		}//end outer loop
		return output;
	}//end extractRow

	//The purpose of this method is to insert a specified
	// row of double data into a double 2D plane.
	void insertRow(double[][] colorPlane,
			double[] theRow,
			int row){
		int numCols = colorPlane[0].length;
		double[] output = new double[numCols];
		for(int col = 0;col < numCols;col++){
			colorPlane[row][col] = theRow[col];
		}//end outer loop
	}//end insertRow

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
			}//end inner loop
		}//end outer loop
		return the8x8Block;
	}//end get8x8Block

	//The purpose of this method is to insert a double 2D
	// plane into the double 3D array that represents an
	// image.  This method also trims off any extra rows and
	// columns in the double 2D plane.
	public void insertPlane(double[][][] threeDPixDouble,
			double[][] colorPlane,
			int plane){

		int numImgRows = threeDPixDouble.length;
		int numImgCols = threeDPixDouble[0].length;

		//Copy the values from the incoming color plane to the
		// specified plane in the 3D array.
		for(int row = 0;row < numImgRows;row++){
			for(int col = 0;col < numImgCols;col++){
				threeDPixDouble[row][col][plane] = 
					colorPlane[row][col];
			}//end loop on col
		}//end loop on row
	}//end insertPlane	

	int[] convertToOneDim(
			int[][][] data,int imgCols,int imgRows){
		//Create the 1D array of type int to be
		// populated with pixel data, one int value
		// per pixel, with four color and alpha bytes
		// per int value.
		int[] oneDPix = new int[
		                        imgCols * imgRows * 4];

		//Move the data into the 1D array.  Note the
		// use of the bitwise OR operator and the
		// bitwise left-shift operators to put the
		// four 8-bit bytes into each int.
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
			}//end for loop on col

		}//end for loop on row

		return oneDPix;
	}//end convertToOneDim

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
			}//end inner loop
		}//end outer loop
		return new3D;
	}//end copyToInt

	int[][][] convertToThreeDim(
			int[] oneDPix,int imgCols,int imgRows){
		//Create the new 3D array to be populated
		// with color data.
		int[][][] data =
			new int[imgRows][imgCols][4];

		for(int row = 0;row < imgRows;row++){
			//Extract a row of pixel data into a
			// temporary array of ints
			int[] aRow = new int[imgCols];
			for(int col = 0; col < imgCols;col++){
				int element = row * imgCols + col;
				aRow[col] = oneDPix[element];
			}//end for loop on col

			//Move the data into the 3D array.  Note
			// the use of bitwise AND and bitwise right
			// shift operations to mask all but the
			// correct set of eight bits.
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
			}//end for loop on col
		}//end for loop on row
		return data;
	}//end convertToThreeDim

	void clipToZero(double[][] colorPlane){
		int numImgRows = colorPlane.length;
		int numImgCols = colorPlane[0].length;
		//Do the clip
		for(int row = 0;row < numImgRows;row++){
			for(int col = 0;col < numImgCols;col++){
				if(colorPlane[row][col] < 0){
					colorPlane[row][col] = 0;
				}//end if
			}//end inner loop
		}//end outer loop
	}//end clipToZero
	
	//The purpose of this method is to clip all color values
	// in a double color plane that are greater than 255 to
	// a value of 255.
	void clipTo255(double[][] colorPlane){
		int numImgRows = colorPlane.length;
		int numImgCols = colorPlane[0].length;
		//Do the clip
		for(int row = 0;row < numImgRows;row++){
			for(int col = 0;col < numImgCols;col++){
				if(colorPlane[row][col] > 255){
					colorPlane[row][col] = 255;
				}//end if
			}//end inner loop
		}//end outer loop
	}//end clipTo255

	void inverseXformPlane(double[][] plane){
		int pixRows = plane.length;
		int pixCols = plane[0].length;
		//Loop on rows of 8x8 blocks
		for(int segRow = 0;segRow < pixRows/8;segRow++){
			//Loop on cols of 8x8 blocks
			for(int segCol = 0;segCol < pixCols/8;segCol++){
				double[][] the8x8Block = 
					get8x8Block(plane,segRow,segCol);
				inverseXform8x8Plane(the8x8Block);
				insert8x8Block(plane,the8x8Block,segRow,segCol);
			}//end inner loop
		}//end outer loop
	}//end inverseXformPlane

	void inverseXform8x8Plane(double[][] the8x8Block){

		int imgRows = 8;
		int imgCols = 8;

		//Extract each col from the spectral data block and
		// perform an inverse DCT on the column. Then insert it
		// back into the block, which is being transformed into
		// a block of color pixel data.
		for(int col = 0;col < imgCols;col++){
			double[] theXform = extractCol(the8x8Block,col);

			double[] theCol = new double[theXform.length];
			//Now perform the inverse transform.
			InverseDCT.transform(theXform,theCol);

			//Insert it back into the block.
			insertCol(the8x8Block,theCol,col);
		}//end for loop

		//At this point, an inverse DCT has been performed on
		// each column in the spectral data block, one column
		// at a time.

		//Extract each row from the block and perform an
		// inverse DCT on the row. Then insert it back into the
		// block.  The row now contains color pixel data.
		for(int row = 0;row < imgRows;row++){
			double[] theXform = extractRow(the8x8Block,row);

			double[] theRow = new double[theXform.length];
			//Now perform the inverse transform.
			InverseDCT.transform(theXform,theRow);

			//Insert it back into the block.
			insertRow(the8x8Block,theRow,row);
		}//end for loop

		//For illustration purposes only, activate the
		// following statement to cause the 8x8 block
		// structure of the entire process to become visible
		// in the displayed image.  This causes each individual
		// block of pixel data to be a slightly different 
		// color.
		//addRandom(the8x8Block);

		//At this point, the spectral data block has been
		// converted into an 8x8 block of pixel color data. 
		// Ultimately it will be necessary to convert it to
		// 8-bit unsigned pixel color format in order to
		// display it.  Clip to zero and 255.
		clipToZero(the8x8Block);
		clipTo255(the8x8Block);
	}//end inverseXform8x8Plane
	/* initialize coefficient matrix */

	/* write dct coefficient matrix into 1D array in zig zag order */
    public double[] zigZag(double[][] m) {
    	double[] zz = new double[N*N];
    	for (int i=0;i<N;i++) {
    		for (int j=0;j<N;j++) 
    			zz[zigzag[i][j]]=m[i][j];
    	}
      	
    	return zz;
    }  
    
    /*creeaza coeficientii mat. zig-zag in zigzag[][] 
    pe care ii foloseste in zigZag() pentru a ordona coef 
    mat 8*8 in zig zag */
    private static double[][] makeZigZagMatrix(double[] imput) {
		
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
    }

    public static double[][] ZigZag(double[][] imput){		
		
    	double[][]output = new double[8][8]; 
		double[]temp = new double[64];
		
		// copiem matricea de imput intr-un vector
		temp = convert2dTo1d(imput);
		
		// ordonam coeficientii matricei 8x8 in ordine crescatoare
		Arrays.sort(temp);
		//afiseazaArray1D(temp);
		
		// creem matricea de output, cu coeficientii in zig-zag din vectorul temp care este sortat
		output = makeZigZagMatrix(temp);
		//afiseazaArray2D(output);
		
		return output;		
	}

    public static double[] convert2dTo1d(double[][] imput){
		
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


}//sfarsitul clasei

