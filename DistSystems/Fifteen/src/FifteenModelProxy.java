
import java.io.IOException;
import java.net.SocketAddress;
/**
 * FifteenModelProxy mimics a model residing locally, but in actuality is on a remote server.
 * @author Evan Wheeler
 *
 */
public class FifteenModelProxy implements FifteenViewListener{
	FifteenModelListener view;
	int myID;
	SocketAddress server;
	FifteenClientMailbox mailbox;
	
	/**
	 * Constructor takes the MessageManager responsible for sending datagrams
	 * and the address of the server.
	 * @param mailbox
	 * @param server
	 */
	public FifteenModelProxy(FifteenClientMailbox mailbox, SocketAddress server)
	{
		this.mailbox=mailbox;
		this.server = server;
	}

	/* (non-Javadoc)
	 * @see FifteenViewListener#chooseDigit(int)
	 */
	@Override
	public void chooseDigit(int digit, int id) 
	{
			sendMessage("digit " + digit);		
	}
	
	
	/**
	 * Joins the game by sending a message to the server.
	 */
	@Override
	public Player join(FifteenModelListener view, String name) 
	{
		this.view = view;
		sendMessage(String.format("join %s %s %d", name, mailbox.getInboxHost(), mailbox.getInboxPort()));
		return null;
	}

	/* (non-Javadoc)
	 * @see FifteenViewListener#newGame()
	 */
	@Override
	public void newGame() 
	{
		sendMessage("newgame");		
	}

	/* (non-Javadoc)
	 * @see FifteenViewListener#quit()
	 */
	void quit()
	{
		mailbox.stop();
		view.quit();
	}
	
	/* (non-Javadoc)
	 * @see FifteenViewListener#sendQuit()
	 */
	@Override
	public void quitGame() 
	{
		sendMessage("quit");
	}
	
	/**
	 * Registers the player, and starts the game if the player is
	 * the opponent.
	 * @param Player object representing the player.
	 */
	void registerPlayer(Player p) {
			view.registerPlayer(p);
	}

	/**
	 * Helper method that sends a message using the mailbox.
	 * @param msg String to send
	 */
	private void sendMessage(String msg)
	{
		try {
			mailbox.sendMessage(msg, server);
		} catch (IOException e) {
			System.err.println("Error occured when attempting to send message: "+e.getMessage());
			mailbox.stop();
			System.exit(1);
		}
	}



	/**
	 * Processes a "digits" message and 
	 * sets the available digits on the view.
	 * @param Digits, as a bit vector string
	 */
	void setDigits(String digits)
	{
		boolean[] digitsArr = SharedLib.digitStringToArr(digits);
		view.setAvailableDigits(digitsArr);
	}



	/**
	 * Sets the ID assigned to this instance.
	 * @param id
	 */
	void setID(int id) {
		this.myID = id;
		view.assignID(id);
	}



	/**
	 * Sets the score of the player with the given ID.
	 * @param ID of player
	 * @param Score
	 */
	void setScore(int id, int score) {
		view.setScore(id, score);
	}

	/**
	 * Determines whether it is the player's turn.
	 * @param ID of player whose turn it is.
	 */
	void setTurn(int turnID) {
		view.setTurn(turnID);
	}

	
	/**
	 * Sets the winner.
	 * @param ID of winner
	 */
	void win(int winnerID) {
		view.setWinner(winnerID);
	}
	
}
