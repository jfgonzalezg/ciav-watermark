package mark;
/*File DctTrans.java

THIS VERSION IS OPTIMIZED FOR USE WITH 8x8 IMAGES.

See ForwardDCT01 for a general purpose version that works
for images of different sizes.  This version will also 
work with images of any size, but it is optimized for use
with 8x8-pixel images.

When transforming an 8x8-pixel image, this version uses an 
8x8 cosine lookup table instead of calling the cos 
function.  Otherwise, it calls the cosine function during
each iteration.  It is probably faster to use the lookup
table than it is to call the cosine function.

The static method named transform performs a forward 
Discrete Cosine Transform (DCT) on an incoming series
and returns the DCT spectrum.

Incoming parameters are:
  double[] x - incoming real data
  double[] y - outgoing real data*/

import static java.lang.Math.*;

public class DctTrans{

	int N=8;
    int[][] zigzag;
	
    
    
    public DctTrans() {
		zigzag = makeZigZagMatrix();
	}

	public double[] DctDirect(double[] x){

    	//The following values for the cosine table were obtained
    	// by running the program with the call to the cos
    	// function intact, printing the cosine of the argument,
    	// capturing it, and then inserting the values here.
    	// These are the cosine values used for transforming a
    	// series containing 8 values.

    	double y[]= new double[x.length];

    	double[][] cosineTable = {
    			{1.0,
    				1.0,
    				1.0,
    				1.0,
    				1.0,
    				1.0,
    				1.0,
    				1.0},

    				{0.9807852804032304,
    					0.8314696123025452,
    					0.5555702330196023,
    					0.19509032201612833,
    					-0.1950903220161282,
    					-0.555570233019602,
    					-0.8314696123025453,
    					-0.9807852804032304},

    					{0.9238795325112867,
    					0.38268343236508984,
    					-0.3826834323650897,
    					-0.9238795325112867,
    					-0.9238795325112868,
    					-0.38268343236509034,
    					0.38268343236509,
    					0.9238795325112865},

    					{0.8314696123025452,
    					-0.1950903220161282,
    					-0.9807852804032304,
    					-0.5555702330196022,
    					0.5555702330196018,
    					0.9807852804032304,
    					0.19509032201612878,
    					-0.8314696123025451},

    					{0.7071067811865476,
    					-0.7071067811865475,
    					-0.7071067811865477,
    					0.7071067811865474,
    					0.7071067811865477,
    					-0.7071067811865467,
    					-0.7071067811865471,
    					0.7071067811865466},

    					{0.5555702330196023,
    					-0.9807852804032304,
    					0.1950903220161283,
    					0.8314696123025456,
    					-0.8314696123025451,
    					-0.19509032201612803,
    					0.9807852804032307,
    					-0.5555702330196015},

    					{0.38268343236508984,
    					-0.9238795325112868,
    					0.9238795325112865,
    					-0.3826834323650899,
    					-0.38268343236509056,
    					0.9238795325112867,
    					-0.9238795325112864,
    					0.38268343236508956},

    					{0.19509032201612833,
    					-0.5555702330196022,
    					0.8314696123025456,
    					-0.9807852804032307,
    					0.9807852804032304,
    					-0.831469612302545,
    					0.5555702330196015,
    					-0.19509032201612858}
    	};


    	int N = x.length;
    	if(N == 8){
    		//Run optimized version by using cosine lookup table 
    		// instead of computing cosine values for each
    		// iteration.
    		//Outer loop interates on frequency values.
    		for(int k=0; k < N;k++){
    			double sum = 0.0;
    			//Inner loop iterates on time-series values.
    			for(int n=0; n < N; n++){
    				double cosine = cosineTable[k][n];
    				double product = x[n]*cosine;
    				sum += product;
    				//Enable the following statement to count and 
    				//display the number of computations.
    				//count++;
    			}//end inner loop

    			double alpha;
    			if(k == 0){
    				alpha = 1.0/sqrt(2);
    			}else{
    				alpha = 1;
    			}//end else
    				y[k] = sum*alpha*sqrt(2.0/N);
    		}//end outer loop
    	}else{
    		//Run regular version by computing cosine values for
    		// each iteration.
    		//Outer loop interates on frequency values.
    		for(int k=0; k < N;k++){
    			double sum = 0.0;
    			//Inner loop iterates on time-series values.
    			for(int n=0; n < N; n++){
    				double arg = PI*k*(2.0*n+1)/(2*N);
    				double cosine = cos(arg);
    				double product = x[n]*cosine;
    				sum += product;
    				//Enable the following statement to count and 
    				//display the number of computations.
    				//count++;
    			}//end inner loop

    			double alpha;
    			if(k == 0){
    				alpha = 1.0/sqrt(2);
    			}else{
    				alpha = 1;
    			}//end else
    			y[k] = sum*alpha*sqrt(2.0/N);
    		}//end outer loop
    	}//end else
    	return y;
    }//end transform method
  
