import java.awt.Point;
import java.util.HashSet;


public abstract class Node {

	public static final int X_NODE=1;
	public static final int Y_NODE=2;
	public static final int LEAF =3;
	
	int type;
	Node rightChild=null;
	Node leftChild=null;
	Node parent=null;
	
	
	public Node(int t)
	{
		if(t>0 && t<4)
			this.type = t;
		else
		{
			this.type = -1;
			System.exit(1);
		}			
		rightChild=leftChild=null;
	}
	public void setRightChild(Node x)
	{
		rightChild = x;
		rightChild.parent = this;

	}
	public void setLeftChild(Node x)
	{
		leftChild = x;
		leftChild.parent = this;
	}
	
	public int getType()
	{
		return type;
	}
	public Node getRightChild()
	{
		return rightChild;
	}
	public Node getLeftChild()
	{
		return leftChild;
	}
	public void setType(int _type)
	{
		if(_type == Node.LEAF || _type == Node.X_NODE || _type==Node.Y_NODE)
			type = _type;
		else
			System.out.println("Error in Node:SetType():Wrong type set:"+_type);
	}
	public abstract Node getProperChild(Point p);
	public boolean isLeftChild(Node next) {
		if(this.leftChild == next)
			return true;
		else
			return false;		
	}
	public boolean isRightChild(Node next)
	{
		if(this.rightChild == next)
			return true;
		else
			return false;
	}
}
class Trapezoid extends Node
{
	public Point leftp;
	public Point rightp;
	public Edge topp;
	public Edge bottomp;
	public boolean merged = false;
	
	public Trapezoid upperleft,lowerleft,upperright,lowerright;
	
	public Trapezoid(Point l,Point r,Edge up,Edge bot)
	{
		super(LEAF);
		leftp=l;
		rightp=r;
		topp = up;
		bottomp = bot;
		parent=null;
		
		upperleft=null;
		upperright=null;
		lowerleft=null;
		lowerright=null;
		merged = false;
	}
	public void setParent(Node x)
	{
		parent = x;
	}
	public Node getParent()
	{
		return parent;
	}
	
	public void setNeighbors(Trapezoid ul,Trapezoid ll,Trapezoid ur,Trapezoid lr)
	{
		upperleft =ul;
		lowerleft = ll;
		upperright = ur;
		lowerright = lr;
	}
	public void replace(Node x)
	{
		if(parent.leftChild == this)
			parent.leftChild = x;
		else if(parent.rightChild == this)
			parent.rightChild = x;
		else
			System.out.println("Error:Node:replace:Parent does not have child");
	}
	
	@Override
	public Node getProperChild(Point p) {
		if (this.type == Node.LEAF)
			return (Trapezoid)this;
		return null;
	}
}
class XNode extends Node{
	
	Point p;
	
	public XNode()
	{
		super(Node.X_NODE);
	}
	public XNode(int x,int y)
	{
		super(Node.X_NODE);
		p = new Point(x,y);
	}
	public XNode(Point pp)
	{
		super(Node.X_NODE);
		p = pp;
	}
	boolean isOnRight(Point cd) //whether cd is to the right or not
	{
		if(p.getX() < cd.x) 
			return true;
		else
			return false;
	}
	boolean isOnLeft(Point cd)
	{
		return !this.isOnRight(cd);
	}
	@Override
	public Node getProperChild(Point p) {
		if(this.isOnLeft(p))
			return (Trapezoid)this.getLeftChild().getProperChild(p);
		else
			return (Trapezoid)this.getRightChild().getProperChild(p);
	}	
}

class YNode extends Node{
	
	Edge segment;
	public YNode(Edge s)
	{
		super(Y_NODE);
		segment = s;
	}
	public boolean isAbove(Point p)
	{
		return segment.above(p);
	}
	public boolean isBelow(Point p)
	{
		return segment.below(p);
	}
	@Override
	public Node getProperChild(Point p) {
		if(this.isAbove(p))
			return (Trapezoid)this.getLeftChild().getProperChild(p);
		else
			return (Trapezoid)this.getRightChild().getProperChild(p);
	}
		
}