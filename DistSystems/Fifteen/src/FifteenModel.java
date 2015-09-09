

import java.util.ArrayList;
import java.util.List;

/**
 * Actual model of the game that stores the state of the game and updates the views
 * accordingly when it changes.
 * @author Evan Wheeler
 *
 */
public class FifteenModel implements FifteenViewListener{
	
	FifteenGameInstance game;
	List<FifteenModelListener> views = new ArrayList<FifteenModelListener>();
	
	public FifteenModel()
	{
		game = new FifteenGameInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenViewListener#chooseDigit(int, int)
	 */
	@Override
	public void chooseDigit(int digit, int id) {
		Player p = game.chooseDigit(digit, id);
		updateViews();

				if(p.getScore()==15)
				{
					for(FifteenModelListener view: views)
						view.setWinner(id);
				}
				if(game.isDraw())
				{
					for(FifteenModelListener view: views)
						view.setWinner(0);
				}
	}


	/* (non-Javadoc)
	 * @see interfaces.FifteenViewListener#join(interfaces.FifteenModelListener, java.lang.String)
	 */
	@Override
	public Player join(FifteenModelListener newview, String name)
	{
		views.add(newview);
		Player newPlayer = game.addPlayer(name);
		newview.assignID(newPlayer.getID());
		for(FifteenModelListener view: views)
		{
			view.registerPlayer(newPlayer);
		}
		if(newPlayer.getID()==2)
		{
			newview.registerPlayer(game.getPlayer1());
			newGame();
		}
		return newPlayer;
	}
	
	/**
	 * Resets the game, and notifies the views.
	 */
	@Override
	public void newGame() {
		game.newGame();
		for(FifteenModelListener view: views)
		{
			view.setAvailableDigits(game.getDigits());
			view.setScore(game.getPlayer1().getID(), game.getPlayer1().getScore());
			view.setScore(game.getPlayer2().getID(), game.getPlayer2().getScore());
			view.setTurn(game.getTurnID());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.FifteenViewListener#quitGame()
	 */
	@Override
	public void quitGame() {
		for(FifteenModelListener view : views)
		{
			((FifteenViewProxy) view).removeClient();
			view.quit();
		}
	}

	/**
	 * Updates all associated views with the state of the game. 
	 */
	private void updateViews()
	{
		for(FifteenModelListener view: views)
		{
		view.setAvailableDigits(game.getDigits());
		view.setTurn(game.getTurnID());
		view.setScore(game.getPlayer1().getID(), game.getPlayer1().getScore());
		view.setScore(game.getPlayer2().getID(), game.getPlayer2().getScore());
		}
	}

}
