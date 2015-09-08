import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class ExperimentData {
	public static final int[] N = {8, 16, 32, 64, 128, 256, 512}; 
	public static final int NUM_TESTS = N.length;
	public static final int NUM_MST_IMPLEMENTATIONS = 4;
	public static final int NUM_GRAPH_IMPLEMENTATIONS = 2;
	public static final int NUM_KRUSKAL_SORTS = 3;
	
	
	long[][] graphCreationTimes; 
	long[][] mstPrimTime;
	long[][][] mstKruskalTime;
	int seed;
	double p;
	
	public ExperimentData()
	{
		this(new long[NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS], new long[NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS], new long[NUM_KRUSKAL_SORTS][NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS], 0, 0);
	}
	
	enum GraphType{GRAPH_MATRIX, GRAPH_LIST};
	
	
	public ExperimentData(long[][] graphCreationTimes, long[][] mstPrimTime,
			long[][][] mstKruskalTime, int seed, double p) {
		this.graphCreationTimes = graphCreationTimes; 
		this.mstPrimTime = mstPrimTime; 
		this.mstKruskalTime = mstKruskalTime; 
		this.seed = seed; 
		this.p = p;
	}
	
	public static ExperimentData takeAverage(List<ExperimentData> dataset, int seed, double p)
	{
		ExperimentData avg = new ExperimentData();
		avg.seed = seed;
		avg.p = p;
		for(ExperimentData run:dataset)
		{
			for(int i=0;i<NUM_GRAPH_IMPLEMENTATIONS;i++)			
				for(int j=0;j<NUM_TESTS;j++)
				{		
					avg.graphCreationTimes[i][j] += run.graphCreationTimes[i][j];
					avg.mstPrimTime[i][j] += run.mstPrimTime[i][j];
					for(int k=0;k<NUM_KRUSKAL_SORTS;k++)
					{
						avg.mstKruskalTime[k][i][j] += run.mstKruskalTime[k][i][j];
					}
				}
		}
		for(int i=0;i<NUM_GRAPH_IMPLEMENTATIONS;i++)			
			for(int j=0;j<NUM_TESTS;j++)
			{		
				avg.graphCreationTimes[i][j] /= dataset.size();
				avg.mstPrimTime[i][j] /= dataset.size();
				for(int k=0;k<NUM_KRUSKAL_SORTS;k++)
				{
					avg.mstKruskalTime[k][i][j] /= dataset.size();
				}
			}
		return avg;
	}


	public static ExperimentData runExperiment(int seed, double p) {
		
		long[][] graphCreationTimes = new long[NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS];
		Graph[][] graphs = new Graph[NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS];
		long[][] mstPrimTime = new long [NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS];
		long[][][] mstKruskalTime = new long [NUM_KRUSKAL_SORTS][NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS];
		long time;
		for(int i=0; i<NUM_TESTS; i++)
		{
			time = -System.nanoTime();
			graphs[GraphType.GRAPH_MATRIX.ordinal()][i] = GraphFactory.createGraphMatrix(N[i], p, seed);
			time += System.nanoTime();
			graphCreationTimes[GraphType.GRAPH_MATRIX.ordinal()][i] = time;
			time = -System.nanoTime();
			graphs[GraphType.GRAPH_LIST.ordinal()][i] =  GraphFactory.createGraphList(N[i], p, seed);
			time += System.nanoTime();
			graphCreationTimes[GraphType.GRAPH_LIST.ordinal()][i] = time;
			for(GraphType gtype:GraphType.values())
			{
				Graph g = graphs[gtype.ordinal()][i];
				time = -System.nanoTime();
				g.MSTPrim();
				time += System.nanoTime();
				mstPrimTime[gtype.ordinal()][i] = time;
				for(SortingMethod sm : SortingMethod.values())
				{
					time = -System.nanoTime();
					g.MSTKruskal(sm);
					time += System.nanoTime();
					mstKruskalTime[sm.ordinal()][gtype.ordinal()][i] = time;
				}
			}
		}
		return new ExperimentData(graphCreationTimes, mstPrimTime, mstKruskalTime, seed, p);
	}
	
	//p*algoimps*graphimps = 3x4x2 GRAPHS; this does 8, for a fixed p
	public void exportNvsTime()
	{
		String[] gimplementations = {"list", "matrix"};
		String[] algos = {"Prim","KInsertion","KCount","KQuick"};
		String header = "n,time";
		BufferedWriter fileWriter;
		try {	
			for(int i=0;i<algos.length;i++)
			{
				for(int j=0;j<NUM_GRAPH_IMPLEMENTATIONS;j++)
				{
					String fileName = "MST-"+algos[i]+"-"+gimplementations[j]+"-p"+(int)(p*100)+".csv";
					fileWriter = new BufferedWriter(new FileWriter(new File(fileName)));
					fileWriter.write(header+"\n");
					if(i<1)
					{
						for(int k=0;k<NUM_TESTS;k++)
							fileWriter.write(N[k] + ","+ mstPrimTime[j][k] + "\n");
					}
					else
					{
						for(int k=0;k<NUM_TESTS;k++)
							fileWriter.write(N[k] + ","+ mstKruskalTime[i-1][j][k] + "\n");
					}
					fileWriter.close();
					//close file
				}
			}
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	
	public double[] calculateConstants()
	{
		double[][] constantsNPrim = new double[NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS];
		double[][][] constantsNKruskal = new double[NUM_KRUSKAL_SORTS][NUM_GRAPH_IMPLEMENTATIONS][NUM_TESTS];
		for(int i=0;i<NUM_GRAPH_IMPLEMENTATIONS;i++)
		{
			for(int j=0;j<NUM_TESTS;j++)
			{
				
					int v = N[j];
					int e = v*(v+1)/2;
					double primTheoreticalRuntime = (long) ((e*p) * Math.log(v));
					double kruskalTheoreticalRuntime = (long) ((e*p) * Math.log(e*p) + (e*p) * Math.log(v));
					double kruskalInsTheoreticalRuntime = (long) ((e*p)*(e*p) + (e*p) * Math.log(v));
					constantsNKruskal[0][i][j] = mstKruskalTime[0][i][j]/kruskalInsTheoreticalRuntime;
					for(int k=1;k<NUM_KRUSKAL_SORTS;k++)
					{
						constantsNKruskal[k][i][j] = mstKruskalTime[k][i][j]/kruskalTheoreticalRuntime;
					}
					constantsNPrim[i][j] = mstPrimTime[i][j]/primTheoreticalRuntime;
			}
		}
		

		
		double[] avgPrimConstantPerRepresentation = avg(constantsNPrim, NUM_GRAPH_IMPLEMENTATIONS);
		double[][] avgKruskalConstantPerRepresentation = avg(constantsNKruskal,NUM_KRUSKAL_SORTS, NUM_GRAPH_IMPLEMENTATIONS);
		
		double totalPrimConstant = avg(avgPrimConstantPerRepresentation);
		double [] totalKruskalConstant = avg(avgKruskalConstantPerRepresentation,NUM_KRUSKAL_SORTS);

		double[] constants = {totalPrimConstant, totalKruskalConstant[0], totalKruskalConstant[1], totalKruskalConstant[2]};
 
		
		for(int i=0;i<2;i++)
		{
			System.out.printf("%s",i==0?"GRAPH MATRIX CONSTANTS":"GRAPH LIST CONSTANTS:\n");
			for(int k=0;k<3;k++)
			{
				System.out.printf("\t%s\n",k==0?"K-INSERTION":(k==1?"K-COUNT":"K-QUICK"));
				for(int j=0;j<NUM_TESTS;j++)
					System.out.printf("\t\t%d:%f\n",N[j],constantsNKruskal[k][i][j]);
			}
			System.out.printf("\tPRIM\n");
			for(int j=0;j<NUM_TESTS;j++)
					System.out.printf("\t\t%d:%f\n",N[j],constantsNPrim[i][j]);
		}
	
		System.out.printf("p = %f;"
				+ "AVG CONSTANT FOR PRIM: %f (ns/computation)\n"
				+ "AVG CONSTANT FOR KRUSKAL-INSERTION: %f (ns/computation)\n"
				+ "AVG CONSTANT FOR KRUSKAL-COUNT: %f (ns/computation)\n"
				+ "AVG CONSTANT FOR KRUSKAL-QUICK: %f (ns/computation)\n"
				, p, totalPrimConstant, totalKruskalConstant[0],totalKruskalConstant[1],totalKruskalConstant[2]);
		return constants;
	}
	
	
	
	double[][] avg(double[][][] arr, int a, int b)
	{
		double[][] avg = new double[a][b];
		for(int i=0;i<arr.length;i++)
		{
			avg[i] = avg(arr[i], b);
		}
		return avg;
	}
	
	double[] avg(double[][] arr, int b)
	{
		double[] avg = new double[b];
		for(int i=0;i<b;i++)
		{
			avg[i] = avg(arr[i]);
		}
		return avg;
	}
	
	double avg(double[] arr)
	{
		double avg=0;
		for(int i=0;i<arr.length;i++)
		{
			avg+=arr[i];
		}
		return avg/arr.length;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
	
		String[] gimplementations = {"list", "matrix"};
		String[] algos = {"Prim","KInsertion","KCount","KQuick"};
			
			for(int i=0;i<algos.length;i++)
			{
				for(int j=0;j<NUM_GRAPH_IMPLEMENTATIONS;j++)
				{
					sb.append("MST-"+algos[i]+"-"+gimplementations[j]+"-p"+p+"\n");
					sb.append("n,time\n");
					if(i<1)
					{
						for(int k=0;k<NUM_TESTS;k++)
							sb.append(N[k] + ","+ mstPrimTime[j][k] + "\n");
					}
					else
					{
						for(int k=0;k<NUM_TESTS;k++)
							sb.append(N[k] + ","+ mstKruskalTime[i-1][j][k] + "\n");
					}
					sb.append("\n\n");
					//close file
				}
			}
			return sb.toString();
	}
	
}
