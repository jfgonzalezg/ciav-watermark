/**
 * Utility to convert a colored image into gray color.
 */
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class DwtConv {

	int hi;
	int wi;
	
	// creem outputul
	public Interface interf = new Interface();
	public String sursa_iomagine=null;
	Date dateNow = new Date ();

	//filtru Haar
	double[] Hi_D= new double[]{-0.7071 , 0.7071};
	double[] Hi_R= new double[]{0.7071 , -0.7071};
	double[] Lo_R= new double[]{0.7071 , 0.7071};
	double[] Lo_D= new double[]{0.7071 , 0.7071};

	// matrici de 32*32 rezultate din DWT()
	double[][] a;
	double[][] h;
	double[][] v;
	double[][] d;
	double[][] z;
	double[][] y;
		
/*	public static void main(String[] args) {
		
		MakeDwtWatermark("C:/baboon.jpg","C:/baboon_watermark.jpg");
	}*/
	
	// set si get pentru atributele care ne intereseaza
	public void setInterf(Interface interf) {
		this.interf = interf;
	}
	public void setSursa_iomagine(String sursaIomagine) {
		sursa_iomagine = sursaIomagine;
	}

	public void MakeDwtWatermark(String inputImageFilePath,
			String outputDwt) {
		DwtConv imageGrayer = new DwtConv();
		
		//citeste imaginea
		BufferedImage inputImage = imageGrayer.readImage(inputImageFilePath);
		//Convertim imaginea in grayscale

		int[][] IKey = imageGrayer.creazaKey(imageGrayer,true);
				
		int[][] IRgb=imageGrayer.grayScale(inputImage);

		double[][] Imax= new double[imageGrayer.hi][imageGrayer.wi];
		double[][] Water= new double[imageGrayer.hi][imageGrayer.wi];

		for (int i = 0; i < Imax.length; i++) {
			for (int j = 0; j < Imax[0].length; j++) {
				Imax[i][j] = (double)IRgb[i][j];
			}
		}

		Imax = imageGrayer.dwt(Imax,imageGrayer.Lo_D,imageGrayer.Hi_D);

		double c=0.01;
		
		for (int i = 0; i < Water.length; i++) {
			for (int j = 0; j < Water.length; j++) {
			//Water[i][j] = Imax[i][j] + c*Math.abs(Imax[i][j])*IKey[i][j];
			Water[i][j] = Imax[i][j] +IKey[i][j];
				
			}
		}
		
		int p = Imax.length/2;
		int q = Imax[0].length/2;

		double[][] nca=new double[p][q];
		double[][] ncv=new double[p][q];
		double[][] nch=new double[p][q];
		double[][] ncd=new double[p][q];

		for (int i = 0; i < p; i++) {
			for (int j = 0; j < q; j++) {
				nca[i][j] = Water[i][j];
				ncv[i][j] = Water[i+p][j];
				nch[i][j] = Water[i][j+q];
				ncd[i][j] = Water[i+p][j+q];
			}
		}

		Imax=imageGrayer.idwt(nca,ncv,nch,ncd,imageGrayer.Lo_R,imageGrayer.Hi_R);

		BufferedImage output= new BufferedImage(imageGrayer.wi, imageGrayer.hi, 4);


		for (int i = 0; i < Imax.length; i++) {
			for (int j = 0; j < Imax[0].length; j++) {

				int colorInt = (255 & 0xff) << 24 |
				((int)Imax[i][j] & 0xff) << 16 |
				((int)Imax[i][j]  & 0xff) << 8 |
				((int)Imax[i][j] & 0xff);

				output.setRGB(j, i, colorInt);
			}
		}
		
		imageGrayer.writeImage(output, outputDwt, "jpg");
	//	ImageIcon imag = new ImageIcon(outputDwt);
		interf.afis(outputDwt,2);
	}

	public int[][] creazaKey(DwtConv imageGrayer, boolean b) {
		BufferedImage key = new BufferedImage(imageGrayer.hi,imageGrayer.wi,10);

		for (int i = 0; i < imageGrayer.hi; i++) {
			for (int j = 0; j < imageGrayer.wi; j++) {
				int colorInt = (255 & 0xff) << 24 |
				(imageGrayer.albNegru() & 0xff) << 16 |
				(imageGrayer.albNegru()  & 0xff) << 8 |
				(imageGrayer.albNegru() & 0xff);
				key.setRGB(j, i, colorInt);
			}

		}
		int[][] IKey=imageGrayer.grayScale(key);
		if (b==true)
			imageGrayer.writeImage(key, "C:/key.jpg", "jpg");
		
		return IKey;
	}

	public double[][] idwt(double[][] nca, double[][] ncv, double[][] nch,
			double[][] ncd, double[] loR, double[] hiR) {

		double[][] IdwtIma= new double[512][512];

		IdwtIma = matrImag(upsconv2(a,mat(Lo_R,Lo_R)),upsconv2(h,mat(Hi_R,Lo_R)),upsconv2(v,mat(Lo_R,Hi_R)),upsconv2(d,mat(Hi_R,Hi_R)));
		return IdwtIma; 
	}

	private double[][] matrImag(double[][] upsconv2, double[][] upsconv22,
			double[][] upsconv23, double[][] upsconv24) {
		double[][] mat1 = new double[upsconv2.length][upsconv2[0].length];
		System.out.println("ceva "+upsconv2.length+" "+upsconv2[0].length);
		System.out.println("ceva "+upsconv22.length+" "+upsconv22[0].length);
		for (int i = 0; i < upsconv24.length; i++) {
			for (int j = 0; j < upsconv24.length; j++) {
				mat1[i][j]= upsconv2[i][j]+upsconv22[i][j]+upsconv23[i][j]+upsconv24[i][j];	
			}
		}
		return mat1;
	}

	public double[][] upsconv2(double[][] a2, double[][] mat) {
		// TODO Auto-generated method stub
		double[][] y = new double[a2.length*2][a2[0].length];

		y = extindePeLinii(a2); //bun

		double[][] ff = new double[a2.length*2+2][a2[0].length-1];
		ff = addCol(convolutie(transpusa(y),vetor(mat,0)));


		double[][] mm = new double[a2[0].length-1][a2.length*2];
		mm = transpusa(ff);

		double[][] pp = new double[a2[0].length-1][a2.length*2*2];
		pp= extindePeCol(mm);

		y = addCol(convolutie(pp, vetor(mat,1)));
		return y;
	}
	
	//j = 0 sau 1
	public double[] vetor(double[][] mat, int j) {
		// TODO Auto-generated method stub
		double[] vect = new double[2];

		for (int i = 0; i < mat.length; i++) {
			vect[i]=mat[i][j];
		}
		return vect;
	}

	public static double[][] mat(double[] v1, double[] v2) 
	{
		double[][] matrice = new double[2][2];

		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < matrice.length; j++) {

				matrice[0][j] = v1[j];
				matrice[1][j] = v2[j];

			}
		}

		return matrice;
	}

	public double[][] dwt(double[][] imax2, double[] loD, double[] hiD) {

		a=new double[imax2.length/2][imax2[0].length/2];
		h=new double[imax2.length/2][imax2[0].length/2];
		v=new double[imax2.length/2][imax2[0].length/2];
		d=new double[imax2.length/2][imax2[0].length/2];

		y=new double[imax2.length][imax2[0].length+2];
		//System.out.println((imax2.length)+" "+(imax2[0].length+2));
		y = addCol(imax2); 				//face bine

		z=new double[64][65];
		//System.out.println((z.length)+" "+(z[0].length));
		z = convolutie(y,Lo_D);			//face bine

		a = convdown(z,Lo_D);
		h = convdown(z,Hi_D);
		z = convolutie(y,Hi_D);
		v = convdown(z, Lo_D);
		d = convdown(z, Hi_D);

		int nrCol = a[0].length+h[0].length;
		int nrRan = a.length+v.length;

		double[][] arr=new double[nrRan][nrCol];

		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {				
				arr[i][j] = a[i][j];
			}
		}
		for (int i = 0; i < h.length; i++) {
			for (int j = a[0].length; j < h[0].length+a[0].length; j++) {				
				arr[i][j] = h[i][j-a[0].length];
			}
		}
		for (int i = a.length; i < v.length+a.length-1; i++) {
			for (int j = 0; j < v[0].length; j++) {				
				arr[i][j] = v[i-a.length][j];
			}
		}
		for (int i = a.length; i < h.length+a.length; i++) {
			for (int j = a[0].length; j < h[0].length+a[0].length; j++) {				
				arr[i][j] = d[i-a.length][j-a[0].length];
			}
		}	

		return arr;
	}

	private double[][] convdown(double[][] convolutie,double[] F) {

		double[][] y=new double[convolutie.length][convolutie[0].length/2];
		y=decimeazaPeColoane(convolutie); //bun


		double[][] zz=new double[convolutie.length+2][convolutie[0].length/2];
		zz=addRow(y);					//bun

		double[][] xx=new double[convolutie.length+2][(convolutie[0].length/2)-1];
		xx=convolutie(transpusa(zz),F);



		//System.out.println(xx.length+" "+xx[0].length);
		double[][] gg=new double[(convolutie[0].length/2)-1][convolutie.length+2];
		gg=transpusa(xx);


		double[][] dd=new double[(convolutie[0].length/2)-1][convolutie.length/2+2];
		dd=decimeazaPeLinii(gg);

		return dd;
	}

	private void key(DwtConv imageGrayer, BufferedImage key) {
		for (int i = 0; i < 512; i++) {
			for (int j = 0; j < 512; j++) {
				key.setRGB(i, j, albNegru());
			}
		}

		imageGrayer.writeImage(key, "C:/key.jpg", "jpg");
	}

	public int albNegru()
	{
		int nrtt=0;
		Random ra=new Random();
		nrtt=(int)(ra.nextFloat() * 255);

		return nrtt;		
	}

	public double[][] convolutie(double[][] im, double[] m) {
		int nxr, nxc, nmr, nmc, nyr, nyc;

		nxr = im.length;     // nxr: number of im rows, etc.
		nxc = im[0].length;
		nmr = 1;     // nxr: number of im rows, etc.
		nmc = m.length;

		nyr = nxr - nmr+1;     // get valid output region
		nyc = nxc - nmc+1;

		//System.out.println("ndasdsad "+nyr+" "+nyc);
		double[][] y1 = new double[nyr][nyc];

		for (int yr = 0;yr<nyr;yr++) 
		{
			for (int yc = 0; yc<nyc;yc++)
			{
				double sum = 0.0;
				for (int mr = 0;mr<nmr;mr++)    
				{
					for (int mc = 0;mc<nmc;mc++)
					{
						sum = sum + im[yr-mr+nmr-1][yc-mc+nmc-1] * m[mc];
					}
				}
				y1[yr][yc] = sum;  
			}	
		}

		return y1;
	}

	public double[][] decimeazaPeColoane(double[][] matrice){
		double[][] return1=new double[matrice.length][matrice[0].length/2];
		int cnt=0;

		//parcurgem pe coloane
		for (int i=0;i<matrice[0].length; i++)
		{
			if (i%2!=0)
			{
				for(int j=0;j<matrice.length; j++)
				{
					return1[j][cnt]=matrice[j][i];
				}
				cnt++;
			}			
		}
		return return1;
	}

	public double[][] decimeazaPeLinii(double[][] matrice){
		double[][] return1=new double[matrice.length/2][matrice[0].length];
		int cnt=0;

		//parcurgem pe linii
		for (int i=0;i<matrice.length; i++)
		{
			if (i%2!=0)
			{
				for(int j=0;j<matrice[0].length; j++)
				{
					return1[cnt][j]=matrice[i][j];
				}
				cnt++;
			}			
		}
		return return1;
	}

	public double[][] extindePeLinii(double[][] matrice){
		double[][] return1=new double[matrice.length*2-1][matrice[0].length];
		int cnt=0;

		//parcurgem pe linii
		for (int i=0;i<matrice.length; i++)
		{		
			if (i%2!=0&&i<matrice.length-1)
			{
				for(int j=0;j<matrice[0].length; j++)
				{
					return1[cnt][j]=0;
				}

			}
			for(int j=0;j<matrice[0].length; j++)
			{
				return1[cnt][j]=matrice[i][j];
			}

			cnt=cnt+2;
		}
		return return1;
	}

	public double[][] extindePeCol(double[][] matrice){
		double[][] return1=new double[matrice.length][matrice[0].length*2-1];
		int cnt=0;

		//parcurgem pe linii
		for (int j=0;j<matrice[0].length; j++)
		{		
			if (j%2!=0&&j<matrice[0].length-1)
			{
				for(int i=0;i<matrice.length; i++)
				{
					return1[i][cnt]=0;
				}

			}
			for(int i=0;i<matrice.length; i++)
			{
				return1[i][cnt]=matrice[i][j];
			}

			cnt=cnt+2;
		}
		return return1;
	}

	public double[][] addRow(double[][] mat) {

		double[][] mmat=new double[mat.length+2][mat[0].length];
		//System.out.println(mat.length);

		for (int i = 0; i < mmat.length; i++) {
			for (int j = 0; j < mmat[0].length; j++) {
				if (i==0)
					mmat[i][j]=mat[i][j];
				else if (i>0 && i<mmat.length-1)
					mmat[i][j]=mat[i-1][j];
				else if(i==mmat.length-1)
					mmat[i][j]=mat[i-2][j];
			}
		}		
		return mmat;
	}

	public double[][] addCol(double[][] mat) {

		double[][] mmat=new double[mat.length][mat[0].length+2];
		//				
		//		wTemp=mat[0].length;
		//		hTemp=mat.length;

		for (int i = 0; i < mmat.length; i++) {
			for (int j = 0; j < mmat[0].length; j++) {
				if (j==0)
					mmat[i][j]=mat[i][j];
				else if (j>0 && j<mmat[0].length-1)
					mmat[i][j]=mat[i][j-1];
				else if(j==mmat[0].length-1)
					mmat[i][j]=mat[i][j-2];
			}
		}		
		return mmat;
	}

	public double[][] transpusa(double[][] matrice){
		double[][] return1=new double[matrice[0].length][matrice.length];

		//parcurgem pe coloane
		for (int i=0;i<return1.length; i++)
		{

			for(int j=0;j<return1[0].length; j++)
			{
				return1[i][j]=matrice[j][i];
			}				
		}		

		return return1;
	}	
	
	public int[][] grayScale(BufferedImage imac) {
		
		int[] Rgb = new int[wi*hi];
		int k=0;
		for (int i = 0; i < wi; i++) {
			for (int j = 0; j < hi; j++) {
				Rgb[k] = imac.getRGB(j, i);
				k++;
				}
			}
    	
		for (int m = 0; m < wi*hi; m++) {
			int  red = (Rgb[m] & 0x00ff0000) >> 16;
			int  green = (Rgb[m] & 0x0000ff00) >> 8;
			int  blue = Rgb[m] & 0x000000ff;
			Rgb[m]=(int) (Math.round(lum(new Color(red,green,blue))));
		}
		
		
		int[][] ImRgb=new int[wi][hi];
		
		int h1=0;
		for (int i = 0; i < wi; i++) {
			for (int j = 0; j < hi; j++) {
				ImRgb[i][j]=Rgb[h1];
				h1++;
				//System.out.print(ImRgb[i][j]+" ");
			}
			//System.out.println();
		}
		return ImRgb;
	}
	//folosit de grayScale
    public static double lum(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return .299*r + .587*g + .114*b;
    }
	
	
	
	public BufferedImage readImage(String fileLocation) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fileLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
		hi=img.getHeight();
		wi=img.getWidth();
		return img;
	}

	public void writeImage(BufferedImage img, String fileLocation,
			String extension) {
		try {
			BufferedImage bi = img;
			File outputfile = new File(fileLocation);
			ImageIO.write(bi, extension, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
