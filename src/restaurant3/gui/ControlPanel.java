package restaurant3.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import restaurant3.*;
import market.*;
import person.PersonAgent;

public class ControlPanel extends JPanel implements ActionListener{
	
	//Dimensions
	final int width = 300;
	final int height = 480;
	Dimension size = new Dimension(width, height);

	//Gui panel references
	Restaurant3GUI rGui;
	Restaurant3AnimationPanel aPanel;
	
	//Buttons for adding customers and waiters
	JButton addC = new JButton("Add C");
	JButton addW = new JButton("Add W");
	JButton setH = new JButton("Set H");
	
	//Counters for wtrs and custs
	int cCount = 0;
	int wCount = 0;
	
	//Agent references
	Restaurant3HostRole host = new Restaurant3HostRole("Host", new PersonAgent());
	Restaurant3CookRole cook = new Restaurant3CookRole("Cook", new PersonAgent());
	Restaurant3CashierRole cashier = new Restaurant3CashierRole("Cashier", new PersonAgent());
	Vector<Restaurant3CustomerRole> customers = new Vector<Restaurant3CustomerRole>();
	Vector<Restaurant3WaiterRole> waiters = new Vector<Restaurant3WaiterRole>();
	
	//Agent guis
	Restaurant3CookGui cGui = new Restaurant3CookGui(cook);
	
	public ControlPanel(Restaurant3GUI rGui, Restaurant3AnimationPanel aPanel) {
		//initialize reference panels
		this.rGui = rGui;
		this.aPanel = aPanel;
		
		//Set up panel
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder(" Control Panel "));
		
		//Initialize actionListeners
		addC.addActionListener(this);
		addW.addActionListener(this);
		setH.addActionListener(this);
		
		//Add buttons
		this.add(addC);
		this.add(addW);
		this.add(setH);
		
		//Add guis
		cook.setGui(cGui);
		cGui.setPresent(true);
		aPanel.addGui(cGui);
		
		//Set agent references
		cook.setCashier(cashier);
		
		//Start agent threads	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == addC){
			Restaurant3CustomerRole c = new Restaurant3CustomerRole(("Customer"+ ++cCount), new PersonAgent());
			Restaurant3CustomerGui g = new Restaurant3CustomerGui(c);
			c.setGui(g);
			customers.add(c);
			aPanel.addGui(g);
			c.gotHungry();
		}
		if(e.getSource() == addW){
			Restaurant3WaiterRole w = new Restaurant3WaiterRole(("Waiter" + ++wCount), new PersonAgent());
			Restaurant3WaiterGui g = new Restaurant3WaiterGui(w);
			w.setGui(g);
			waiters.add(w);
			host.addWaiter(w);
			aPanel.addGui(g);
			g.setPresent(true);
		}
		if(e.getSource() == setH){
			for(Restaurant3CustomerRole c : customers){
				c.gotHungry();
			}
		}
	}

}
