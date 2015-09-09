


/**
 * Interface specifying the methods a view can invoke to
 * update a model.
 * @author Evan Wheeler
 *
 */
public interface FifteenViewListener {
	/**
	 * Informs the model of the player's selection of a digit.
	 * @param Digit to choose
	 */
	public void chooseDigit(int digit, int player);
	
	/**
	 * Initiates a game or game session.
	 * @param tokens 
	 * @param View.
	 */	
	public Player join(FifteenModelListener view, String name);
	
	/**
	 * Tells the model to reset the game.
	 */
	public void newGame();
	
	/**
	 * Tell the model to quit the game.
	 */
	public void quitGame();
}
