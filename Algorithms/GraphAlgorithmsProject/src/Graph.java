public interface Graph {

	public boolean addEdge(int src, int dest, int weight);
	public boolean isConnected();
	public int[] DFS(int initialVertex);
	public void print();
	public Graph MSTKruskal(SortingMethod sort);
	public Graph MSTPrim();
	public int vertexCount();
	public int edgeCount();
	public Edge[] edges();

}
