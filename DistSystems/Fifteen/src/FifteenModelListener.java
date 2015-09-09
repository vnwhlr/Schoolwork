



/**
 * Interface specifying the methods of the Fifteen view that 
 * the model can call to update the view.
 * @author Evan Wheeler
 *
 */
public interface FifteenModelListener {
	
	/**
	 * Assigns to the view an ID corresponding to the player associated with it.
	 * @param playerID
	 */
	void assignID(int playerID);

	/**
	 * Notifies the user of an error.
	 * @param Exception representing the error.
	 */
	public void notifyError(Exception e);

	/**
	 * Tells the view to terminate.
	 */
	public void quit();

	/**
	 * Registers the opponent and begins the game.
	 * @param Name of opponent.
	 */
	void registerPlayer(Player player);

	/**
	 * Sets the available digits of the view.
	 * @param Digits, represented as boolean array bit vector
	 */
	public void setAvailableDigits(boolean[] digitsArr);

	/**
	 * Updates the view to display the new score of a player.
	 * @param Boolean value, true if self, false if opponent
	 * @param Score
	 */
	void setScore(int playerID, int score);

	/**
	 * Sets whose turn it is in the view.
	 * @param Boolean value, true if it is the user's turn, false otherwise
	 */
	public void setTurn(int playerID);

	/**
	 * Sets the model that will respond to this view.
	 * @param FifteenViewListener (view)
	 */
	public void setViewListener(FifteenViewListener modelp);
	
	/**
	 * Sets the winner, if there is one.
	 * @param boolean value representing whether winner is the player.
	 * @param boolean value representing whether game is a draw.
	 */
	public void setWinner(int winnerID);

}
