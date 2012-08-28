import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

class Graph
{
	Node root=null;
	
	public Graph()
	{
		root = new XNode(); //dummy node
	}
	
	public Trapezoid retrieveNode(Point e)
	{
		Node temp = root.leftChild;
		
		while(temp.type != Node.LEAF)
		{
			if(temp.type == Node.X_NODE)
			{
				XNode xx = (XNode)temp;
				if(xx.isOnLeft(e))
					temp = xx.getLeftChild();
				else
					temp = xx.getRightChild();
			}
			else
			{
				YNode yy = (YNode)temp;
				if(yy.isAbove(e))
					temp = yy.getLeftChild();
				else
					temp = yy.getRightChild();
			}
		}
		return (Trapezoid)temp;
	}
	public List<Trapezoid> retrieveIntersectedNodes(Edge e)
	{
		Point start = e.getStartPoint();
		Point end   = e.getEndPoint();
		List<Trapezoid> list = new LinkedList<Trapezoid>();
		
		Trapezoid temp = retrieveNode(start);
		list.add(temp);
		while(temp != null && (temp.rightp != null && end.x>= temp.rightp.x))
		{
			if(e.above(temp.rightp))
				temp=temp.lowerright;
			else
				temp=temp.upperright;
			if(temp != null)
				list.add(temp);
		}
		
		return list;
	}
	public void add(Node n) {
	    if(root == null)
	      root = n;
	  }
	
} 