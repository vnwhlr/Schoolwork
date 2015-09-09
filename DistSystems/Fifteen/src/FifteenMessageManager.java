

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Abstract class responsible for recieving and sending UDP datagrams.
 * @author Evan Wheeler
 *
 */
public abstract class FifteenMessageManager{


	protected DatagramSocket incoming;
	Thread listenerThread;
	boolean running;
	private byte[] payload;
	
	/**
	 * Helper method extracts the UTF message data from a datagram.
	 * @param DatagramPacket 
	 */
	private String extractMessage(DatagramPacket dp) throws IOException
	{
	DataInputStream in = new DataInputStream
			 (new ByteArrayInputStream
			 (dp.getData(), 0, dp.getLength()));
			String input = in.readUTF();
			return input;
	}
		
	/**
	 * Gets the hostname of the socket.
	 * @return host
	 */
	public String getInboxHost()
	{
		return incoming.getLocalAddress().getHostName();
	}
	
	/**
	 * Gets the port of the socket.
	 * @return port number
	 */
	public int getInboxPort()
	{
		return incoming.getLocalPort();
	}

	/**
	 * Abstract method handles a recieved message.
	 * @param SocketAddress The message's sender.
	 * @param String The message content (encoded in UTF)
	 */
	protected abstract void processMessage(SocketAddress sender, String message);
	
	/**
	 * Blocking method that awaits a message from the server.
	 * @throws IOException
	 */
	public synchronized void recieveMessage() throws IOException {
		payload = new byte[128];
		DatagramPacket packet = new DatagramPacket(payload, payload.length);
		incoming.receive(packet);
		SocketAddress senderAddress = packet.getSocketAddress();
		String message = extractMessage(packet);
		// System.out.printf("%s <- %s\n", incoming.getLocalAddress().toString()+":"+incoming.getLocalPort(), message);
		processMessage(senderAddress, message);
	}

	
	/**
	 * Sends a message to the remote address specified by the SocketAddress.
	 * @param String message to send.
	 * @param SocketAddress of recipient.
	 */
	public void sendMessage(String str, SocketAddress recipient) throws IOException {
		DatagramPacket packet;
		payload = new byte[128];
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream (baos);
		out.writeUTF(str);
		 out.close();
		 byte[] payload = baos.toByteArray();
		 packet = new DatagramPacket (payload, payload.length, recipient);
		 // System.out.printf("%s -> %s\n", str, incoming.getLocalAddress().toString()+":"+incoming.getLocalPort());
		 incoming.send(packet);
	}

	/**
	 * Starts a thread that listens for incoming messages.
	 */
	public void start()
	{
		listenerThread = new Thread(){
			@Override
			public void run() {
				while(true)
					try {
						recieveMessage();
					} catch (IOException e) {
						System.err.println("Error occurred when recieving messages: "+e.getMessage());
						System.exit(1);
					}
			}			
		};
		running = true;
		listenerThread.start();
	}
	
	/**
	 * Stops the thread listening for incoming messages.
	 */
	public void stop()
	{
		if(running)
		{
			listenerThread.interrupt();
		}
	}

}
