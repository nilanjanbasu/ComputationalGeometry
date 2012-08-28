import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class Driver {

	private Graph graph;
	private Set<Edge> set = new HashSet<Edge>();	
	Set<Trapezoid> Trapezoids = new HashSet<Trapezoid>();
	Set<Trapezoid> newFaces = new HashSet<Trapezoid>();
	
	public Point queryInMap(Point p)
	{
		Trapezoid t = graph.retrieveNode(p);
		
		int avg_x = (t.rightp.x + t.leftp.x)/2;
		
		int top_l = t.topp.getStartPoint().y ;
		int top_u = t.topp.getEndPoint().y ;
		int bot_l = t.bottomp.getStartPoint().y;
		int bot_u = t.bottomp.getEndPoint().y;
		
		int y1 = top_l < top_u ?top_l:top_u;
		int y2 = bot_u > bot_l ? bot_u:bot_l;
		
		int avg_y = (y1+y2)/2;
		
		return new Point(avg_x,avg_y);		
	}
	
	@SuppressWarnings("unchecked")
	public Driver(Set<Edge> c,int width,int height) 
	{
		Set<Edge> segments = ((Set<Edge>) ((HashSet<Edge>) set).clone());

		Trapezoid nilFace = new Trapezoid(new Point(0,0),new Point(width-1,0),
											new Edge(new Point(0,height-1),new Point(width-1,height-1)),
											new Edge(new Point(0,0),new Point(0,width-1)));
		Trapezoids.add(nilFace);

		graph = new Graph();
		graph.add(nilFace);

		for(Iterator<Edge> tseg = segments.iterator();tseg.hasNext();) {
			Edge seg = tseg.next();
			tseg.remove();

			List<Trapezoid> intersectedFaces = graph.retrieveIntersectedNodes(seg);

			if (intersectedFaces.size() == 1) {
				handleFullyContained(graph,intersectedFaces.get(0), seg);		
			}else{
				handleIntersection(graph,intersectedFaces,seg);
			}

			for (Trapezoid d : newFaces) {
				d.merged = false;
				Trapezoids.add(d);
			}
		}
		/*int i = 0;

		for (Trapezoid f : Trapezoids) {
			f.setIndex(i);
			i++;
		}*/
	}
	
	void handleFullyContained(Graph g,Trapezoid d,Edge e)
	{	
		Point p = e.getStartPoint();
		Point q = e.getEndPoint();
		
		Trapezoids.remove(d);
		Trapezoid A = new Trapezoid(d.leftp, p,d.topp, d.bottomp);
		Trapezoid C = new Trapezoid( p, q, d.topp, e);
		Trapezoid D = new Trapezoid(p, q, e, d.bottomp);
		Trapezoid B = new Trapezoid(q,d.rightp, d.topp, d.bottomp );

		A.setNeighbors(d.upperleft, d.lowerleft, C, D);
		C.setNeighbors(A, A, B, B);
		D.setNeighbors(A, A, B, B);
		B.setNeighbors(C, D, d.upperright, d.lowerright);
		
		if(d.upperleft != null) {
			d.upperleft.upperright = A;
			d.upperleft.lowerright = A;
		}
		if(d.lowerleft != null) {
			d.lowerleft.upperright = A;
			d.lowerleft.lowerright = A;
		}
		
		if(d.upperright != null) {
			d.upperright.upperleft = B;
			d.upperright.lowerleft = B;
		}
		
		if(d.lowerright != null) {
			d.lowerright.upperleft = B;
			d.lowerright.lowerleft = B;
		}

		Trapezoids.add(A);
		Trapezoids.add(B);
		Trapezoids.add(C);
		Trapezoids.add(D);

		XNode subgraph = new XNode(p);
		d.replace(subgraph);

		subgraph.setLeftChild(A);
		subgraph.setRightChild(new XNode(q));
		
		subgraph.rightChild.setRightChild(B);
		subgraph.rightChild.setLeftChild(new YNode(e));

		subgraph.rightChild.leftChild.setLeftChild(C);
		subgraph.rightChild.leftChild.setRightChild(D);

	}
	
	void handleIntersection(Graph g,List<Trapezoid> intersectedFaces, Edge seg)
	{
		int i = 0;

		Trapezoid prevUpper = null;
		Trapezoid prevLower = null;
		Point p = seg.getStartPoint();
		Point q = seg.getEndPoint();

		for (Trapezoid d : intersectedFaces) {
			if (i == 0) {
				Trapezoids.remove(d);

				Trapezoid A = new Trapezoid(d.leftp, p,d.topp, d.bottomp);
				Trapezoid B = new Trapezoid(p,d.rightp, d.topp, seg );
				Trapezoid C = new Trapezoid(p,d.rightp, seg, d.bottomp);

				A.setNeighbors(d.upperleft, d.lowerleft, B, C);
				B.setNeighbors(A, A, null, null);
				C.setNeighbors(A, A, null, null);

				if(d.upperleft!=null) {
					d.upperleft.upperright = A;
					d.upperleft.lowerright = A;
				}
				if(d.lowerleft!=null) {
						d.lowerleft.lowerright = A;
					d.lowerleft.upperright = A;
					
				}
										
				Trapezoids.add(A);
				newFaces.add(B);
				newFaces.add(C);

				prevUpper = B;
				prevLower = C;
				
				XNode subgraph = new XNode(p);
				d.replace(subgraph);

				subgraph.setLeftChild(A);

				subgraph.setRightChild(new YNode(seg));

				subgraph.rightChild.setLeftChild(B);
				subgraph.rightChild.setRightChild(C);
				
				for(Trapezoid e : Trapezoids) {
					if(!Trapezoids.contains(e.upperleft)) e.upperleft=null;
					if(!Trapezoids.contains(e.lowerleft)) e.lowerleft=null;
					if(!Trapezoids.contains(e.upperright)) e.upperright=null;
					if(!Trapezoids.contains(e.lowerright)) e.lowerright=null;
				}

			}
			// Last
			else if (i == intersectedFaces.size() - 1) {
				Trapezoids.remove(d);

				Trapezoid B = new Trapezoid(d.leftp, q, d.topp, seg);
				Trapezoid C = new Trapezoid(d.leftp, q, seg, d.bottomp);
				Trapezoid A = new Trapezoid(q,d.rightp, d.topp, d.bottomp);

				B.setNeighbors(prevUpper, prevUpper, A, A);
				C.setNeighbors(prevLower, prevLower, A, A);
				A.setNeighbors(B, C, d.upperright, d.lowerright);

				prevUpper.upperright = B;
				prevUpper.lowerright = B;
				prevLower.upperright = C;
				prevLower.lowerright = C;
				
				if(d.upperright!=null) {
					d.upperright.upperleft = A;
					d.upperright.lowerleft = A;
				}
				if(d.lowerright!=null) {
					d.lowerright.lowerleft = A;
					d.lowerright.upperleft = A;
				}

				Trapezoids.add(A);
				newFaces.add(B);
				newFaces.add(C);

				XNode subgraph = new XNode(p);
				d.replace(subgraph);

				subgraph.setRightChild(A);
				subgraph.setLeftChild(new YNode(seg));

				subgraph.leftChild.setLeftChild(B);
				subgraph.leftChild.setRightChild(C);
				
				for(Trapezoid e : Trapezoids) {
					if(!Trapezoids.contains(e.upperleft)) e.upperleft=null;
					if(!Trapezoids.contains(e.lowerleft)) e.lowerleft=null;
					if(!Trapezoids.contains(e.upperright)) e.upperright=null;
					if(!Trapezoids.contains(e.lowerright)) e.lowerright=null;
				}
			}
			// Middle
			else {
				Trapezoids.remove(d);

				Trapezoid A = new Trapezoid(d.leftp, d.rightp,d.topp, seg);
				Trapezoid B = new Trapezoid(d.leftp, d.rightp,seg, d.bottomp);

				A.setNeighbors(prevUpper, prevUpper, null, null);
				B.setNeighbors(prevLower, prevLower, null, null);

				prevUpper.upperright = A;
				prevUpper.lowerright = A;
				prevLower.upperright = B;
				prevLower.lowerright = B;
				
				prevUpper = A;
				prevLower = B;

				newFaces.add(A);
				newFaces.add(B);

				YNode subgraph = new YNode(seg);
				d.replace(subgraph);

				subgraph.setLeftChild(A);
				subgraph.setRightChild(B);
				
				for(Trapezoid e : Trapezoids) {
					if(!Trapezoids.contains(e.upperleft)) e.upperleft=null;
					if(!Trapezoids.contains(e.lowerleft)) e.lowerleft=null;
					if(!Trapezoids.contains(e.upperright)) e.upperright=null;
					if(!Trapezoids.contains(e.lowerright)) e.lowerright=null;
				}
				
			}

			i++;
		}

		boolean allMerged = false;

		while (!allMerged) {
			for (Trapezoid d : newFaces) {
				if (d.rightp != null
						&& !d.rightp.equals(p)
						&& !d.rightp.equals(q)
						&& ((d.topp != null && (d.topp.above(d.rightp))) || (d.bottomp != null && (d.bottomp.below(d.rightp))))) {
					Trapezoid next = d.upperright; // Either should work
					d.upperright = next.upperright;
					d.lowerright = next.lowerright;

					if ((d.topp != null && (d.topp.above(d.rightp)))) {
						d.upperright.lowerleft = d;
						
					}
					else {
						d.upperright.upperleft = d;
					}

					d.rightp = next.rightp;

					// Update the node tree as well
					if (next.parent.isLeftChild(next))
						next.parent.setLeftChild(d);
					else
						next.parent.setRightChild(d);

					newFaces.remove(next);
					break;
				} else {
					d.merged = true;
				}
			}
			allMerged = true;
			for (Trapezoid d : newFaces) {
				if (!d.merged)
					allMerged = false;
			}			
		}
	
	}
}


