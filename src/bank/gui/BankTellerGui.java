package bank.gui;

import bank.BankTellerRole;

import java.awt.*;
import java.util.*;

import javax.swing.ImageIcon;

import person.PersonAgent;
import simcity.astar.Position;
import utilities.Gui;

/**
 * This class represents the 
 * tellers in the bank
 */
public class BankTellerGui implements Gui{

	private BankTellerRole bt = null;
	public boolean isPresent = true;
	private boolean atDestination = true;
	private Map<String, Position> locations = new HashMap<String, Position>();

	BankAnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;

	public ImageIcon img = new ImageIcon(this.getClass().getResource("image/teller.png"));
    public Image m1 = img.getImage();
	
	public BankTellerGui(BankTellerRole t){//, BankAnimationPanel gui){ //HostAgent m) {
		bt = t;
		xPos = 240;
		yPos = 400;
		xDestination = 240;
		yDestination = 400;
		//this.gui = gui;
		locations.put("Teller1", new Position(80, 380));
		locations.put("Teller2", new Position(160, 380));
		locations.put("Teller3", new Position(240, 380));
		locations.put("Teller4", new Position(320, 380));
		locations.put("Teller5", new Position(400, 380));
		locations.put("Outside", new Position(240, 400));
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
			bt.msgAtDestination();
			atDestination = true;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.drawImage(m1, xPos, yPos, 20, 20, null);
		//g.fillRect(xPos, yPos, 20, 20);
		g.drawString(((PersonAgent)bt.getPerson()).getName(), xPos-14, yPos+30);
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
