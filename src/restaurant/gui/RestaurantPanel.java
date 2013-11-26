package restaurant.gui;

import restaurant.Restaurant1CookRole;
import restaurant.Restaurant1CustomerRole;
import restaurant.Restaurant1HostRole;
import restaurant.Restaurant1WaiterRole;
import restaurant.Restaurant1CashierRole;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {
	private int count = 1;
	private int Cuscount = 0;
	private int x = 1;
	private int y = 2;
	private int dis1 = 20;
	private int dis2 = 10;
	//Host, cook, waiters and customers
	private Restaurant1HostRole ho = new Restaurant1HostRole("Jack");
	//protected Restaurant1WaiterRole waiter= new Restaurant1WaiterRole("Ross");
	private Restaurant1CookRole cook = new Restaurant1CookRole("BrotherCai");
	private Restaurant1CashierRole cashier = new Restaurant1CashierRole("Yo");


	private Vector<Restaurant1CustomerRole> customers = new Vector<Restaurant1CustomerRole>();
	private Vector<Restaurant1WaiterRole> waiters = new Vector<Restaurant1WaiterRole>();

	private JPanel restLabel = new JPanel();
	private ListPanel customerPanel = new ListPanel(this, "Customers");
	private WaiterListPanel waiterPanel = new WaiterListPanel(this, "Waiters");
	private JPanel group = new JPanel();
	//private WaiterGui waiterGui = new WaiterGui(waiter,ho);

	private RestaurantGui gui; //reference to main gui

	public RestaurantPanel(RestaurantGui gui) {
		this.gui = gui;
		//waiter.setGui(waiterGui);

		//gui.animationPanel.addGui(waiterGui);
		//waiter.sethost(ho);
		//waiter.setcook(cook);
		//waiter.setCashier(cashier);
		//ho.setwaiter(host);
		//waiter.startThread();
		ho.startThread();
		cook.startThread();
		CookGui cg = new CookGui(cook, gui);
		cook.setGui(cg);
		cook.setCashier(cashier);
		gui.animationPanel.addGui(cg);
		cashier.startThread();
		
		
		//ho.msgaddwaiter(waiter);

		setLayout(new GridLayout(x, y, dis1, dis1));
		group.setLayout(new GridLayout(x, y, dis2, dis2));

		group.add(customerPanel);
		group.add(waiterPanel);
		initRestLabel();
		add(restLabel);
		add(group);


	}

	/**
	 * Sets up the restaurant label that includes the menu,
	 * and host and cook information
	 */
	private void initRestLabel() {
		JLabel label = new JLabel();
		//restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
		restLabel.setLayout(new BorderLayout());
		label.setText(
				"<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" +  "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

		restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
		restLabel.add(label, BorderLayout.CENTER);
		restLabel.add(new JLabel("               "), BorderLayout.EAST);
		restLabel.add(new JLabel("               "), BorderLayout.WEST);
	}

	/**
	 * When a customer or waiter is clicked, this function calls
	 * updatedInfoPanel() from the main gui so that person's information
	 * will be shown
	 *
	 * @param type indicates whether the person is a customer or waiter
	 * @param name name of person
	 */
	public void showInfo(String type, String name) {

		if (type.equals("Customers")) {

			for (int i = 0; i < customers.size(); i++) {
				Restaurant1CustomerRole temp = customers.get(i);
				if (temp.getName() == name)
					gui.updateInfoPanel(temp);
			}
		}
	}

	public void showWaiterInfo( String name) {
		for (int i = 0; i < waiters.size(); i++) {
			Restaurant1WaiterRole temp = waiters.get(i);
			if (temp.getName() == name)
				gui.updateInfoPanel(temp);
		}

	}

	/**
	 * Adds a customer or waiter to the appropriate list
	 *
	 * @param type indicates whether the person is a customer or waiter (later)
	 * @param name name of person
	 */
	public void addPerson(String type, String name) {

		if (type.equals("Customers")) {
			int a = Cuscount;
			if(Cuscount < 9){
			Cuscount ++;
			}
			else{
				Cuscount = 0;
			}
			Restaurant1CustomerRole c = new Restaurant1CustomerRole(name);	
			CustomerGui g = new CustomerGui(c, gui);
			
			gui.animationPanel.addGui(g);// dw
			c.setHost(ho);
			//c.setwaiter(host);
			c.setGui(g);
			c.setCashier(cashier);
			customers.add(c);
			c.startThread();
			c.setLocation(a);
			if(name.compareTo("zero") == 0){
				c.setMoney(0);
			}
			if(name.compareTo("six") == 0){
				c.setMoney(6);
			}
			if(name.compareTo("pizza") == 0){
				c.setMoney(9);
			}
			if(name.compareTo("chicken") == 0){
				c.setMoney(12);
			}
			if(customerPanel.s == true){
				c.getGui().setHungry();
			}
		}
	}

	public void addWaiter(String name) {
		int s = count;
		count++;
		Restaurant1WaiterRole c = new Restaurant1WaiterRole(name);	
		WaiterGui g = new WaiterGui(c, ho);

		gui.animationPanel.addGui(g);// dw
		c.sethost(ho);
		c.setcook(cook);
		c.setGui(g);
		waiters.add(c);
		c.setCashier(cashier);
		waiters.add(c);
		c.startThread();
		c.setNumber(s);
		ho.msgaddwaiter(c);

	}

}
