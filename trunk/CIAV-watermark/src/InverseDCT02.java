/*File InverseDCT02.java
Copyright 2006, R.G.Baldwin
Rev 01/19/06

THIS VERSION IS OPTIMIZED FOR USE WITH 8x8 IMAGES.

See InverseDCT01 for a general purpose version that works
for images of different sizes.  This version will also 
work with images of any size, but it is optimized for use
with 8x8-pixel images.

When transforming an 8x8-pixel image, this version uses an 
8x8 cosine lookup table instead of calling the cos 
function.  Otherwise, it calls the cosine function during
each iteration.  It is probably faster to use the lookup
table than it is to call the cosine function.

The static method named transform performs an inverse 
Discreet Cosine Transform (DCT) on in incoming DCT
spectrum and returns the DCT time or image series.

See http://en.wikipedia.org/wiki/Discrete_cosine_transform
#DCT-II and http://rkb.home.cern.ch/rkb/AN16pp/node61.html
for background on the DCT.

This formulation is from 
http://www.cmlab.csie.ntu.edu.tw/cml/dsp/training/
coding/transform/dct.html

Incoming parameters are:
  double[] y - incoming real data
  double[] x - outgoing real data

Tested using J2SE 5.0 under WinXP.  Requires J2SE 5.0 or
later due to the use of static import of Math class.
**********************************************************/
import static java.lang.Math.*;

public class InverseDCT02{

  public static void transform(double[] y,double[] x){
    
  //The following values for the cosine table were obtained
  // by running the program with the call to the cos
  // function intact, printing the cosine of the argument,
  // capturing it, and then inserting the values here.
  // These are the cosine values used for transforming a
  // series containing 8 values.
                                
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
  }//end transform method
  //-----------------------------------------------------//
}//end class InverseDCT02