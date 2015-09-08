import java.util.Stack;

public class GraphMatrix implements Graph{

	private int size;
	private int[][] adjMatrix;
	private int edgeCount;
	private boolean directed;
	
	public GraphMatrix(int n, boolean directed)
	{
		size = n;
		adjMatrix= new int[n][n];
		edgeCount=0;
		this.directed=directed;
	}
	
	public boolean addEdge(int srcVertex, int destVertex, int weight)
	{
		if(adjMatrix[srcVertex][destVertex]==0)
			edgeCount++;
		adjMatrix[srcVertex][destVertex]=weight;
		if(!directed)
			adjMatrix[destVertex][srcVertex]=weight;
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
			   for(int i=0;i<size;i++) 
			   {
				   if(adjMatrix[v][i]>0)
					   visitStack.push(i);
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
				   for(int i=0;i<size;i++) //go through each adjacent edge and 
				   {
					   if(adjMatrix[v][i]>0)
					   {
						   switch(vertexStates[i])
						   {
							   case UNDISCOVERED:
								   vertexStates[i] = VertexState.DISCOVERED;
							   case DISCOVERED:
								   found = true;
								   next = i<next?i:next; //picks unvisited node with minimum id
							   default: //don't revisit any visited/visiting nodes
								   break;
						   }
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
	   
	
	public int[] getAdjacentEdges(int srcVertex)
	{
		return adjMatrix[srcVertex];
	}
	
	
	
	@Override
	public void print()
	{
		int i,j;
		for(i=0;i<size;i++)
		{
			for(j=0;j<size;j++)
			{
				System.out.printf(" %-3d",adjMatrix[i][j]);
			}
		System.out.println("\n");
		}
	}

	public Edge[] edges()
	{
		Edge[] eList = new Edge[edgeCount]; 
		int k=0;
		for(int i=0;i<size;i++)
			for(int j=i+1;j<size;j++)
				if(adjMatrix[i][j]>0)
					eList[k++]=new Edge(i,j,adjMatrix[i][j]);
		return eList;
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
			default: /*print error, return exception*/ return null;
		}
		
		int[] p = new int[size];
		int[] rank = new int[size];
		for(int i=0;i<size;i++)
		{
			p[i] = i;
			rank[i]=1;
		}
		GraphMatrix mst = new GraphMatrix(size, false);
		while(edgeCount < size-1)
		{
			Edge e = edges[edgeIndex++];
			if(!sameSubtree(p, e.src, e.dest))
			{
				union(p, rank, e.src, e.dest);
				mst.addEdge(e.src, e.dest, e.weight);
				edgeCount++;
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
			if(rank[t2]==rank[t1]) rank[t2]++;
		}	
	}
	
	
	@Override
	public Graph MSTPrim() {
		int i,j;
		Graph mst = new GraphMatrix(this.size, this.directed);
		PrimFringe pq = new PrimFringe();
		int chosenVertex = 0;
		for(i=1;i<size;i++)
		{
			pq.offer(new PrimFringeVertex(0, i, Integer.MAX_VALUE));
		}
		for(i=0;i<size-1;i++)
		{
			for(j=0;j<size;j++)
				if(adjMatrix[chosenVertex][j]>0) //TODO: optimize?
					pq.updateFringeIfLessThan(new PrimFringeVertex(chosenVertex, j, adjMatrix[chosenVertex][j]));
			PrimFringeVertex p = pq.pluck();
			chosenVertex = p.dest;
			mst.addEdge(p.src, p.dest, p.weight);		
		}
		return mst;	
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

