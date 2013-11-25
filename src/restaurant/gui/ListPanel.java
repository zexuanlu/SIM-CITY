package restaurant.gui;

import restaurant.Restaurant1CustomerRole;
import restaurant.Restaurant1HostRole;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener, KeyListener {

	public JScrollPane pane =
			new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JCheckBox newstate;
	private JTextField text;
	private JPanel view = new JPanel();
	private List<JButton> list = new ArrayList<JButton>();
	private JButton addPersonB = new JButton("Add");
	private int dis = 20;
	private RestaurantPanel restPanel;
	private String type;
	public boolean s = false;
	/**
	 * Constructor for ListPanel.  Sets up all the gui
	 *
	 * @param rp   reference to the restaurant panel
	 * @param type indicates if this is for customers or waiters
	 */
	public ListPanel(RestaurantPanel rp, String type) {
		restPanel = rp;
		this.type = type;

		setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
		add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

		text = new JTextField();
		text.setMaximumSize(getMaximumSize());

		newstate = new JCheckBox();
		newstate.setVisible(true);
		newstate.addActionListener(this);
		newstate.setText("Hungry?");


		addPersonB.addActionListener(this);
		add(addPersonB);
		add(newstate);
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

		if(e.getSource() == newstate){
			s = newstate.isSelected();}
		if (e.getSource() == addPersonB) {
			// Chapter 2.19 describes showInputDialog()
			addPerson(text.getText());

			restPanel.addPerson("Customer", text.getText());
		}
		else {
			// Isn't the second for loop more beautiful?
			/*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
			for (JButton temp:list){
				if (e.getSource() == temp)
					restPanel.showInfo(type, temp.getText());
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
	public void addPerson(String name) {
		if (name != null) {
			JButton button = new JButton(name);
			button.setBackground(Color.white);

			Dimension paneSize = pane.getSize();
			Dimension buttonSize = new Dimension(paneSize.width - dis,
					(int) (paneSize.height / 7));
			button.setPreferredSize(buttonSize);
			button.setMinimumSize(buttonSize);
			button.setMaximumSize(buttonSize);
			button.addActionListener(this);
			list.add(button);
			view.add(button);
			restPanel.addPerson(type, name);//puts customer on list
			restPanel.showInfo(type, name);//puts hungry button on panel
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
