
public class PrimFringe {

	BinaryHeap<PrimFringeVertex> heap;
	
	public PrimFringe()
	{
		heap = new BinaryHeap<PrimFringeVertex>();
	}
	
	public void offer(PrimFringeVertex data)
	{
		heap.insert(data);
	}
	
	public PrimFringeVertex pluck()
	{
		return heap.removeMax();
	}

	public void updateFringeIfLessThan(PrimFringeVertex edge) {
		heap.replaceIfLessThan(edge);
	}
}
