/*      Class LowPassPassive
*
*       This class contains the constructor to create an instance of
*       a low pass filter:
*           V(out) = V(in)/(1 +RCjomega)
*       and the methods needed to use this process in simulation
*       of control loops.
*
*       This class is a subclass of the superclass BlackBox.
*
*       Author:  Michael Thomas Flanagan.
*
*       Created: 21 May 2005
*
*
*       DOCUMENTATION:
*       See Michael T Flanagan's JAVA library on-line web page:
*       LowPassPassive.html
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

public class LowPassPassive extends BlackBox{

    private double resistance = 0.0D;       // Resistance value, R ohms
    private double capacitance = 0.0D;      // Capacitance value, C farads
    private double timeConstant = 0.0D;     // Time constant, RC seconds
    private boolean setR = false;           // = true when resistance set
    private boolean setC = false;           // = true when capacitance set

    // Constructor
    // Sets time constant to unity and the order to unity
    public LowPassPassive(){
        this.timeConstant = 1.0D;
        super.sNumerDeg = 0;
        super.sDenomDeg = 1;
        super.sNumer = new ComplexPoly(1.0D);
        super.sDenom = new ComplexPoly(1.0D, 1.0D);
        super.sPoles = Complex.oneDarray(1);
        super.ztransMethod=1;
        super.name = "Passive Low Pass Filter";
        super.fixedName = "Passive Low Pass Filter";
        this.calcPolesZerosS();
        super.addDeadTimeExtras();
    }

    public void setResistance(double res){
        this.resistance = res;
        this.timeConstant = res*this.capacitance;
        this.calcPolesZerosS();
        super.sDenom = ComplexPoly.rootsToPoly(this.sPoles);
        super.addDeadTimeExtras();
        this.setR = true;
    }

    public void setCapacitance(double cap){
        this.capacitance = cap;
        this.timeConstant = cap*this.resistance;
        this.calcPolesZerosS();
        super.sDenom = ComplexPoly.rootsToPoly(this.sPoles);
        super.addDeadTimeExtras();
        this.setC = true;
    }

    public void setTimeConstant(double tau){
        this.timeConstant = tau;
        this.calcPolesZerosS();
        super.sDenom = ComplexPoly.rootsToPoly(this.sPoles);
        super.addDeadTimeExtras();
    }

    public double getResistance(){
        if(this.setR){
            return this.resistance;
        }
        else{
            System.out.println("Class; LowPassPassive, method: getResistance");
            System.out.println("No resistance has been entered; zero returned");
            return 0.0D;
        }
    }

    public double getCapacitance(){
        if(this.setC){
            return this.capacitance;
        }
        else{
            System.out.println("Class; LowPassPassive, method: getCapacitance");
            System.out.println("No capacitance has been entered; zero returned");
            return 0.0D;
        }
    }

    public double getTimeConstant(){
        return this.timeConstant;
    }

    // Calculate the zeros and poles in the s-domain
    protected void calcPolesZerosS(){
            super.sPoles[0].setReal(-this.timeConstant);
    }

    // REDUNDANT BlackBox METHODS
    // Setting the transfer function numerator in the s-domain
    public void setSnumer(double[] coeff){
        System.out.println("Transfer numerator is set by a constructor or special methods in LowPassPassive; Not by setSnumer()");
    }

    public void setSnumer(Complex[] coeff){
        System.out.println("Transfer numerator is set by a constructor or special methods in LowPassPassive; Not by setSnumer()");
    }

    public void setSnumer(ComplexPoly coeff){
        System.out.println("Transfer numerator is set by a constructor or special methods in LowPassPassive; Not by setSnumer()");
    }

    // Seting the transfer function denominator in the s-domain
    public void setSdenom(double[] coeff){
        System.out.println("Transfer denominator is set by a constructor or special methods in LowPassPassive; Not by setSdenom()");
    }

    public void setSdenom(Complex[] coeff){
        System.out.println("Transfer denominator is set by a constructor or special methods in LowPassPassive; Not by setSdenom()");
    }

    public void setSdenom(ComplexPoly coeff){
        System.out.println("Transfer denominator is set by a constructor or special methods in LowPassPassive; Not by setSdenom()");
    }

    // Setting the transfer function numerator in the z-domain
    public void setZnumer(double[] coeff){
        System.out.println("Transfer numerator is set by special methods in LowPassPassive; Not by setZnumer()");
    }

    public void setZnumer(Complex[] coeff){
        System.out.println("Transfer numerator is set by special methods in LowPassPassive; Not by setZnumer()");
    }

    public void setZnumer(ComplexPoly coeff){
        System.out.println("Transfer numerator is set by special methods in LowPassPassive; Not by setZnumer()");
    }

    public void setZdenom(double[] coeff){
        System.out.println("Transfer numerator is set by special methods in LowPassPassive; Not by setZdenom()");
    }

    public void setZdenom(Complex[] coeff){
        System.out.println("Transfer numerator is set by special methods in LowPassPassive; Not by setZdenom()");
    }

    public void setZdenom(ComplexPoly coeff){
        System.out.println("Transfer numerator is set by special methods in LowPassPassive; Not by setZdenom()");
    }
 }

