
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


/**
 * Main class that validates arguments, attempts to bind ;to sockets
 * and creates a model and a view that will 
 * respond to each other.
 * @author Evan Wheeler
 *
 */
public class Fifteen {
	public static void main(String args[])
    {
        if(args.length!=5) 
        	usage();
        String name = args[0];
        String chost = args[1];
        int cport = Integer.parseInt(args[2]);
        String shost = args[3];
        int sport = Integer.parseInt(args[4]);
        SocketAddress server = null;
        DatagramSocket inbox = null;
        //name chost cport shost sport
        try {
            inbox = new DatagramSocket(new InetSocketAddress (chost, cport));
        	server = new InetSocketAddress (shost, sport);
        } catch (IOException e) {
		    System.err.println("Unable to establish connection.");
		    System.exit(0);
		}
        
        FifteenClientMailbox mailbox = new FifteenClientMailbox(inbox);
        FifteenView view = new FifteenView(name);
        FifteenModelProxy modelp = new FifteenModelProxy(mailbox, server);
        mailbox.setListener(modelp);
        view.setViewListener(modelp);
        mailbox.start();
        modelp.join(view, name);       	
	}
	
	/**
	 * Prints an error message when given an incorrect number of arguments.
	 */
	private static void usage() {
		System.err.println("Incorrect number of arguments.");
		System.err.println("Usage: java Fifteen <myname> <clienthost> <clientport> <serverhost> <serverport>");
		System.exit(0);
	}
}
