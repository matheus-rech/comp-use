/**********************************************************
*
*   BiCubicSpline.java
*
*   Class for performing an interpolation on the tabulated
*   function y = f(x1,x2) using a natural bicubic spline
*   Assumes second derivatives at end points = 0 (natural spine)
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:	May 2002
*   UPDATE: 20 May 2003
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web page:
*   BiCubicSpline.html
*
*   Copyright (c) May 2003   Michael Thomas Flanagan
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/

package flanagan.interpolation;

public class BiCubicSpline{

    	private int nPoints = 0;   	    // no. of x1 tabulated points
    	private int mPoints = 0;   	    // no. of x2 tabulated points
    	private double[][] y = null;  	// y=f(x1,x2) tabulated function
    	private double[] x1 = null;   	// x1 in tabulated function f(x1,x2)
    	private double[] x2 = null;   	// x2 in tabulated function f(x1,x2)
    	private CubicSpline csn[] = null;    // nPoints array of CubicSpline instances
    	private CubicSpline csm = null;      // CubicSpline instance
    	private double yp1 = 0.0D;    	// first derivative at point one
                                    	// default value in CubicSpline = zero (natural spline)
    	private double ypn = 0.0D;    	// first derivative at point n
                                    	// default value i CubicSpline = zero (natural spline)
                           		        // yp1 and ypn are fixed at the natural spline option in BiCubicSpline
        private boolean derivCalculated = false;    // = true when the derivatives have been calculated

    	// Constructor
    	public BiCubicSpline(double[] x1, double[] x2, double[][] y){
        	this.nPoints=x1.length;
        	this.mPoints=x2.length;
        	if(this.nPoints!=y.length)throw new IllegalArgumentException("Arrays x1 and y-row are of different length" + this.nPoints + " " + y.length);
        	if(this.mPoints!=y[0].length)throw new IllegalArgumentException("Arrays x2 and y-column are of different length"+ this.mPoints + " " + y[0].length);
          	if(this.nPoints<3 || this.mPoints<3)throw new IllegalArgumentException("The data matrix must have a minimum size of 3 X 3");

        	this.csm = new CubicSpline(this.nPoints);
        	this.csn = CubicSpline.oneDarray(this.nPoints, this.mPoints);
        	this.x1 = new double[this.nPoints];
        	this.x2 = new double[this.mPoints];
        	this.y = new double[this.nPoints][this.mPoints];
        	for(int i=0; i<this.nPoints; i++){
            		this.x1[i]=x1[i];
        	}
        	for(int j=0; j<this.mPoints; j++){
            		this.x2[j]=x2[j];
        	}
        	for(int i =0; i<this.nPoints; i++){
            		for(int j=0; j<this.mPoints; j++){
                		this.y[i][j]=y[i][j];
            		}
        	}
        	this.yp1 = 1e40;
        	this.ypn = 1e40;
    	}

    	//  METHODS

    	//	Calculates the second derivatives of the tabulated function for
    	//  	use by the bicubic spline interpolation method (.interpolate)
    	public void calcDeriv(){
        	double[] yTempn = new double[mPoints];

	    	for(int i=0; i<nPoints; i++){
	        	for(int j=0; j<mPoints; j++)yTempn[j]=y[i][j];
	        	csn[i].resetData(x2,yTempn);
	        	csn[i].calcDeriv();
	    	}
	    	this.derivCalculated = true;
    	}

    	//	Returns an interpolated value of y for a value of x
    	//  	from a tabulated function y=f(x1,x2)
    	public double interpolate(double xx1, double xx2){

    	    if(!this.derivCalculated)this.calcDeriv();

	    	double[] yTempm = new double[nPoints];
	    	double ym = 0.0D;

	    	for (int i=0;i<nPoints;i++){
		    	yTempm[i]=csn[i].interpolate(xx2);
	    	}
	    	csm.resetData(x1,yTempm);
	    	csm.calcDeriv();
	    	return csm.interpolate(xx1);
    	}
}

