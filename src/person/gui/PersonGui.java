package person.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import person.PersonAgent;
import person.Position;
import simcity.gui.Gui;

public class PersonGui implements Gui{
	private PersonAgent agent = null;

	private int xPos = -20, yPos = -20;//default player position
	private int xDestination = 20, yDestination = 20;//default start position
	private boolean arrived; 
	public PersonGui(PersonAgent agent) {
		this.agent = agent;
		arrived = false;
	}

	public void updatePosition() {
		if (xPos < xDestination){ xPos++; }
		else if (xPos > xDestination){ xPos--; }

		if (yPos < yDestination){ yPos++; }
		else if (yPos > yDestination){ yPos--; }

		if(yPos == yDestination && xPos == xDestination && !arrived && xDestination != 20 && yDestination != 20){
			arrived = true;
			agent.msgAtDest(new Position(yPos, xPos));
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.MAGENTA);
		g.fillRect(xPos, yPos, 20, 20);
	}

	public boolean isPresent() {
		return true;
	}

	public void DoGoTo(){
		System.out.println("Going to...");
		xDestination = 100;//p.getX();
		yDestination = 100;//p.getY();
		System.out.println(xDestination);
		arrived = false;
	}
	public int getX(){ return xPos;}
	public int getY(){ return yPos; }

}
