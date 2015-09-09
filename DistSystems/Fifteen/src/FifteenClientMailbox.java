

import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Class responsible for sending and recieving messages on the client side.
 * @author Evan Wheeler
 *
 */
public class FifteenClientMailbox extends FifteenMessageManager {

	FifteenModelProxy modelp;
	
	
	/**
	 * 
	 * @param incoming DatagramSocket that recieves messages.
	 */
	public FifteenClientMailbox(DatagramSocket incoming)
	{
		this.incoming = incoming;
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenMessageManager#processMessage(java.net.SocketAddress, java.lang.String)
	 */
	@Override
	protected void processMessage(SocketAddress sender, String message)
	{
		String[] tokens = message.split("\\s+");
		switch(tokens[0])
		{
			case "id":
				int id = Integer.parseInt(tokens[1]);
				modelp.setID(id);
				break;
			case "name":
				id = Integer.parseInt(tokens[1]);
				modelp.registerPlayer(new Player(tokens[2], id));
				break;
			case "digits":
				modelp.setDigits(tokens[1]);
				break;
			case "score":
				id = Integer.parseInt(tokens[1]);
				int score = Integer.parseInt(tokens[2]);
				modelp.setScore(id, score);
				break;
			case "turn":
				int turnID = Integer.parseInt(tokens[1]);
				modelp.setTurn(turnID);
				break;
			case "win":
				int winnerID = Integer.parseInt(tokens[1]);
				modelp.win(winnerID);
				break;
			case "quit": 
				modelp.quit();
				break;
			default:
				throw new RuntimeException("Unexpected message from server: "+message);
		}
	}

	/**
	 * Sets the modelproxy that will respond to the messages.
	 * @param ModelProxy object to register with the mailbox.
	 */
	public void setListener(FifteenModelProxy modelp2) {
		modelp=modelp2;
	}

}
