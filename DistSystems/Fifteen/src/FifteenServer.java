

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Class implementing a server that can run multiple simultaneous sessions
 * of the game.
 * @author Evan Wheeler
 *
 */
public class FifteenServer {
	/**
	 * Launches the server.
	 * @param String array of arguments: <hostname> <port> 
	 */
	 public static void main(String args[])
	 {
		 if (args.length != 2) usage();
		 String host = args[0];
		 int port = Integer.parseInt (args[1]);
		 DatagramSocket msgSocket=null;
		try {
			msgSocket = new DatagramSocket
					 (new InetSocketAddress (host, port));
		} catch (SocketException e1) {
			System.err.println("Failed to boot server: "+e1.getMessage());
			return;
		}
		 FifteenMessageManager mailbox = new FifteenServerMailbox(msgSocket);		 
		 mailbox.start();
	 }
	 
	 /**
	  * Prints an error message when supplied with an incorrect number of arguments.
	  */
		private static void usage() {
			System.err.println("Incorrect number of arguments.");
			System.err.println("Usage: java FifteenServer <host> <port>");
			System.exit(0);
		}
}
