import java.util.Arrays;



public class ZigZag {
	

	public static void main(String[] args) {
		int[][] matrice = CreateMatrix(8 , 8);	
		afiseazaArray2D(matrice);
		
		// putem apela functiile direct din main
		// prin decomentarea codului de mai jos
		/*
		System.out.println();
		int[] vector = convert2dTo1d(matrice);
		Arrays.sort(vector);
		int[][] matriceZigZag = makeZigZagMatrix(vector);
		afiseazaArray2D(matriceZigZag);
		*/
		
		// SAU putem apela direct ZigZag, care face totul
		int[][] output = ZigZag(matrice);
	}
	
	// creaza o matrice de dimensiuni date 
	public static int[][] CreateMatrix(int rows, int cols){
		
		int[][] matrix = new int[rows][cols];
		int k=100;
		for (int i=0; i< rows; i++){
			for (int j=0; j<cols;j++){
			matrix[i][j] = k;
			k--;
			}		
		}
		return matrix;		
	}
	     	
	// fonctie care aranjeaza coeficientii unei matrici de 8x8 in zig-zag
	// NU AM MAI FOLOSIT-O
	public static int[][] ZigZag(int[][] imput){		
		
		int[][]output = new int[8][8]; 
		int[]temp = new int[64];
		
		// copiem matricea de imput intr-un vector
		temp = convert2dTo1d(imput);
		
		// ordonam coeficientii matricei 8x8 in ordine crescatoare
		Arrays.sort(temp);
		afiseazaArray1D(temp);
		
		// creem matricea de output, cu coeficientii in zig-zag din vectorul temp care este sortat
		output = makeZigZagMatrix(temp);
		afiseazaArray2D(output);
		
		return output;
		
	}
	
	// scriem o matrice intr-un vector
	public static int[] convert2dTo1d(int[][] imput){
		
		int k=0;
		int[] temp = new int[imput.length * imput.length] ;
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
	
	// afisarea unui vector
	public static void afiseazaArray1D(int[] array){
		for (int i=0; i < array.length; i++ )
			System.out.println(i + " " + array[i]);		
	}
	
	// afisarea unei matrici
	public static void afiseazaArray2D(int[][] array){
		for (int i=0; i < array.length; i++ )
		{
			for (int j=0; j< array[i].length; j++)
			{
			System.out.print(array[i][j]+" \t");		
			}
			System.out.println("");
		}
	}
	
	// functia primeste ca intrare un vector ordonat crescator si contruieste din el o matrice Zig-Zag
	private static int[][] makeZigZagMatrix(int[] imput) {
				
		// initializam N ca radical din lungimea vectorului de intrare
		// se poate initializa direct cu 8 pentru matrici de 8x8
		int N = (int)Math.sqrt(imput.length);
		
        int[][] zz = new int[N][N];
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
	
}
