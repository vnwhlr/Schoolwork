


/**
 * Class with shared convenience methods.
 * @author Evan Wheeler
 *
 */
public class SharedLib {
	/**
	 * Takes a bit vector represented by a boolean array and turns it into a string.
	 * @param boolean[] array to convert.
	 * @return String representation of bit vector.
	 */
	public static String digitArrToString(boolean[] digitArr)
	{
		StringBuilder sb = new StringBuilder();
		for(boolean digit:digitArr)
			sb.append(digit?"1":"0");
		return sb.toString();
	}

	/**
	 * Helper method that turns the bit vector representing available
	 * digits into a boolean array.
	 * @param bit vector as string
	 * @return bit vector as boolean array
	 */
	public static boolean[] digitStringToArr(String digitstr)
	{
		boolean[] digitArr = new boolean[9];
		for (int i = 0; i < digitstr.length(); i++) 
			  digitArr[i] = digitstr.charAt(i)=='1'?true:false;
		return digitArr;
	}	
}
