package bank.gui;

import bank.interfaces.*;

import java.awt.*;
import java.util.*;

/**
 * This class represents the 
 * tellers in the bank
 */
public class BankHostGui implements Gui{

	private BankHost bh = null;
	private boolean isPresent = true;
	private boolean atDestination = true;
	private Map<String, Dimension> locations = new HashMap<String, Dimension>();

	BankAnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;

	public BankHostGui(BankHost t){//, BankAnimationPanel gui){ //HostAgent m) {
		bh = t;
		xPos = 200;
		yPos = -20;
		xDestination = 200;
		yDestination = -20;
		//this.gui = gui;
		locations.put("Host", new Dimension(320,120));
		gui.addGui(this);
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
			bh.msgAtDestination();
			atDestination = true;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
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
