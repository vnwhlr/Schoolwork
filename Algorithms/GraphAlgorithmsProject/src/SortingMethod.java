
public enum SortingMethod {
INSERTION_SORT,
COUNT_SORT,
QUICK_SORT;

@Override
public String toString() {
	  switch(this) {
	    case INSERTION_SORT: return "INSERTION SORT";
	    case COUNT_SORT: return "COUNT SORT";
	    case QUICK_SORT: return "QUICKSORT";
	    default: throw new IllegalArgumentException();
	  }
	}
}