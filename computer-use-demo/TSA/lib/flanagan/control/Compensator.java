/*      Class Compensator
*
*       This class contains the constructor to create an instance of
*       a generalised compensator,
*           K(a + s)/(b + s)
*       and the methods needed to use this process in simulation
*       of control loops.
*
*       This class is a subclass of the superclass BlackBox.
*
*       Author:  Michael Thomas Flanagan.
*
*       Created: 14 May 2005
*
*
*       DOCUMENTATION:
*       See Michael T Flanagan's JAVA library on-line web page:
*       Compensator.html
*
*   Copyright (c) May 2005  Michael Thomas Flanagan
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/


package flanagan.control;
import flanagan.complex.Complex;
import flanagan.complex.ComplexPoly;

public class Compensator extends BlackBox{

    private double kConst;  // K constant in compensator equation above
    private double aConst;  // a constant in compensator equation above
    private double bConst;  // b constant in compensator equation above

    // Constructor
    // Sets all constants to unity
    public Compensator(){
        this.aConst = 1.0D;
        this.bConst = 1.0D;
        this.kConst = 1.0D;
        super.sNumerDeg = 1;
        super.sDenomDeg = 1;
        super.sNumer = new ComplexPoly(1.0D, 1.0D);
        super.sDenom = new ComplexPoly(1.0D, 1.0D);
        super.sZeros = Complex.oneDarray(1);
        super.sPoles = Complex.oneDarray(1);
        super.ztransMethod=1;
        super.name = "Compensator";
        super.fixedName = "Compensator";
        this.calcPolesZerosS();
        super.addDeadTimeExtras();
    }

    // Constructor
    // constants set from argument list
    public Compensator(double kk, double aa, double bb){
        this.aConst = aa;
        this.bConst = bb;
        this.kConst = kk;
        super.sNumerDeg = 1;
        super.sDenomDeg = 1;
        super.sNumer = new ComplexPoly(this.aConst*kConst, kConst);
        super.sDenom = new ComplexPoly(this.bConst, 1.0D);
        super.sZeros = Complex.oneDarray(1);
        super.sPoles = Complex.oneDarray(1);
        super.ztransMethod=1;
        super.name = "Compensator";
        super.fixedName = "Compensator";
        this.calcPolesZerosS();
        super.addDeadTimeExtras();
    }

    public void setCoeff(double kk, double aa, double bb){
        this.aConst = aa;
        this.bConst = bb;
        this.kConst = kk;
        Complex[] num = Complex.oneDarray(2);
        num[0].reset(this.aConst*this.kConst, 0.0D);
        num[1].reset(this.kConst, 0.0D);
        super.sNumer.resetPoly(num);
        Complex[] den = Complex.oneDarray(2);
        den[0].reset(this.bConst, 0.0D);
        den[1].reset(1.0D, 0.0D);
        super.sDenom.resetPoly(den);
        this.calcPolesZerosS();
        super.addDeadTimeExtras();
    }

    public void setK(double kk){
        this.kConst = kk;
        Complex co = new Complex(this.aConst*this.kConst, 0.0);
        super.sNumer.resetCoeff(0, co);
        co = new Complex(this.kConst, 0.0);
        super.sNumer.resetCoeff(1, co);
        this.calcPolesZerosS();
        super.addDeadTimeExtras();
    }

    public void setA(double aa){
        this.aConst = aa;
        Complex co = new Complex(this.aConst*this.kConst, 0.0);
        super.sNumer.resetCoeff(0, co);
        this.calcPolesZerosS();
        super.addDeadTimeExtras();
    }

    public void setB(double bb){
        this.bConst = bb;
        Complex co = new Complex(this.bConst, 0.0);
        super.sDenom.resetCoeff(0, co);
        this.calcPolesZerosS();
        super.addDeadTimeExtras();
    }

    public double getA(){
        return this.aConst;
    }

    public double getB(){
        return this.bConst;
    }

    public double getK(){
        return this.kConst;
    }

    // Calculate the zeros and poles in the s-domain
    protected void calcPolesZerosS(){
        super.sPoles[0].setReal(-aConst);
        super.sPoles[0].setReal(-bConst);
    }

    // REDUNDANT BlackBox METHODS
    // Setting the transfer function numerator in the s-domain
    public void setSnumer(double[] coeff){
        System.out.println("Transfer numerator is set by a constructor or special methods in FirstOrder; Not by setSnumer()");
    }

    public void setSnumer(Complex[] coeff){
        System.out.println("Transfer numerator is set by a constructor or special methods in FirstOrder; Not by setSnumer()");
    }

    public void setSnumer(ComplexPoly coeff){
        System.out.println("Transfer numerator is set by a constructor or special methods in FirstOrder; Not by setSnumer()");
    }

    // Seting the transfer function denominator in the s-domain
    public void setSdenom(double[] coeff){
        System.out.println("Transfer denominator is set by a constructor or special methods in FirstOrder; Not by setSdenom()");
    }

    public void setSdenom(Complex[] coeff){
        System.out.println("Transfer denominator is set by a constructor or special methods in FirstOrder; Not by setSdenom()");
    }

    public void setSdenom(ComplexPoly coeff){
        System.out.println("Transfer denominator is set by a constructor or special methods in FirstOrder; Not by setSdenom()");
    }

    // Setting the transfer function numerator in the z-domain
    public void setZnumer(double[] coeff){
        System.out.println("Transfer numerator is set by special methods in FirstOrder; Not by setZnumer()");
    }

    public void setZnumer(Complex[] coeff){
        System.out.println("Transfer numerator is set by special methods in FirstOrder; Not by setZnumer()");
    }

    public void setZnumer(ComplexPoly coeff){
        System.out.println("Transfer numerator is set by special methods in FirstOrder; Not by setZnumer()");
    }

    public void setZdenom(double[] coeff){
        System.out.println("Transfer numerator is set by special methods in FirstOrder; Not by setZdenom()");
    }

    public void setZdenom(Complex[] coeff){
        System.out.println("Transfer numerator is set by special methods in FirstOrder; Not by setZdenom()");
    }

    public void setZdenom(ComplexPoly coeff){
        System.out.println("Transfer numerator is set by special methods in FirstOrder; Not by setZdenom()");
    }
 }

