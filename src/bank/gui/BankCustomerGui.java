package bank.gui;

import bank.BankCustomerRole;

import java.awt.*;
import java.util.*;

import person.PersonAgent;
import simcity.astar.Position;
import utilities.Gui;

/**
 * This class represents the 
 * customers in the bank
 */
public class BankCustomerGui implements Gui{

	private BankCustomerRole bc = null;
	private boolean isPresent = true;
	private boolean atDestination = true;
	private Map<String, Position> locations = new HashMap<String, Position>();

	BankAnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;

	public BankCustomerGui(BankCustomerRole c){//, BankAnimationPanel gui){ //HostAgent m) {
		bc = c;
		xPos = 200;
		yPos = -20;
		xDestination = 200;
		yDestination = -20;
		//this.gui = gui;
		locations.put("Host", new Position(320,80));
		locations.put("Teller1", new Position(40, 340));
		locations.put("Teller2", new Position(100, 340));
		locations.put("Teller3", new Position(160, 340));
		locations.put("Teller4", new Position(220, 340));
		locations.put("Teller5", new Position(280, 340));
		locations.put("Teller6", new Position(340, 340));
		locations.put("Teller7", new Position(400, 340));
		locations.put("Teller8", new Position(460, 340));
		locations.put("Teller9", new Position(520, 340));
		locations.put("Teller10", new Position(580, 340));
		locations.put("waitArea0", new Position(460 ,80));
		locations.put("waitArea1", new Position(460 ,120));
		locations.put("waitArea2", new Position(460 ,160));
		locations.put("waitArea3", new Position(460 ,200));
		locations.put("waitArea4", new Position(460 ,240));
		locations.put("waitArea5", new Position(500 ,80));
		locations.put("waitArea6", new Position(500 ,120));
		locations.put("waitArea7", new Position(500 ,160));
		locations.put("waitArea8", new Position(500 ,200));
		locations.put("waitArea9", new Position(500 ,240));
		locations.put("waitArea10", new Position(540, 80));
		locations.put("waitArea11", new Position(540 ,120));
		locations.put("waitArea12", new Position(540 ,160));
		locations.put("waitArea13", new Position(540 ,200));
		locations.put("waitArea14", new Position(540 ,240));
		locations.put("Outside", new Position(280, -20));
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
		g.drawString(((PersonAgent)bc.getPerson()).getName(), xPos-14, yPos-5);
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToLocation(String location){
		atDestination = false;
		Position d = locations.get(location);
		xDestination = d.getX();
		yDestination = d.getY();
	}
	public void DoGoToPos(int x, int y){
		xDestination = x;
		yDestination = y;
	}

}
