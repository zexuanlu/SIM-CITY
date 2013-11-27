package bank.gui;

import agent.Gui;
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

	BankAnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;

	public BankCustomerGui(BankCustomer c){//, BankAnimationPanel gui){ //HostAgent m) {
		bc = c;
		xPos = 200;
		yPos = -20;
		xDestination = 200;
		yDestination = -20;
		//this.gui = gui;
		locations.put("Host", new Dimension(320,80));
		locations.put("Teller1", new Dimension(40, 340));
		locations.put("Teller2", new Dimension(100, 340));
		locations.put("Teller3", new Dimension(160, 340));
		locations.put("Teller4", new Dimension(220, 340));
		locations.put("Teller5", new Dimension(280, 340));
		locations.put("Teller6", new Dimension(340, 340));
		locations.put("Teller7", new Dimension(400, 340));
		locations.put("Teller8", new Dimension(460, 340));
		locations.put("Teller9", new Dimension(520, 340));
		locations.put("Teller10", new Dimension(580, 340));
		locations.put("waitArea0", new Dimension(460 ,80));
		locations.put("waitArea1", new Dimension(460 ,120));
		locations.put("waitArea2", new Dimension(460 ,160));
		locations.put("waitArea3", new Dimension(460 ,200));
		locations.put("waitArea4", new Dimension(460 ,240));
		locations.put("waitArea5", new Dimension(500 ,80));
		locations.put("waitArea6", new Dimension(500 ,120));
		locations.put("waitArea7", new Dimension(500 ,160));
		locations.put("waitArea8", new Dimension(500 ,200));
		locations.put("waitArea9", new Dimension(500 ,240));
		locations.put("Outside", new Dimension(280, -20));
		//gui.addGui(this);
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
		System.err.println(location);
		Dimension d = locations.get(location);
		xDestination = d.width;
		yDestination = d.height;
	}
	public void DoGoToPos(int x, int y){
		xDestination = x;
		yDestination = y;
	}

}
