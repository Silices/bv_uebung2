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
	 * xtr = x
	 * ytr = y cos(φ)
	 * ztr = y sin(φ)
	 * 
	 * x´ = xtr / c(ztr)
	 * y´ = ytr / c(ztr)
	 * 
	 * c(ztr) = s ztr + 1 s ist Stärke der Verzerrung
	 * 
	 * x´ = (cos(φ) x) / (s sin(φ) x + 1)
	 * y´ = y / (s sin(φ) x + 1)  
	 */
	public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using nearest neighbour image rendering
		
		int height = src.height;
		int width = src.width;
		double angle_new = degToRad(angle);
		double s = perspectiveDistortion;
		
		int argbS[] = src.argb;
		int argbD[] = dst.argb;
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radians
		
		for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
            	
            	int pos = y * width + x;
            	
            	int x_new = (int) ((Math.cos(angle_new) * x) / (s * Math.sin(angle_new) * x + 1));
            	int y_new = (int) (y / (s * Math.sin(angle_new) * x + 1));
            	
            	int pos_new = y_new * width + x_new;
            	
            	argbD[pos_new] = argbS[pos];
		
		
            }
		}
		
		
		System.out.println(src.width);
		System.out.println(dst.width);
	}


	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 */
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using bilinear interpolation
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radians
		
 	}
	
	public double degToRad(double degrees) 
	{
		return Math.toRadians(degrees);
	}


}
