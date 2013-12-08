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
		locations.put("Host", new Position(430,70));
		locations.put("Teller1", new Position(80, 340));
		locations.put("Teller2", new Position(160, 340));
		locations.put("Teller3", new Position(240, 340));
		locations.put("Teller4", new Position(320, 340));
		locations.put("Teller5", new Position(400, 340));
		locations.put("waitArea0", new Position(60 ,240));
		locations.put("waitArea1", new Position(100 ,240));
		locations.put("waitArea2", new Position(140 ,240));
		locations.put("waitArea3", new Position(180 ,240));
		locations.put("waitArea4", new Position(220 ,240));
		locations.put("waitArea5", new Position(60 ,200));
		locations.put("waitArea6", new Position(100 ,200));
		locations.put("waitArea7", new Position(140 ,200));
		locations.put("waitArea8", new Position(180 ,200));
		locations.put("waitArea9", new Position(220 ,200));
		locations.put("waitArea10", new Position(60, 160));
		locations.put("waitArea11", new Position(100 ,160));
		locations.put("waitArea12", new Position(140 ,160));
		locations.put("waitArea13", new Position(180 ,160));
		locations.put("waitArea14", new Position(220 ,160));
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
