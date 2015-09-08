import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class GraphList implements Graph {

   /**
    * @param args
    */
   private HashMap<Integer, ArrayList<Edge>> adjList;
   private int size;
   private int edgeCount;
   private boolean directed;
   
   public GraphList(int size, boolean directed){
       adjList = new HashMap<Integer, ArrayList<Edge>>((int) (size*1.5));
       for(int i=0;i<size;i++)
       {
    	   adjList.put(i, new ArrayList<Edge>());
       }
       this.size=size;
       this.directed = directed;
   }
   
   public boolean addEdge(int src, int dest, int weight)
   {
	    adjList.get(src).add(new Edge(src, dest, weight));
	    if(!directed)
	    	adjList.get(dest).add(new Edge(dest, src, weight));
	    edgeCount++;
		return true;
   }
    
   public boolean isConnected()
   {
	   Stack<Integer> visitStack = new Stack<Integer>();
	   boolean[] visited = new boolean[size];
	   int numVisited=0;
	   for(int i=0;i<size;i++)
	   {
		   visited[i] = false;
	   }
	   int v = 0;
	   visitStack.push(v);
	   while(!visitStack.isEmpty())
	   {
		   visited[v] = true;
		   for(Edge e: adjList.get(v))
		   {
			   visitStack.push(e.dest);
		   }
		   do{
			   v = visitStack.pop();
		   }
		   while(visited[v]&&!visitStack.isEmpty());
		   numVisited++;
	   }
	   return numVisited==size;
		   
   }
   
   public int[] DFS(int initialVertex)
   {
	   Stack<Integer> visitStack = new Stack<Integer>();
	   VertexState[] vertexStates = new VertexState[size];
	   int[] predecessor = new int[size];
	   for(int i=0;i<size;i++) //mark vertices as undiscovered
	   {
		   vertexStates[i] = VertexState.UNDISCOVERED;
	   }
	   predecessor[initialVertex] = -1; 
	   int v = initialVertex;
	   int next = 0;
	   visitStack.push(v);
	   while(!visitStack.isEmpty()) //while there are vertices in the current stack
	   {
			   vertexStates[v] = VertexState.VISITING; 
			   boolean found = false;
			   next = size+1; //prevents algorithm from getting stuck in loop when picking next node to visit
			   for(Edge e: adjList.get(v)) //go through each adjacent edge and 
			   {
				   switch(vertexStates[e.dest])
				   {
					   case UNDISCOVERED:
						   vertexStates[e.dest] = VertexState.DISCOVERED;
					   case DISCOVERED:
						   found = true;
						   next = e.dest<next?e.dest:next; //picks unvisited node with minimum id
					   default: //don't revisit any visited/visiting nodes
						   break;
				   }

			   }
			   if(found) //node has adjacent unvisited nodes
			   {
				   visitStack.push(v); //push v back on stack
				   visitStack.push(next);
				   predecessor[next] = v;
			   }
			   else
			   {
				   vertexStates[v] = VertexState.VISITED;
			   }
			   v = visitStack.pop(); //visit adjacent node, or backtrack  
		}
	   return predecessor;
	   }
   
   @Override
public void print() {
	for(Integer i: adjList.keySet())
	{
		System.out.format("%d-> ",i);
		for(Edge e: adjList.get(i))
		{
			System.out.format("%d(%d) ", e.dest, e.weight);
		}
		System.out.println();
	}
	System.out.println();
}

	
	@Override
	public Graph MSTKruskal(SortingMethod sort) {
		//sort edges
		int edgeCount = 0;
		int edgeIndex = 0;
		Edge[] edges;
		switch(sort)
		{
			case INSERTION_SORT: edges = Sort.InsertionSort(edges()); break;
			case COUNT_SORT: edges = Sort.CountSort(edges()); break;
			case QUICK_SORT: edges = Sort.QuickSort(edges()); break;
			default: throw new IllegalArgumentException("Invalid sort method.");
		}
		int[] p = new int[size];
		int[] rank = new int[size];
		for(int i=0;i<size;i++)
		{
			p[i] = i;
			rank[i]=0;
		}
		GraphList mst = new GraphList(size, false);
		while(edgeCount < size-1)
		{
			Edge e = edges[edgeIndex++];
			if(!sameSubtree(p, e.src, e.dest))
			{		
				mst.addEdge(e.src, e.dest, e.weight);
				edgeCount++;
				union(p, rank, e.src, e.dest);				
			}				
		}
		
		//add as long as no cycle
		return mst;
	}
	
	private boolean sameSubtree(int[] partition, int t1, int t2)
	{
		return find(partition, t1) == find(partition, t2);
	}
	
	
	private int find(int[] partition, int vrtx)
	{
		if(partition[vrtx]!=vrtx)
		{
			partition[vrtx] = find(partition, partition[vrtx]);
			return partition[vrtx];
		}
		return vrtx;
	}
	
	private void union(int[] partition, int[] rank, int t1, int t2)
	{
		if(rank[t1]<rank[t2])
			partition[partition[t1]] = partition[t2];
		else 
		{
			partition[partition[t2]] = partition[t1];
			if(rank[t2]==rank[t1]) 
				 rank[t1]++;
		}
				
	}
	
	@Override
	public Graph MSTPrim() {
		int i;
		Graph mst = new GraphMatrix(this.size, this.directed);
		PrimFringe pq = new PrimFringe();
		int chosenVertex = 0;
		for(i=1;i<size;i++)
		{
			pq.offer(new PrimFringeVertex(0, i, Integer.MAX_VALUE));
		}
		for(i=0;i<size-1;i++)
		{
			for(Edge e : this.adjList.get(chosenVertex))
			{
				pq.updateFringeIfLessThan(new PrimFringeVertex(e.src, e.dest, e.weight)); //TODO: can optimize
			}
			PrimFringeVertex p = pq.pluck();
			chosenVertex = p.dest;
			mst.addEdge(p.src, p.dest, p.weight);		
		}
		return mst;	
	}

	@Override
	public Edge[] edges() {
		Edge[] edges = new Edge[edgeCount];
		int index=0;
		for(ArrayList<Edge> elist: adjList.values())
		{
			for(Edge e:elist)
			{
				if(directed||e.src<e.dest)
					edges[index++] = e;
			}
		}
		return edges;
	}

	@Override
	public int vertexCount() {
		return size;
	}

	@Override
	public int edgeCount() {
		return edgeCount;
	}
}