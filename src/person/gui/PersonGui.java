package person.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import person.PersonAgent;
import person.Position;
import utilities.Gui;
import utilities.TrafficLightAgent;

public class PersonGui implements Gui{
	private PersonAgent agent = null;
	private TrafficLightAgent light = null;
	public int xPos = -20, yPos = -20;//default player position
	public int xDestination = 20, yDestination = 20;//default start position
	private boolean arrived; 
	public boolean isPresent;
	public boolean atLight;
	
	public PersonGui(PersonAgent agent) {
		this.agent = agent;
		arrived = false;
		isPresent = true;
	}
	public PersonGui(PersonAgent agent, TrafficLightAgent tlight){
		light = tlight;
		this.agent = agent;
		arrived = false;
		isPresent = false;
		atLight = false;
	}
	public void updatePosition() {
		if (xPos < xDestination){ //&& (yPos < 200 + 70 || yPos > 200 - 70)){ 
			xPos++; 
		}
		else if (xPos > xDestination){// && (yPos < 200 + 70 || yPos > 200 - 70)){ 
			xPos--; 
		}
		if (yPos < yDestination ){//&& (xPos < 360 + 70 || xPos > 360 - 70)){ 
			yPos++; 
		}
		else if (yPos > yDestination ){//{&& (xPos < 360 + 70 || xPos > 360 - 70)){ 
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
		g.drawString(agent.getName(), xPos-14, yPos-5);
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
	
	public void walkto(int x, int y){
		xPos = x; 
		yPos = y; 
		arrived = false; 
	}
	public void cross(){
		light.msgCheckLight(this.agent);
	}
	public int getX(){return xPos;}
	public int getY(){return yPos;}
	public void setStart(int x, int y){
		xPos = x;
		yPos = y;
	}
}
