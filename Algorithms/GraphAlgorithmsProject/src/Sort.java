public class Sort {
	
	public static Edge[] QuickSort(Edge[] edgeList)
	{
		return Sort.QuickSort(edgeList, 0, edgeList.length-1);
	}
	
	
	public static Edge[] InsertionSort(Edge[] edgeList){
		int j;
			for(int i=1;i<edgeList.length;i++)
			{
					j=i;				
					while(j>0)
					{
						if(edgeList[j].compareTo(edgeList[j-1])<0)
						{
							swap(edgeList, j, j-1);
						}
						j--;
					}
			}
		return edgeList;
	}

	public static Edge[] CountSort(Edge[] edges) {
		/*
		 * Find Max weight
		 * Max 
		 */
		int R = getMaxEdgeWeight(edges);
		int[] count = fillCount(edges, R);
		Edge[] aux = new Edge[edges.length];
		cumulativeSum(count);
		for(Edge e: edges)
			if(e.src<e.dest) aux[count[e.weight]++] = e;
		return aux;
		// TODO Auto-generated method stub
		
	}

	private static int getMaxEdgeWeight(Edge[] edges)
	{
		int max = edges[0].weight;
		for(Edge e: edges)
				max = e.weight>max ? e.weight : max;
		return max;
	}
	
	private static int[] fillCount(Edge[] edges, int R)
	{
		int[] count = new int[R+2];
		for(Edge e: edges)
				if(e.src<e.dest) count[e.weight+1]++;
		return count;
	}
	
	private static void cumulativeSum(int[] arr)
	{
		for(int i=1;i<arr.length;i++)
			arr[i]+=arr[i-1];
	}
	
	public static Edge[] QuickSort(Edge[] list, int min, int max) {
		if(max>min)
		{
			int pivot = partition(list, min, max);
			QuickSort(list, min, pivot-1);
			QuickSort(list, pivot+1, max);
		}
		return list;
	}
	
	private static int partition(Edge[] list, int lo, int hi) {
		int i=lo,j=hi+1;
		while(true)
		{
			while(list[++i].compareTo(list[lo])<0)
				if(i==hi) break;

			while(list[--j].compareTo(list[lo])>0)
				if(j==lo) break;
			if(i>=j) break;
			swap(list,i,j);
		}
		swap(list,lo,j);
		return j;

	}
	
	private static void swap(Object[] list, int a, int b)
	{
		Object temp;
		temp = list[a];
		list[a] = list[b];
		list[b] = temp;
	}


	public static Edge[] SortPrimEdges(Edge[] edges) {
		int j;
		for(int i=1;i<edges.length;i++)
		{
				j=i;				
				while(j>0)
				{
					if(edges[j].compareToP(edges[j-1])<0)
					{
						swap(edges, j, j-1);
					}
					j--;
				}
		}
		return edges;
	}
	
}
