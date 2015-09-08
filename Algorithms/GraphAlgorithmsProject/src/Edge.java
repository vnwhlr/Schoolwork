public class Edge implements Comparable<Edge>
{
	   public int src;   
	   public int dest;
	   public int weight;
	   public Edge(int src, int dest, int weight)
	   {
		   this.src = src;
		   this.dest = dest;
		   this.weight = weight;
	   }
	   
	   public String toString()
	   {
		   return String.format("%d -(%d)-> %d", src, weight, dest);
	   }

	public int compareTo(Edge e)
	{ 
		if(this.weight<e.weight)
			return -1;
		else if(this.weight>e.weight)
			return 1;
		else
		{
			if(this.src<e.src)
				return -1;
			else if(this.src>e.src)
				return 1;
			else
			{
				if(this.dest<e.dest)
					return -1;
				else if(this.dest>e.dest)
					return 1;
				else
					return 0;
			}
		}
	}
	
	/*Used for sorting edges in increasing v-order. TODO: generalize Sort methods with generics, comparators*/
	public int compareToP(Edge e) 
	{
		if(this.dest<e.dest)
			return -1;
		else if(this.dest>e.dest)
			return 1;		
		else
		{

				if(this.src<e.src)
					return -1;
				else if(this.src<e.src)
					return 1;
				else
					{			
					if(this.weight<e.weight)
						return -1;
					else if(this.weight>e.weight)
						return 1;
					else
						return 0;
					}		
		}
	}
}
