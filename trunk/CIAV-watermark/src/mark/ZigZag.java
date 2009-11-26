package mark;

public class ZigZag {
	

	public static void main(String[] args) {
		  
	}
	
	public int[][] CreateMatrix(int rows, int cols){
		
		int[][] matrix = new int[rows][cols];
		int k=0;
		for (int i=0; i< rows; i++){
			for (int j=0; j<cols;j++){
			matrix[i][j] = k;
			k++;
			}		
		}
		return matrix;		
	}
	     
	
	
	public int[][] ZigZag(int[][] imput){		
		
		int rows = imput[].lenght();
		
		int[][]output = new int[rows][cols]; 
		
		return output;
		
	}
	

}
