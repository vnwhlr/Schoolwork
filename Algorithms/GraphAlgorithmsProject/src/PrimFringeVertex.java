
public class PrimFringeVertex extends Edge{	
	public PrimFringeVertex(int src, int dest, int weight)
	{
		super(src, dest, weight);
	}

	@Override
	public int compareTo(Edge other) {
		if(this.weight<other.weight)
			return 1;
		else if(this.weight == other.weight)
			return 0;
		else
			return -1;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other.getClass()!=Edge.class&&other.getClass()!=this.getClass())
			return false;
		else
			return this.dest==((Edge)other).dest;
	}
	
	
}
