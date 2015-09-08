
public class Vertex
{
	   public int id;
	   public Vertex(int id)
	   {
		   this.id = id;
	   }
	   
	   public boolean equals(Vertex v2)
	   {	   
			  return this.id==v2.id;
	   }
	   public String toString()
	   {
		   return new String("ID: "+id);
	   }
	   
}