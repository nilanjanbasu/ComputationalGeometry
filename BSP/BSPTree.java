import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


class BSPNode{
    public box this_point=null;
    public LinkedList<box> other_points = new LinkedList<box>();
    public BSPNode front = null;
    public BSPNode back = null;
}

class BSPTree
{
	private BSPNode root;
	public int obj_x;
	private GUI parent;
	public final int FRONT=1;
	public final int BACK = 2;
	public final int ON =3;
	public BSPTree(GUI _p,int x)
	{
		root=null;
		obj_x=x;
		parent = _p;
	}
	public int evaluatePoint(int x,int partition)
	{
		if(partition == x)
			return ON;
		else if(partition > x)
			return FRONT;
		else
			return BACK;
	}
	
	public void build(List<box> list)
	{
		if(root == null)
			root = new BSPNode();
		build(root,list);
	}
	private void build(BSPNode tree,List<box> list)
	{
		Iterator<box> i = list.iterator();
		if(i.hasNext())
			tree.this_point = i.next();
		
		tree.other_points.add(tree.this_point);
		
		LinkedList<box> front = new LinkedList<box>();
		LinkedList<box> back  = new LinkedList<box>();
		while(i.hasNext())
		{
			box temp = i.next();
			int result = evaluatePoint(temp.x,tree.this_point.x);
			if(result == ON)
			{
				tree.other_points.add(temp);
			}
			else if(result == BACK)
			{
				back.add(temp);
			}
			else
				front.add(temp);
		}
		if(!front.isEmpty())
		{
			tree.front = new BSPNode();
			build(tree.front,front);
		}
		else
			tree.front=null;
		if(!back.isEmpty())
		{
			tree.back = new BSPNode();
			build(tree.back,back);
		}
		else
			tree.back =null;
	}
	public void renderTree()
	{
		parent.imageClear();
		renderTree(root);
	}
	private void renderTree(BSPNode root)
	{
		
		if(root == null)
			return;
		
		int result = evaluatePoint(obj_x,root.this_point.x);
		if(result == FRONT)
		{
			renderTree(root.back);
			Iterator<box> i = root.other_points.iterator();
			while(i.hasNext())
			{
				box temp = i.next();
				if(evaluatePoint(obj_x, temp.x) == FRONT)
					parent.renderPoint(temp);
			}
			renderTree(root.front);
		}
		else if(result == BACK)
		{
			renderTree(root.front);
			Iterator<box> i = root.other_points.iterator();
			while(i.hasNext())
			{
				box temp = i.next();
				if(evaluatePoint(obj_x, temp.x) == BACK)
					parent.renderPoint(temp);
			}
			renderTree(root.back);
		}
		else
		{
			renderTree(root.front);
			renderTree(root.back);
		}
	}
	
	
}
	
