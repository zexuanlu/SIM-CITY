package bank.gui;

import bank.interfaces.*;

import java.awt.*;
import java.util.*;

/**
 * This class represents the 
 * customers in the bank
 */
public class BankCustomerGui implements Gui{

	private BankCustomer bc = null;
	private boolean isPresent = true;
	private boolean atDestination = true;
	private Map<String, Dimension> locations = new HashMap<String, Dimension>();

	AnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;

	public BankCustomerGui(BankCustomer c, AnimationPanel gui){ //HostAgent m) {
		bc = c;
		xPos = -20;
		yPos = 300;
		xDestination = -20;
		yDestination = 300;
		this.gui = gui;
		locations.put("Host", new Dimension(30, 265));
		locations.put("Teller", new Dimension(220, 40));
		locations.put("Outside", new Dimension(-20, 200));
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (!atDestination && xPos == xDestination && yPos == yDestination) {
			bc.msgAtDestination();
			atDestination = true;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 20, 20);
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToLocation(String location){
		atDestination = false;
		Dimension d = locations.get(location);
		xDestination = d.width;
		yDestination = d.height;
	}
	public void DoGoToPos(int x, int y){
		xDestination = x;
		yDestination = y;
	}

}
