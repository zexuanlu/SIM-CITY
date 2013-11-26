package person.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import person.PersonAgent;
import person.Position;
import simcity.gui.Gui;

public class PersonGui implements Gui{
	private PersonAgent agent = null;

	public int xPos = -20, yPos = -20;//default player position
	public int xDestination = 20, yDestination = 20;//default start position
	private boolean arrived; 
	private boolean isPresent;

	public PersonGui(PersonAgent agent) {
		this.agent = agent;
		arrived = false;
		isPresent = true;
	}

	public void updatePosition() {
		if (xPos < xDestination){ 
			xPos++; 
		}
		else if (xPos > xDestination){ 
			xPos--; 
		}

		if (yPos < yDestination){ 
			yPos++; 
		}
		else if (yPos > yDestination){ 
			yPos--; 
		}
		if(yPos == yDestination && xPos == xDestination && !arrived){
			arrived = true;
			agent.msgAtDest(new Position(yPos, xPos));
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.fillRect(xPos, yPos, 20, 20);
	}

	public boolean isPresent() {
		if(isPresent){
			return true;
		}
		else
			return false;
	}
	public void setPresent(boolean tf){
		isPresent = tf;
	}
	public void DoGoTo(Position p){
		xDestination = p.getX();
		yDestination = p.getY();
		arrived = false;
	}
	public int getX(){return xPos;}
	public int getY(){return yPos;}

}