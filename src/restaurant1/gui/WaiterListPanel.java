package restaurant1.gui;


import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


public class WaiterListPanel extends JPanel  implements ActionListener, KeyListener  {


	public JScrollPane pane =
			new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JTextField text;
	private JPanel view = new JPanel();
	private List<JButton> waiterlist = new ArrayList<JButton>();
	private JButton addwaiter;

	private RestaurantPanel restPanel;
	private String type;
	public boolean s = false;
	/**
	 * Constructor for ListPanel.  Sets up all the gui
	 *
	 * @param rp   reference to the restaurant panel
	 * @param type indicates if this is for customers or waiters
	 */
	public WaiterListPanel(RestaurantPanel rp, String type) {
		restPanel = rp;
		this.type = type;

		setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
		add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

		text = new JTextField();
		text.setMaximumSize(getMaximumSize());

		addwaiter = new JButton();
		addwaiter.addActionListener(this);
		addwaiter.setText("Add a waiter");


		add(addwaiter);
		add(text);
		view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
		pane.setViewportView(view);
		add(pane);
	}

	/**
	 * Method from the ActionListener interface.
	 * Handles the event of the add button being pressed
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addwaiter){
			System.out.println("dfdfdfd");
			addWaiter(text.getText());
		}
		else {
			for (JButton temp:waiterlist){
				if (e.getSource() == temp)
					restPanel.showWaiterInfo(temp.getText());
			}
		}
	}

	/**
	 * If the add button is pressed, this function creates
	 * a spot for it in the scroll pane, and tells the restaurant panel
	 * to add a new person.
	 *
	 * @param name name of new person
	 */

	public void addWaiter(String name) {
		if (name != null) {

			JButton button = new JButton(name);
			button.setBackground(Color.white);

			Dimension paneSize = pane.getSize();
			Dimension buttonSize = new Dimension(paneSize.width - 20,
					(int) (paneSize.height / 7));
			button.setPreferredSize(buttonSize);
			button.setMinimumSize(buttonSize);
			button.setMaximumSize(buttonSize);
			button.addActionListener(this);
			waiterlist.add(button);
			view.add(button);
			restPanel.addWaiter(name);//puts customer on list
			restPanel.showWaiterInfo(name);
			validate();
		}
	}



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}


}
