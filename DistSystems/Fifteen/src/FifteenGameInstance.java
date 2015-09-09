

/**
 * Class representing the actual game instance. Responsible for 
 * storing information about the players, taking turns and the 
 * the available digits.
 * @author Evan Wheeler
 *
 */
public class FifteenGameInstance {
	private boolean[] digits;
	private Player player1;
	private Player player2;
	private int turnID;
	private boolean draw;
	
	public FifteenGameInstance()
	{
		draw=false;
		turnID=1;
		digits = new boolean[9];
	}

	/**
	 * Creates a player and adds them to the game.
	 * @param Name of the player to create.
	 * @return The player created.
	 */
	public Player addPlayer(String name)
	{
		if(player1==null){
			player1 = new Player(name, 1);
			return player1;
		}
		else if(player2==null){
			player2 = new Player(name, 2);
			return player2;
		}
		else{
			System.err.print("Unexpected error: player ID invalid.");
			return null;
			}
	}
	
	/**
	 * Helper method that checks whether all the digits have been chosen.
	 * @return True if all digits have been chosen (are false), true otherwise
	 */
	private boolean allDigitsChosen()
	{
		for(boolean digit:digits)
		{
			if(digit)
				return false;
		}
		return true;
	}
	
	/**
	 * Selects a digit, adds it to the player's score
	 * and checks whether the game is won or is a draw.
	 * @param int Digits to select
	 * @param int ID of player
	 * @return Updated Player information.
	 */
	public Player chooseDigit(int digit, int id)
	{		
		Player player = null;
		if(player1.getID()==id)
		{
			player = player1;
		}
		else if(player2.getID()==id)
		{
			player = player2;
		}
		else{/*TODO: error*/}
		player.addToScore(digit);
		digits[digit-1]=false;
		if(allDigitsChosen())
		{
			turnID=0;
			draw=true;
		}
		else
			turnID = turnID==1?2:1;		
		return player;
	}
	
	/**
	 * Accessor methods.
	 */
	public boolean[] getDigits() {
		return digits;
	}
	
	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public int getTurnID() {
		return turnID;
	}

	/**
	 * Returns whether the game has ended in a draw.
	 * @return True if a draw, false otherwise
	 */
	public boolean isDraw()
	{
		return draw;
	}

	/**
	 * Resets the game by resetting the digits, turn and scores.
	 */
	public void newGame()
	{
		draw=false;
		for(int i=0;i<digits.length;i++)
		{
			digits[i]=true;
		}
		player1.resetScore();
		player2.resetScore();
		turnID=1;
	}
	
}
