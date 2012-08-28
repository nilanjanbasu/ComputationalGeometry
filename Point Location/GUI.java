import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class Edge
{
	private Point start,end;
	
	public Edge(int a,int b,int c,int d)
	{
		start = new Point(a,b);
		end = new Point(c,d);
	}
	public Edge(double a, double b, double c, double d) {
		start = new Point((int)a,(int)b);
		end = new Point((int)c,(int)d);
	}
	public Edge(Point a,Point b)
	{
		start = a;
		end = b;
	}	
	public Point getStartPoint()
	{
		return getLeftmostPoint();
	}
	public Point getEndPoint()
	{
		return getRightmostPoint();
	}
	public Point getLeftmostPoint()
	{
		return (start.getX()<start.getX() ? start : end);
	}
	public Point getRightmostPoint()
	{
		return (start.getX() > start.getX() ? start : end);
	}	
	@Override
	public String toString()
	{
		String st="("+start.x+","+start.y+")";
		String en = "("+end.x+","+end.y+")";
		return st+en;
	}
	public boolean above(Point p) {

	    if(p.equals(start) || p.equals(end)) 
	    	return false;

	    // Assume that p is within the proper x coords of the shape
	    int x1 = getStartPoint().x;
	    int x2 = getEndPoint().x;
	    int y1 = getStartPoint().y;
	    int y2 = getEndPoint().y;
	    return (p.y > lineAprox(x1,y1,x2,y2,p.x));

	  }

	  public boolean below(Point p) {

		if(p.equals(start) || p.equals(end)) 
		   	return false;

	    return !above(p);

	  }

	  private float lineAprox(int x1, int y1, int x2, int y2, int x){
	    return (float)((float)(((float)(y2-y1)/(x2-x1))*(x-x1))+y1);
	  }
	
}
public class GUI extends JApplet {
	
	private final static int DRAW_MODE = 1;
	private final static int QUERY_MODE = 2;
	
	Driver driver = null;	
	Set<Edge> set;
	DrawPanel drawPanel;
	
	private int Mode = DRAW_MODE;
	
	public void init()
	{		
		 set = new HashSet<Edge>();
		 getContentPane().setLayout(new BorderLayout());
		 
		 drawPanel = new DrawPanel();
		 
		 JPanel buttonPane = new JPanel();
		 buttonPane.setLayout(new GridLayout(3,1));
		 JButton clearButton = new JButton("Clear"); 
		 JButton doneButton = new JButton("Done");
		 JButton penUpButton = new JButton("Pen Up");
		 
		 buttonPane.add(clearButton);
		 buttonPane.add(doneButton);
		 buttonPane.add(penUpButton);
		 
		 getContentPane().add(drawPanel,BorderLayout.CENTER);
		 getContentPane().add(buttonPane,BorderLayout.WEST);
		 
		 clearButton.addActionListener(new
				 ActionListener()
		 		 {
			 		public void actionPerformed(ActionEvent e)
			 		{
			 			set.clear();
			 			drawPanel.clear();
			 			Mode = DRAW_MODE;
			 			driver = null;
			 		}
		 		 });
		 doneButton.addActionListener( new
				 ActionListener()
		 		 {
					 public void actionPerformed(ActionEvent e)
				 	 {
						 driver = new Driver(set, drawPanel.getWidth(), drawPanel.getHeight());				 
				 		 Mode = QUERY_MODE;
				 	 }
		 		 });
		 penUpButton.addActionListener( new
				 ActionListener()
		 		 {
					 public void actionPerformed(ActionEvent e)
				 	 {
				 			drawPanel.penUp();
				 	 }
		 		 });		 
	}
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Point Location Demo");
		
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
		 
		JApplet app = new GUI();
		frame.getContentPane().add("Center",app);
		app.init();
		frame.pack();
		
		/*Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();*/
		frame.setSize(500, 350);
		frame.setVisible(true);	
	}
	class DrawPanel extends JPanel implements MouseListener,MouseMotionListener
	{	
		private Point start,end;
		private BufferedImage buff=null,t_buff=null;
		
		
		private Point draw_pt = null;
		
		public DrawPanel()
		{
			setSize(300,250);		
			start = end =null;
			addMouseListener(this);
			addMouseMotionListener(this);
		}		
		public void penUp()
		{
			start = null;
		}
		public void clear()
		{
			start=end=null;
			buff=null;
			draw_pt=null;
			t_buff=null;
			repaint();		
		}
		public void fill(Point f)
		{
			if(t_buff ==null)
			{
				t_buff = deepCopy(buff);
				System.out.println("here");
			}
			else
				buff = deepCopy(t_buff);
			
			Stack<Point> st = new Stack<Point>();
			st.push(f);
			while(!st.empty())
			{
				Point tt= st.pop();
				if (tt.x<0 ||tt.y<0 || tt.x >=this.getWidth() || tt.y>= this.getHeight())
					continue;
				else if(new Color(buff.getRGB(tt.x,tt.y)).equals(Color.BLACK) )
					continue;
				//System.out.print("before");
				buff.setRGB(tt.x,tt.y,Color.BLACK.getRGB());
				//System.out.println("after");
				
				st.push(new Point(tt.x+1,tt.y));
				st.push(new Point(tt.x,tt.y+1));
				st.push(new Point(tt.x,tt.y-1));
				st.push(new Point(tt.x-1,tt.y));
				//repaint();
			}
			repaint();
		}
		
		private BufferedImage deepCopy(BufferedImage bi) {
			 ColorModel cm = bi.getColorModel();
			 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			 WritableRaster raster = bi.copyData(null);
			 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
			}
		private void paintBuffer()
		{			
			Graphics2D g2 = buff.createGraphics();
			g2.drawLine(start.x, start.y, end.x, end.y);
			repaint();
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D)g;
			
			if(buff == null)
			{
				int h = getHeight();
				int w = getWidth();			
				buff = (BufferedImage)createImage(w, h);
				Graphics2D gc = buff.createGraphics();
				gc.setColor(Color.WHITE);
				gc.fillRect(0, 0, w, h);			
			}
			
			g2.drawImage(buff, null, 0, 0);
			if(draw_pt != null)
			{
				g2.setColor(Color.red);
				g2.fillOval(draw_pt.x-4, draw_pt.y-4,8,8);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e){
			switch(Mode)
			{
			case DRAW_MODE:
				
				end = start;
				if(draw_pt == null)
					start = e.getPoint();
				else
					start = draw_pt;				
				if(end != null)
				{
					set.add(new Edge(start,end));
					paintBuffer();
				}
				break;
			case QUERY_MODE:
				
				fill(driver.queryInMap(e.getPoint()));
				break;
			}		
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
		@Override
		public void mouseDragged(MouseEvent arg0) {}
		@Override
		public void mouseMoved(MouseEvent e) {			
			
			Iterator<Edge> i = set.iterator();
			Point p = e.getPoint();
			while(i.hasNext())
			{
				Edge temp = i.next();
				Point pt1 = temp.getStartPoint();
				Point pt2 = temp.getEndPoint();
				if(getDistance(pt1,p) < 5 ){
					draw_pt = pt1;
					break;
				}
				else if(getDistance(pt2, p)<5)
				{
					draw_pt = pt2;
					break;
				}
				else
					draw_pt = null;
			}
			repaint();
			
		}
		private double getDistance(Point a,Point b)
		{
			double sqr_x = a.x - b.x;
			sqr_x *= sqr_x;
			double sqr_y = a.y - b.y;
			sqr_y *= sqr_y;
			
			return Math.sqrt(sqr_x+sqr_y);
		}
	}
}