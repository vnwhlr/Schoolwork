
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;

/**
 * Class FifteenView provides the user interface for the Fifteen network game.
 *
 * @author  Alan Kaminsky
 * @author  Evan Wheeler
 * @version 16-Mar-2014
 */
public class FifteenView
	extends JFrame implements FifteenModelListener
	{

	/**
	 * Class DigitButton provides a button labeled with a digit.
	 */
	private class DigitButton
		extends JButton
		{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4111082955865015606L;
		private int digit;
		private boolean enabled = true;
		private boolean available = true;
		
		
		/**
		 * Construct a new digit button.
		 *
		 * @param  digit  Digit for the button label.
		 */
		public DigitButton
			(int digit)
			{
			super ("" + digit);
			this.digit = digit;
			addActionListener (new ActionListener()
				{
				@Override
				public void actionPerformed (ActionEvent e)
					{
					onDigitButton (DigitButton.this.digit);
					}
				});
			}

		/**
		 * Make this digit button available or unavailable. When available, the
		 * button displays its digit. When not available, the button is blank.
		 *
		 * @param  available  True if available, false if not.
		 */
		public void available
			(boolean available)
			{
			this.available = available;
			setText (available ? "" + digit : " ");
			updateButton();
			}

		/**
		 * Enable or disable this digit button. When enabled and available,
		 * clicking the button performs the appropriate action. Otherwise,
		 * clicking the button has no effect.
		 *
		 * @param  enabled  True if enabled, false if not.
		 */
		@Override
		public void setEnabled
			(boolean enabled)
			{
			this.enabled = enabled;
			updateButton();
			}

		/**
		 * Update this digit button's label and enabled state.
		 */
		private void updateButton()
			{
			super.setEnabled (available && enabled);
			}
		}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8562277217063109672L;
	private static final int GAP = 10;
	private static final int COLS = 12;
	private FifteenViewListener model;
	private Player me;
	private Player opponent;
	private int myID;

	/**
	 * User interface widgets.
	 */
	private DigitButton[] digitButton;
	private JTextField myScoreField;
	private JTextField theirScoreField;
	private JTextField winnerField;
	private JButton newGameButton;

	/**
	 * Construct a new FifteenView object.
	 *
	 * @param  myName  Player's name.
	 */
	public FifteenView
		(String myName)
		{
		super ("Fifteen -- " + myName);
		JPanel panel = new JPanel();
		add (panel);
		panel.setLayout (new BoxLayout (panel, BoxLayout.X_AXIS));
		panel.setBorder (BorderFactory.createEmptyBorder (GAP, GAP, GAP, GAP));
		JPanel panel_a = new JPanel();
		panel.add (panel_a);
		panel_a.setLayout (new BoxLayout (panel_a, BoxLayout.Y_AXIS));
		digitButton = new DigitButton [9];
		for (int i = 0; i < 9; ++ i)
			{
			panel_a.add (digitButton[i] = new DigitButton (i + 1));
			digitButton[i].setAlignmentX (0.5f);
			digitButton[i].setEnabled (false);
			digitButton[i].setMinimumSize (digitButton[i].getPreferredSize());
			digitButton[i].setMaximumSize (digitButton[i].getPreferredSize());
			digitButton[i].setSize (digitButton[i].getPreferredSize());
			}
		panel.add (Box.createHorizontalStrut (GAP));
		JPanel panel_b = new JPanel();
		panel.add (panel_b);
		panel_b.setLayout (new BoxLayout (panel_b, BoxLayout.Y_AXIS));
		panel_b.add (Box.createRigidArea (new Dimension (0, GAP)));
		panel_b.add (myScoreField = new JTextField (COLS));
		myScoreField.setAlignmentX (0.5f);
		myScoreField.setEditable (false);
		myScoreField.setMaximumSize (myScoreField.getPreferredSize());
		panel_b.add (Box.createRigidArea (new Dimension (0, GAP)));
		panel_b.add (theirScoreField = new JTextField (COLS));
		theirScoreField.setAlignmentX (0.5f);
		theirScoreField.setEditable (false);
		theirScoreField.setMaximumSize (theirScoreField.getPreferredSize());
		theirScoreField.setText("Waiting for partner");
		panel_b.add (Box.createRigidArea (new Dimension (0, GAP)));
		panel_b.add (winnerField = new JTextField (COLS));
		winnerField.setAlignmentX (0.5f);
		winnerField.setEditable (false);
		winnerField.setMaximumSize (winnerField.getPreferredSize());
		panel_b.add (Box.createVerticalGlue());
		panel_b.add (newGameButton = new JButton ("New Game"));
		newGameButton.setAlignmentX (0.5f);
		newGameButton.setMaximumSize (newGameButton.getPreferredSize());
		newGameButton.setEnabled (false);
		newGameButton.addActionListener (new ActionListener()
			{
			@Override
			public void actionPerformed (ActionEvent e)
				{
				onNewGameButton();
				}
			});
		addWindowListener (new WindowAdapter()
			{
			@Override
			public void windowClosing (WindowEvent e)
				{
				onClose();
				}
			});
		pack();
		setVisible(true);
		}

	/* (non-Javadoc)
	 * @see interfaces.FifteenModelListener#assignID(int)
	 */
	@Override
	public void assignID(int playerID) {
		myID = playerID;
	}

	/**
	 * Helper method that enables (or disables) the digit buttons.
	 * @param Boolean value, true to enable, false to disable
	 */
	private void enableDigits(boolean enable)
	{
		for(int i=0;i<9;i++)
		{
			digitButton[i].setEnabled(enable);
		}
	}

	/* (non-Javadoc)
	 * @see FifteenModelListener#notifyError(java.lang.Exception)
	 */
	@Override
	public void notifyError(Exception e) {
		JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
	}

	/**
	 * Take action when the Fifteen window is closing.
	 */
	private void onClose()
		{
			model.quitGame();
			System.exit(0);
		}

	/**
	 * Take action when a digit button is clicked.
	 *
	 * @param  digit  Digit that was clicked.
	 */
	private void onDigitButton
		(int digit)
		{
			model.chooseDigit(digit, me.getID());
		}

	/**
	 * Take action when the New Game button is clicked.
	 */
	private void onNewGameButton()
		{
			enableDigits(false);
			winnerField.setText("");
			model.newGame();
		}

	/* (non-Javadoc)
	 * @see FifteenModelListener#quit()
	 */
	@Override
	public void quit() {
			System.exit(0);
	}

	/* (non-Javadoc)
	 * @see FifteenModelListener#registerOpponent(java.lang.String)
	 */
	@Override
	public void registerPlayer(Player player) {
        if(player.getID()==myID)
        {
        	me = player;
        	myScoreField.setText(me.getName() + " = 0");
        }
        else
        {
        	opponent=player;
        	theirScoreField.setText(opponent.getName() + " = 0");
        }
        if(opponent!=null&&me!=null)
        {
	        newGameButton.setEnabled(true);
	        
        }
	}

	/* (non-Javadoc)
	 * @see FifteenModelListener#setAvailableDigits(boolean[])
	 */
	@Override
	public void setAvailableDigits(boolean[] digitsArr) {
		boolean newGameFlag=true;
		for(int i=0;i<digitsArr.length;i++)
		{
			if(digitsArr[i]==false)
				newGameFlag=true;
			digitButton[i].available(digitsArr[i]);
		}
		if(newGameFlag)
			winnerField.setText("");		
	}

	/* (non-Javadoc)
	 * @see FifteenModelListener#setScore(boolean, int)
	 */
	@Override
	public void setScore(int playerID, int score) {
		if(playerID==me.getID())
			myScoreField.setText(me.getName() + " = " + score);
		else
			theirScoreField.setText(opponent.getName() + " = " + score);
	}

	/* (non-Javadoc)
	 * @see FifteenModelListener#setTurn(boolean)
	 */
	@Override
	public void setTurn(int playerID) {
		enableDigits(playerID==me.getID());
	}

	/* (non-Javadoc)
	 * @see FifteenModelListener#setViewListener(FifteenViewListener)
	 */
	@Override
	public void setViewListener(FifteenViewListener model) {
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see FifteenModelListener#setWinner(boolean, boolean)
	 */
	@Override
	public void setWinner(int winnerID) {
		if(winnerID == 0)
			winnerField.setText("Draw!");
		else 
		{
			if(winnerID == me.getID())
				winnerField.setText(me.getName() + " wins!");
            else
            	winnerField.setText(opponent.getName() + " wins!");
		}
		enableDigits(false);
	}

	}
