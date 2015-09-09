
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Class representing a View that actually resides on the client's machin,
 * transmitting messages from the actual model residing on the server
 * to the actual view.
 * @author Evan Wheeler
 *
 */
public class FifteenViewProxy implements FifteenModelListener{

	FifteenServerMailbox mailbox;
	FifteenViewListener model;
	FifteenSessionManager manager;
	SocketAddress clientAddress;
	Player p;
	
	public FifteenViewProxy(FifteenServerMailbox mailbox, FifteenSessionManager manager)
	{
		this.mailbox = mailbox;
		this.manager = manager;
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#assignID(int)
	 */
	@Override
	public void assignID(int playerID) {
		sendCommand("id " + playerID); 
	}

	/**
	 * Tells the client that a new game has begun.
	 */
	private void newGame() {
		sendCommand("digits 111111111");
		sendCommand("score 1 0");
		sendCommand("score 2 0");
		sendCommand("turn 1");	
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#notifyError(java.lang.Exception)
	 */
	@Override
	public void notifyError(Exception e) {
		sendCommand("");
	}

	/**
	 * Processes a message sent from the ModelProxy residing on the client.
	 * @param Message sent to the server.
	 */
	public boolean process(String message)
	{
		String[] tokens = message.split("\\s+");
		switch(tokens[0])
		{
			case "join":
				int port = Integer.parseInt(tokens[3]);
				clientAddress = new InetSocketAddress(tokens[2], port);
				p = model.join(this, tokens[1]);
				break;
			case "digit":
				int digit = Integer.parseInt(tokens[1]);
				model.chooseDigit(digit, p.getID());
				break;
			case "newgame":
				model.newGame();
				newGame();
				break;
			case "quit":
				model.quitGame();
				return true;
			default:
				throw new RuntimeException("Unexpected message from server: "+message);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#quit()
	 */
	@Override
	public void quit() {
		sendCommand("quit");
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#registerPlayer(interfaces.Player)
	 */
	@Override
	public void registerPlayer(Player player) {
		sendCommand("name " + player.getID() + " " + player.getName()); 

	}

	/**
	 * Tells the SessionManager to remove this viewproxy
	 * from its listing of sessions.
	 * @param fifteenViewProxy
	 */
	public void removeClient() {
		manager.remove(this);
		mailbox.remove(clientAddress);
	}
	
	/**
	 * Helper method, sends command to the server.
	 * @param Command to send
	 */
	private void sendCommand(String message)
	{
		try {
			mailbox.sendMessage(message, clientAddress);
		} catch (IOException e) {
			System.err.println("Failed to send message to server. Details:\n"+e.getMessage());
			System.exit(1);
		} 
	}
	
	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#setAvailableDigits(boolean[])
	 */
	@Override
	public void setAvailableDigits(boolean[] digitsArr) {
		sendCommand("digits " + SharedLib.digitArrToString(digitsArr));
	}
	
	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#setScore(int, int)
	 */
	@Override
	public void setScore(int playerID, int score) {
		sendCommand("score " + playerID + " " + score);
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#setTurn(int)
	 */
	@Override
	public void setTurn(int playerID) {
		sendCommand("turn " + playerID);
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#setViewListener(interfaces.FifteenViewListener)
	 */
	@Override
	public void setViewListener(FifteenViewListener model) {
		this.model = model;		
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenModelListener#setWinner(int)
	 */
	@Override
	public void setWinner(int winnerID) {
		sendCommand("win " + winnerID);
	}


}
