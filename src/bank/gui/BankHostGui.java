package bank.gui;

import bank.BankHostRole;

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
public class BankHostGui implements Gui{

	private BankHostRole bh = null;
	public boolean isPresent = true;
	private boolean atDestination = true;
	private Map<String, Position> locations = new HashMap<String, Position>();

	BankAnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	
	public ImageIcon img = new ImageIcon(this.getClass().getResource("image/host.png"));
    public Image m1 = img.getImage();

	public BankHostGui(BankHostRole t){//, BankAnimationPanel gui){ //HostAgent m) {
		bh = t;
		xPos = 550;
		yPos = 70;
		xDestination = 550;
		yDestination = 70;
		//this.gui = gui;
		locations.put("Host", new Position(465,120));
		locations.put("Outside", new Position(530, 70));
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
			bh.msgAtDestination();
			atDestination = true;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.drawImage(m1, xPos, yPos, 20, 20, null);
		//g.fillRect(xPos, yPos, 20, 20);
		g.drawString(((PersonAgent)bh.getPerson()).getName(), xPos-14, yPos+30);
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
