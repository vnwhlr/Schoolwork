
/**
 * Class representing a player, with their associated name, id and score.
 * @author Evan Wheeler
 */
public class Player
{
	private String name;
	private int id;
	private int score;

	public Player(String name, int id)
	{
		this.id=id;
		this.name = name;
		this.score=0;
	}

	/**
	 * Adds to this player's current score.
	 * @param digit
	 * @return The new score.
	 */
	public int addToScore(int digit)
	{
		this.score += digit;
		return score;
	}
	
	@Override
	public boolean equals(Object arg0)
	{
		if(arg0.getClass()!=this.getClass())
			return false;
		else
		{
			Player other = (Player) arg0;
			return this.id==other.id&&this.name==other.name;
		}
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getScore()
	{
		return this.score;
	}
	
	/**
	 * Resets the score of the player to 0.
	 */
	public void resetScore()
	{
		this.score=0;
	}

	public void setID(int id)
	{
		this.id=id;
	}
	
	public void setName(String myName) {
		name = myName;
	}
	
}
	
