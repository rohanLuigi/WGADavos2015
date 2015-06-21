import java.awt.*;
import java.applet.*;
import java.lang.Math;
import java.lang.*;
import java.util.Vector;


public class PlotterAppletV extends Applet {
    Plotter plotter;
    public void init() {
      setLayout(new BorderLayout());
      PlotCanvas c = new PlotCanvas();
      add("Center", c);
      plotter = new Plotter(c);
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

class Plotter extends Panel {
    TextField parmNames[];
    double doubleParms[];
    int integerParms[], integerType[], currentParm, nParms;
    Button plotButton, clearButton;
    
    PlotCanvas canvas;

  public Plotter(PlotCanvas canvas){
	this.canvas = canvas;
        currentParm = 0;
        

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


class PlotCanvas extends Canvas {
    Font  font;
    Vector X = new Vector (10,5);
    Vector Y = new Vector (10,5);
    int plotn = 0;
    int first=1;
  
  public void clear(){
      first =1;
      plotn = 0;
      Y.removeAllElements();
      X.removeAllElements();
      repaint();
  }

  public void paint(Graphics g) {

      Rectangle r = bounds();

      int adjw = r.width - 70;
      int adjh = r.height - 50;

      int hlines = adjh / 10;
      int vlines = adjw / 10;
    
      g.setColor(Color.pink);
      for (int i = 1; i <= hlines; i++) {
        g.drawLine(0, i * 10, adjw, i * 10);
      }
      for (int i = 1; i <= vlines; i++) {
        g.drawLine(i * 10, 0, i * 10, adjh);
      }
      g.setColor(Color.black);
      g.drawRect(0, 0, adjw, adjh );
      g.setFont(new Font("Helvetica", Font.PLAIN, 8)) ;
      if (first == 0) plotxy(g);
      first = 0;
    
    
  }

  public void plotxy (Graphics g) {

    Rectangle r = bounds();
 // find min and max value of y
    double maxy = Double.MIN_VALUE;
    double miny = Double.MAX_VALUE;
    for (int ploti=0; ploti<plotn; ploti++){
      double y[] = (double[]) Y.elementAt(ploti);
      for (int i=0; i<y.length; i++) {
        maxy = Math.max(y[i],maxy);
        miny = Math.min(y[i],miny);
      }
    }

    for (int ploti=0; ploti<plotn; ploti++){

 // compute scaling factor for x and y values
      double y[] = (double[]) Y.elementAt(ploti);
      double x[] = (double[]) X.elementAt(ploti);
      int adjw = r.width - 70;
      int adjh = r.height - 50;
      double xscale = adjw/(x[x.length-1] - x[0]);
      double yscale = adjh/(maxy - miny)*.8;
      double ytics  = (maxy-miny)/4, ymark;

 // now plot x and y
      switch (ploti) {
        case 0: g.setColor(Color.blue); break;
        case 1: g.setColor(Color.red); break;
        case 2: g.setColor(Color.green); break;
        case 3: g.setColor(Color.yellow); break;
        case 4: g.setColor(Color.cyan); break;
        case 5: g.setColor(Color.magenta); break; 
        default:g.setColor(Color.black); break; 
      }
      for (int i=0; i < x.length-1; i++){
        g.drawLine((int)((x[i  ]-x[0])*xscale),   (int)(adjh - (y[i  ]-miny)*yscale),
                 (int)((x[i+1]-x[0])*xscale),   (int)(adjh - (y[i+1]-miny)*yscale)); 
      }
      g.setColor(Color.black);
      for (int i=0; i<=4; i++){
         ymark = i*ytics;
         g.drawString(Double.toString (miny+ymark), adjw+1, (int)(adjh - ymark*yscale) );
      }
    }
       
  }  
    public void putxy(double[] x, double[] y) {
        X.addElement(x);
	Y.addElement(y);
        plotn++;
        repaint();
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

