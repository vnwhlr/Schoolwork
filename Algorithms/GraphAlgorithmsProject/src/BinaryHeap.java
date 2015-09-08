import java.util.ArrayList;
import java.util.List;

//minheap
public class BinaryHeap<T extends Comparable<? super T>> {

	List<T> array;
	public BinaryHeap()
	{
		array = new ArrayList<T>();
		array.add(null);
	}
	
	public boolean replaceIfLessThan(T updated)
	{
		int i=1;
		for(;i<array.size();i++)
			if(array.get(i).equals(updated))
				break;
		if(i==array.size())
			return false;
		else
		{			
			if(updated.compareTo(array.get(i))>0)//higher priority than swapped
			{
				array.set(i, updated);
				swim(i);
				return true;
			}
			else
				return false;
		}
	}
	
	private void swim(int index)
	{		
		int k = index;
		while(k>1 && array.get(k/2).compareTo(array.get(k)) < 0)
		{
			boolean leftchild = (k&1)==0; //is right child
			int otherchild = leftchild?k+1:k-1;
			boolean onlyChild = !((leftchild?otherchild:k)<array.size());
			if(!onlyChild && array.get(k).compareTo(array.get(otherchild))==0)//check for equality w/ other child
			{
					if(leftchild) //this is left child; swap with parent
						swap(array, k, k/2);
					else
						swap(array, otherchild, otherchild/2);//swap other child and parent
			}
			else 
				swap(array, k, k/2);
			k /= 2;
		}
	}
	
	public void insert(T data)
	{
		array.add(data);
		swim(array.size()-1);
	}
	
	private void swap(List<T> list, int i, int j)
	{
		T temp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, temp);
	}
	
	private void sink(int index)
	{
		int j,k=index,n=array.size();
		while(2*k<n)
		{
			j=k*2;
			if(j<n-1 && array.get(j).compareTo(array.get(j+1))<0) j++;
			if(array.get(k).compareTo(array.get(j))>=0) break;
			swap(array, k, j);
			k=j;				
		}
	}
	
	public T removeMax()
	{
		swap(array, 1, array.size()-1);
		T max = array.remove(array.size()-1);	
		sink(1);
		return max;
	}
	
}
