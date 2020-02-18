package de.freese.base.swing.components.text;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * TextField was eine Autovervollstaendigung bietet aus den vorherigen Eingaben.
 * 
 * @author Thomas Freese
 */
public class AutoCompleteableTextField extends JTextField
{
	/**
	 * MenuAction um den darin enthaltenen Text in das Textfeld zu setzten.
	 * 
	 * @author Thomas Freese
	 */
	private class PrevSearchAction extends AbstractAction
	{
		/**
         * 
         */
		private static final long serialVersionUID = -6115968950918667824L;

		/**
         * 
         */
		private String term = null;

		/**
		 * Creates a new {@link PrevSearchAction} object.
		 * 
		 * @param term String
		 */
		public PrevSearchAction(final String term)
		{
			super();

			this.term = term;
			putValue(Action.NAME, this.term);
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			Runnable runner = new Runnable()
			{
				@Override
				public void run()
				{
					setText(PrevSearchAction.this.term);

					// getFilterTextField().setCaretPosition(term.length());
					requestFocus();
				}
			};

			SwingUtilities.invokeLater(runner);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return this.term;
		}
	}

	/**
     * 
     */
	private static final long serialVersionUID = 8765972663291526963L;

	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		JFrame frame = new JFrame("AutoCompleteableTextField");

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JLabel label = new JLabel("Text: ");

		label.setFocusable(true);

		frame.getContentPane().add(label, BorderLayout.WEST);
		frame.getContentPane().add(new AutoCompleteableTextField(50), BorderLayout.CENTER);

		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
     * 
     */
	private LinkedList<String> prevSearches = new LinkedList<>();

	/**
     * 
     */
	private JPopupMenu prevSearchMenu = null;

	/**
	 * Erstellt ein neues {@link AutoCompleteableTextField} Object.
	 */
	public AutoCompleteableTextField()
	{
		this(0);
	}

	/**
	 * Creates a new {@link AutoCompleteableTextField} object.
	 * 
	 * @param columns int
	 */
	public AutoCompleteableTextField(final int columns)
	{
		super(columns);

		addFocusListener(new FocusAdapter()
		{
			/**
			 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
			 */
			@Override
			public void focusLost(final FocusEvent e)
			{
				if ((AutoCompleteableTextField.this.prevSearchMenu == null)
						|| !AutoCompleteableTextField.this.prevSearchMenu.isVisible())
				{
					saveLastSearch();
				}
			}
		});

		addKeyListener(new KeyAdapter()
		{
			/**
			 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
			 */
			@Override
			public void keyReleased(final KeyEvent e)
			{
				if ((e.getKeyCode() == KeyEvent.VK_ENTER) || (e.getKeyCode() == KeyEvent.VK_TAB))
				{
					JPopupMenu prevSearchMenu = AutoCompleteableTextField.this.prevSearchMenu;

					// Wenn das PopupMenu geoeffnet ist, das selektierte MenuItem ausfuehren
					if ((prevSearchMenu != null) && prevSearchMenu.isVisible())
					{
						MenuElement[] path =
								MenuSelectionManager.defaultManager().getSelectedPath();

						if (path.length > 0)
						{
							JMenuItem menuItem = (JMenuItem) path[path.length - 1];

							menuItem.doClick();
							prevSearchMenu.setVisible(false);
						}

						return;
					}

					// Bei Enter oder Tabulator den Fokus wechseln und so den neuen Text zu
					// Vervollstaendigung aufnehmen
					transferFocus();
				}
				else if (Character.isJavaIdentifierPart(e.getKeyChar()))
				{
					// Popup einblenden
					popupMenu(0, (int) getSize().getHeight());
				}
			}
		});
	}

	/**
	 * Anzeigen des Popups.
	 * 
	 * @param x int
	 * @param y int
	 */
	private void popupMenu(final int x, final int y)
	{
		if (this.prevSearchMenu != null)
		{
			this.prevSearchMenu.setVisible(false);
			this.prevSearchMenu.removeAll();
			this.prevSearchMenu = null;
		}

		if ((this.prevSearches.size() > 0) && (getText().trim().length() > 0))
		{
			this.prevSearchMenu = new JPopupMenu();

			Iterator<String> it = this.prevSearches.iterator();
			List<String> matches = new ArrayList<>();

			// Treffer rausfinden
			while (it.hasNext())
			{
				String search = it.next();

				if (search.indexOf(getText().trim()) != -1)
				{
					matches.add(search);
				}
			}

			// Treffer sortieren
			Collections.sort(matches);

			for (int i = 0; i < matches.size(); i++)
			{
				Action action = new PrevSearchAction(matches.get(i));

				this.prevSearchMenu.add(action);
			}

			if (this.prevSearchMenu.getComponentCount() > 0)
			{
				this.prevSearchMenu.show(this, x, y);

				// Coursor wieder zurueck ins Textfeld
				// if (!hasFocus())
				// {
				requestFocus();

				// }
			}
		}
	}

	/**
	 * Text im Textfeld in die Historie aufnehmen, max. 10 Eintraege
	 */
	private void saveLastSearch()
	{
		String search = getText().trim();

		if ((search != null) && (search.length() > 1) && !this.prevSearches.contains(search))
		{
			// System.out.println("AutoCompleteableTextField.saveLastSearch(): " + search);
			this.prevSearches.addFirst(search);

			if (this.prevSearches.size() > 10)
			{
				this.prevSearches.removeLast();
			}
		}
	}
}
