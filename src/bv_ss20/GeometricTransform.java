// BV Ue2 SS2020 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ss20;


public class GeometricTransform {

	public enum InterpolationType { 
		NEAREST("Nearest Neighbour"), 
		BILINEAR("Bilinear");
		
		private final String name;       
	    private InterpolationType(String s) { name = s; }
	    public String toString() { return this.name; }
	};
	
	public void perspective(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion, InterpolationType interpolation) {
		switch(interpolation) {
		case NEAREST:
			perspectiveNearestNeighbour(src, dst, angle, perspectiveDistortion);
			break;
		case BILINEAR:
			perspectiveBilinear(src, dst, angle, perspectiveDistortion);
			break;
		default:
			break;	
		}
		
	}

	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 * 
	 * xtr = x_src
	 * ytr = y_src cos(φ)
	 * ztr = y_src sin(φ)
	 * 
	 * x_src´ = xtr / c(ztr)
	 * y_src´ = ytr / c(ztr)
	 * 
	 * c(ztr) = s ztr + 1 s ist Stärke der Verzerrung
	 * 
	 * x_src´ = (cos(φ) x_src) / (s sin(φ) x_src + 1)
	 * y_src´ = y_src / (s sin(φ) x_src + 1)  
	 */
	public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
        // TODO: implement the geometric transformation using nearest neighbour image rendering

		int height_dst = dst.height;
        int width_dst = dst.width;
        
        int height_src = src.height;
        int width_src = src.width;
        
        int midHeight_dst = dst.height/2;
        int midWidth_dst = dst.width/2;
       
        int midHeight_src = src.height/2;
        int midWidth_src = src.width/2;
      
        double scaleWidth = width_dst / width_src;
        double scaleHeight = height_dst / height_src;
        
        double angle_new = degToRad(angle);
        double s = perspectiveDistortion;

        int argbS[] = src.argb;
        int argbDst[] = dst.argb;

        // NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radians

        for (int y_dst = -midHeight_dst; y_dst < midHeight_dst; y_dst++) {
            for (int x_dst = -midWidth_dst; x_dst < midWidth_dst; x_dst++) {


            	int x_src = (int) (x_dst / (Math.cos(angle_new) - x_dst * s * Math.sin(angle_new)) * scaleWidth);
            	int y_src = (int) (y_dst * s * x_src * Math.sin(angle_new) + y_dst * scaleHeight);
            	
                y_src += midHeight_src;
                x_src += midWidth_src;
                
                if (y_src < height_src && x_src < width_src && y_src >= 0 && x_src >= 0) {
                
                	int pos_src = y_src * width_src + x_src;
                	int pos_dst = (y_dst + midHeight_dst) * width_dst + (x_dst + midWidth_dst);

                
                	argbDst[pos_dst] = argbS[pos_src];

                }
            }
        }
    }


	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 */
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using bilinear interpolation

		int height_dst = dst.height;
        int width_dst = dst.width;
        
        int height_src = src.height;
        int width_src = src.width;
        
        int midHeight_dst = dst.height/2;
        int midWidth_dst = dst.width/2;
       
        int midHeight_src = src.height/2;
        int midWidth_src = src.width/2;
      
        double scaleWidth = width_dst / width_src;
        double scaleHeight = height_dst / height_src;
        
        double angle_new = degToRad(angle);
        double s = perspectiveDistortion;

        int argbS[] = src.argb;
        int argbDst[] = dst.argb;
        
        int PointA, PointB, PointC, PointD;

        // NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radians

        for (int y_dst = -midHeight_dst; y_dst < midHeight_dst; y_dst++) {
            for (int x_dst = -midWidth_dst; x_dst < midWidth_dst; x_dst++) {

            	double x_src = (x_dst / (Math.cos(angle_new) - x_dst * s * Math.sin(angle_new))) * scaleWidth;
            	double y_src = (y_dst * s * x_src * Math.sin(angle_new) + y_dst) * scaleHeight;
            	
            	int x_int = (int) (x_src);
            	int y_int = (int) (y_src);
            	
            	// v und h sind die Nachkommastelle vom skalierten Y-Wert/X-Wert
				double v = y_src - y_int;
				double h = x_src - x_int;
            	
                y_int += midHeight_src;
                x_int += midWidth_src;
                
                // Alte Position
				int position = y_int * width_src + x_int;
 
                if (y_int < height_src && x_int < width_src && y_int >= 0 && x_int >= 0) {

					// Die Punkte A, B, C und D und ihre ARGB-Werte
					PointA = argbS[position];
					if(position + 1 < argbS.length)
					{
						PointB = argbS[position + 1];
					} else {
						PointB = PointA;
					}
					if(position + width_src + 1 < argbS.length) 
					{
						PointC = argbS[position + width_src];
						PointD = argbS[position + width_src + 1];
					} else {
						PointC = PointA;
						PointD = PointB;
					}	

					int[] argbA = { ((PointA >> 16) & 0xff), ((PointA >> 8) & 0xff), (PointA & 0xff) };
					int[] argbB = { ((PointB >> 16) & 0xff), ((PointB >> 8) & 0xff), (PointB & 0xff) };
					int[] argbC = { ((PointC >> 16) & 0xff), ((PointC >> 8) & 0xff), (PointC & 0xff) };
					int[] argbD = { ((PointD >> 16) & 0xff), ((PointD >> 8) & 0xff), (PointD & 0xff) };
					
					int r, g, b;

					// Formel aus: http://home.htw-berlin.de/~barthel/veranstaltungen/GLDM/vorlesungen/07_GLDM_Bildmanipulation3_geometrische_2.pdf
					// bzw. http://home.htw-berlin.de/~barthel/veranstaltungen/GLDM/vorlesungen/06_GLDM_Bildmanipulation3_geometrische.pdf
					// P = A * (1-h) * (1-V) + B * h * (1-v) + C * (1-h) * v + D * h * v
					r = (int) (argbA[0] * (1 - h) * (1 - v) + argbB[0] * h * (1 - v) + argbC[0] * (1 - h) * v
							+ argbD[0] * h * v);
					g = (int) (argbA[1] * (1 - h) * (1 - v) + argbB[1] * h * (1 - v) + argbC[1] * (1 - h) * v
							+ argbD[1] * h * v);
					b = (int) (argbA[2] * (1 - h) * (1 - v) + argbB[2] * h * (1 - v) + argbC[2] * (1 - h) * v
							+ argbD[2] * h * v);
    				
    				// Werte korrigieren
    				if (r > 255) {
    					r = 255;
    				}
    				else if (r < 0) {
    					r = 0;
    				}
    				if (g > 255) {
    					g = 255;
    				}
    				else if (g < 0) {
    					g = 0;
    				}
    				if (b > 255) {
    					b = 255;
    				}
    				else if (b < 0) {
    					b = 0;
    				}
                	
                	int pos_dst = (y_dst + midHeight_dst) * width_dst + (x_dst + midWidth_dst);

                
                	argbDst[pos_dst] = (0xff<<24) | (r<<16) | (g<<8) | (b);

                }
            }
        }
 	}
	
	public double degToRad(double degrees) 
	{
		return Math.toRadians(degrees);
	}


}
