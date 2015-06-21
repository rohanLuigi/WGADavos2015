import java.awt.*;
import java.applet.*;
import java.lang.Math;
import java.lang.*;
import java.util.Vector;
import plotxy.PlotCanvas;
import plotxy.Plotter;   

class MyPlotter extends Plotter { // This has to be done before PlotterAppletV 
                                  // to work correctly with Netscape!
   
  public MyPlotter(PlotCanvas canvas){
   super(canvas);
  }

   public double[] getx(){
     int n = 2*integerParms[1];
     double x[] = new double[n+1];
     for (int i=0;i<=n;i++){
        x[i] = i;
     } 
     return x;
   }
   public double[] gety(){
     double p = doubleParms[0];
     int n    = 2*integerParms[1];
     int g = integerParms[2];
     double s = doubleParms[3];
     Qdist qdist = new Qdist();
     return qdist.dist(p,n,g,s);
   }
}

public class PlotterAppletV extends Applet {
    MyPlotter plotter;
    public void init() {
      setLayout(new BorderLayout());
      PlotCanvas c = new PlotCanvas();
      add("Center", c);
      plotter = new MyPlotter(c);
      plotter.putNumberofParms(4);
      plotter.putParm(" Initial Gene Frequency","0.7",0);
      plotter.putParm(" Effective Population Size","25",1);
      plotter.putParm(" Number of Generations","10",1);
      plotter.putParm(" Coefficinet of Selection","0.1",0);
      add("East", plotter);
    }

    public boolean handleEvent(Event e) {
	if (e.id == Event.WINDOW_DESTROY) {
	    System.exit(0);
	}
	return false;
    }
}


class Qdist{
  double A[][],y[],z[],s;
  public double [] dist(double p, int n, int ngen,double s){
    this.s = s;
    double q = 1 - p;
    double q1 = (q - 0.5*s*q - 0.5*s*q*q)/(1 - s*q);
    y = Binomial.dist(n,1 - q1);
    z = new double[n+1];
    mkA(n);
    for (int t=2; t<=ngen; t++){         // loop over generations
      for (int row=0; row<=n; row++){    // matrix multiplication 
	z[row]=0;
	for (int col=0;col<=n; col++){
	  z[row] += A[row][col]*y[col];
	}
      }
      for (int row=0; row<=n; row++){    // copy z to y
	y[row]=z[row];
      }
    }
    return y;
  }
  
  
  
  public void mkA(int n) {
    double X[];
    A = new double[n+1][n+1];
    for (int col=0; col<=n; col++) {
      double p = (double) col/n;
      double q = 1 - p;
      double q1 = (q - 0.5*s*q - 0.5*s*q*q)/(1 - s*q);
      X = Binomial.dist(n,1-q1);
      for (int row=0; row<=n; row++) {
	A[row][col] = X[row];
      }
    }
  } 
}

class Binomial {
  public static double [] dist(int n, double p) {
    double [] X = new double[n+1];
    X[0] = 1 - p;
    X[1] = p;
    double temp1, temp2;
    for (int i=2; i<=n; i++) {
      temp1 = X[0];
      X[0] = (1-p)*temp1;
      for (int j=1; j<i; j++){
	temp2 = temp1*p + X[j]*(1-p);
	temp1 = X[j];
	X[j] = temp2;
      }
      X[i] = temp1*p;
    }
    return X;
  }
}	

