

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;

/**
 * Class that handles UDP messages to and from the server.
 * @author Evan Wheeler
 *
 */
public class FifteenServerMailbox extends FifteenMessageManager{

	private HashMap<SocketAddress, FifteenViewProxy> viewProxies;
	private FifteenSessionManager sessionManager;
	
	public FifteenServerMailbox(DatagramSocket msgSocket)
	{
		this.incoming = msgSocket;
		sessionManager = new FifteenSessionManager();
		viewProxies = new HashMap<SocketAddress, FifteenViewProxy>(); 
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenMessageManager#processMessage(java.net.SocketAddress, java.lang.String)
	 */
	@Override
	public void processMessage(SocketAddress sender, String message)
	{
		FifteenViewProxy viewp = viewProxies.get(sender);
		if(viewp==null)
		{
			viewp = new FifteenViewProxy(this, sessionManager);
			viewp.setViewListener(sessionManager);			
			viewProxies.put(sender, viewp);
		}
		if(viewp.process(message))
			viewProxies.remove(sender);
	}
	
	/**
	 * Removes a ViewProxy from the mapping, which occurs
	 * when the session is terminated.
	 * @param SocketAddress of client.
	 */
	public void remove(SocketAddress clientAddress) {
		viewProxies.remove(clientAddress);
	}

}


