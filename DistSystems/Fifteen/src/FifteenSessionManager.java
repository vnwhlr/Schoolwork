

import java.util.HashMap;


/**
 * Class responsible for managing the clients who have sent a message to the server,
 * and assigning them to a game instance. 
 * A ViewProxy sets this as its listener, and if the message indicated
 * they wish to join it assigns it to an instance (if another player is waiting) or creates a new one. 
 * @author Evan Wheeler
 *
 */
public class FifteenSessionManager implements FifteenViewListener{

	HashMap<FifteenModelListener, FifteenViewListener> sessions;
	FifteenViewListener openModel;
	
	public FifteenSessionManager()
	{
		sessions = new HashMap<FifteenModelListener, FifteenViewListener>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenViewListener#chooseDigit(int, int)
	 */
	@Override
	public void chooseDigit(int digit, int player) {}
	
	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenViewListener#join(interfaces.FifteenModelListener, java.lang.String)
	 */
	@Override
	public synchronized Player join(FifteenModelListener view, String name) {
		Player p;
		if(openModel==null)
		{
			openModel = new FifteenModel();
			view.setViewListener(openModel);
			p = openModel.join(view, name);			
			sessions.put(view, openModel);
		}
		else
		{
			view.setViewListener(openModel);
			p = openModel.join(view, name);	
			sessions.put(view, openModel);
			openModel=null;
		}
		return p;
	}
	
	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenViewListener#newGame()
	 */
	@Override
	public void newGame() {}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenViewListener#quitGame()
	 */
	@Override
	public void quitGame() {}

	public void remove(FifteenModelListener viewp)
	{
		sessions.remove(viewp);
	}

}
