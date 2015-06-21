package plotxy;

import java.awt.*;
import java.applet.*;
import java.lang.Math;
import java.lang.*;
import java.util.Vector;
import plotxy.PlotCanvas;

abstract public class Plotter extends Panel {
    TextField parmNames[];
    public double doubleParms[];
    public int integerParms[], integerType[], currentParm, nParms;
    Button plotButton, clearButton;
    
    PlotCanvas canvas;

  public Plotter(PlotCanvas canvas){
	this.canvas = canvas;
        currentParm = 0;
        

    }
    abstract public double[] getx();
 
    abstract public double[] gety();
  
    
    public void putNumberofParms(int n) {
        nParms = n;
	parmNames = new TextField[n];
        doubleParms = new double[n];
        integerParms = new int[n];
        integerType = new int[n];
        setLayout(new GridLayout(2*(n+1),1));
        add(plotButton = new Button("Plot"));
        add(clearButton = new Button("Clear"));
   } 
   public void putParm(String label, String deflt, int IDtype) {
        integerType[currentParm] = IDtype;
        add(new Label(label));
	add(parmNames[currentParm] = new TextField(deflt, 4));
        TextField pn = parmNames[currentParm];
        if (IDtype == 0) {
                   try {
      	              doubleParms[currentParm] = Double.valueOf(pn.getText()).doubleValue();
                   }
                   catch (java.lang.NumberFormatException e) {
                       ;
                   }
               }
               else {
                  try {
      	             integerParms[currentParm] = Integer.parseInt(pn.getText());
                  }
                   catch (java.lang.NumberFormatException e) {
                      ;
                  }
              }	
        currentParm++;
   }
                                              
    public boolean action(Event ev, Object arg) {
	    if (ev.target == plotButton) {
               for (int i=0; i<nParms ; i++){
                  TextField pn = parmNames[i];
                  if (integerType[i] == 0) {
	              try {
      	                 doubleParms[i] = Double.valueOf(pn.getText()).doubleValue();
                      }
                      catch (java.lang.NumberFormatException e) {
                          ;
                      }
                  }
                  else {
                     try {
      	                integerParms[i] = Integer.parseInt(pn.getText());
                     }
                      catch (java.lang.NumberFormatException e) {
                         ;
                     }
                  } 
                }
                canvas.putxy(getx(),gety());
	        return true;
	     }
             else if (ev.target == clearButton) {
               canvas.clear();
               return true;
             }  
             else {
	        return false;
            }
   }

}
