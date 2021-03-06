package plotxy;

import java.awt.*;
import java.applet.*;
import java.lang.Math;
import java.lang.*;
import java.util.Vector;

public class PlotCanvas extends Canvas {
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

      int adjw = r.width - 60;
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
      int adjw = r.width - 60;
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