    public double[] DctInvers(double[] y){
	    
	  //The following values for the cosine table were obtained
	  // by running the program with the call to the cos
	  // function intact, printing the cosine of the argument,
	  // capturing it, and then inserting the values here.
	  // These are the cosine values used for transforming a
	  // series containing 8 values.
	  double x[]= new double[y.length];
	    double[][] cosineTable = {
	                              {1.0,
	                              0.9807852804032304,
	                              0.9238795325112867,
	                              0.8314696123025452,
	                              0.7071067811865476,
	                              0.5555702330196023,
	                              0.38268343236508984,
	                              0.19509032201612833},
	                              
	                              {1.0,
	                              0.8314696123025452,
	                              0.38268343236508984,
	                              -0.1950903220161282,
	                              -0.7071067811865475,
	                              -0.9807852804032304,
	                              -0.9238795325112868,
	                              -0.5555702330196022},
	                              
	                              {1.0,
	                              0.5555702330196023,
	                              -0.3826834323650897,
	                              -0.9807852804032304,
	                              -0.7071067811865477,
	                              0.1950903220161283,
	                              0.9238795325112865,
	                              0.8314696123025456},
	                              
	                              {1.0,
	                              0.19509032201612833,
	                              -0.9238795325112867,
	                              -0.5555702330196022,
	                              0.7071067811865474,
	                              0.8314696123025456,
	                              -0.3826834323650899,
	                              -0.9807852804032307},
	                              
	                              {1.0,
	                              -0.1950903220161282,
	                              -0.9238795325112868,
	                              0.5555702330196018,
	                              0.7071067811865477,
	                              -0.8314696123025451,
	                              -0.38268343236509056,
	                              0.9807852804032304},
	                              
	                              {1.0,
	                              -0.555570233019602,
	                              -0.38268343236509034,
	                              0.9807852804032304,
	                              -0.7071067811865467,
	                              -0.19509032201612803,
	                              0.9238795325112867,
	                              -0.831469612302545},
	                              
	                              {1.0,
	                              -0.8314696123025453,
	                              0.38268343236509,
	                              0.19509032201612878,
	                              -0.7071067811865471,
	                              0.9807852804032307,
	                              -0.9238795325112864,
	                              0.5555702330196015},
	                              
	                              {1.0,
	                              -0.9807852804032304,
	                              0.9238795325112865,
	                              -0.8314696123025451,
	                              0.7071067811865466,
	                              -0.5555702330196015,
	                              0.38268343236508956,
	                              -0.19509032201612858}
	                             };

	    int N = y.length;
	    if(N == 8){
	      //Run optimized version by using cosine lookup table 
	      // instead of computing cosine values for each
	      // iteration.
	      //Outer loop interates on time values.
	      for(int n=0; n < N;n++){
	        double sum = 0.0;
	        //Inner loop iterates on frequency values
	        for(int k=0; k < N; k++){
	          double cosine = cosineTable[n][k];
	          double product = y[k]*cosine;
	          double alpha;
	          if(k == 0){
	            alpha = 1.0/sqrt(2);
	          }else{
	            alpha = 1;
	          }//end else
	            
	          sum += alpha * product;
	        }//end inner loop
	  
	        x[n] = sum * sqrt(2.0/N);
	      }//end outer loop
	    }else{
	      //Run regular version by computing cosine values for
	      // each iteration.
	      //Outer loop interates on time values.
	      for(int n=0; n < N;n++){
	        double sum = 0.0;
	        //Inner loop iterates on frequency values
	        for(int k=0; k < N; k++){
	          double arg = PI*k*(2.0*n+1)/(2*N);
	          double cosine = cos(arg);
	          double product = y[k]*cosine;
	          double alpha;
	          if(k == 0){
	            alpha = 1.0/sqrt(2);
	          }else{
	            alpha = 1;
	          }//end else
	            
	          sum += alpha * product;
	  
	        }//end inner loop
	  
	        x[n] = sum * sqrt(2.0/N);
	        
	      }//end outer loop
	    }//end else
	    return x;
	    
	  }//end transform method
  
    /* write dct coefficient matrix into 1D array in zig zag order */
    public double[] zigZag(double[][] m) {
    	double[] zz = new double[N*N];
    	for (int i=0;i<N;i++) {
    		for (int j=0;j<N;j++) zz[zigzag[i][j]]=m[i][j];
    	}
    	return zz;
    }  
    
    /*creeaza coeficientii mat. zig-zag in zigzag[][] 
    pe care ii foloseste in zigZag() pentru a ordona coef 
    mat 8*8 in zig zag */
    public int[][] makeZigZagMatrix() {
        int[][] zz = new int[N][N];
        int zval=0;
        int zval2=N*(N-1)/2;
        int i,j;
        for (int k=0;k<N;k++) {
          if (k%2==0) {
            i=0;
            j=k;
            while (j>-1) {
              zz[i][j]=zval;
              zval++;
              i++;
              j--;
            }
            i=N-1;
            j=k;
            while (j<N) {
              zz[i][j]=zval2;
              zval2++;
              i--;
              j++;
            }
          }
          else {
            i=k;
            j=0;
            while (i>-1) {
              zz[i][j]=zval;
              zval++;
              j++;
              i--;
            }
            i=k;
            j=N-1;
            while (i<N) {
              zz[i][j]=zval2;
              zval2++;
              i++;
              j--;
            }
          }
        }
        return zz;
    }
    
}//end class DctTrans
