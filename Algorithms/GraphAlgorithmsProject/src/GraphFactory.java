import java.util.Random;

public class GraphFactory {

	/**
	 * @param args
	 */
	public static GraphMatrix createGraphMatrix(int n, double p, long seed)
	{
		Random edgeRand = new Random(seed);
		Random weightRand = new Random(2*seed);
		GraphMatrix matrixG;
		double prob;
		do
		{	
			matrixG = new GraphMatrix(n, false);
			for (int i = 0; i < n; i++) 
			{	
				   for (int j = i+1; j < n; j++) 
				   {	
				      prob = edgeRand.nextDouble();	
				      if (prob <= p) 
				      {
				    	  int range = n-1+1;
				    	  int weight = weightRand.nextInt(range)+1;
				    	  matrixG.addEdge(i,j,weight);
				      }
				   }
				}
		}while(!matrixG.isConnected());
		return matrixG;
	}
	
	public static GraphList createGraphList(int n, double p, long seed)
	{
		Random edgeRand = new Random(seed);
		Random weightRand = new Random(2*seed);
		GraphList listG;
		double prob;
		do
		{	
			listG = new GraphList(n, false);
			for (int i = 0; i < n; i++) 
			{	
				   for (int j = i+1; j < n; j++) 
				   {	
				      prob = edgeRand.nextDouble();	
				      if (prob <= p) 
				      {
				    	  int range = n-1+1;
				    	  int weight = weightRand.nextInt(range)+1;
				    	  listG.addEdge(i,j,weight);
				      }
				   }
				}
		}while(!listG.isConnected());
		return listG;
	}
	
}
