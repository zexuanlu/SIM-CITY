package person.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import person.PersonAgent;
import person.Position;

public class PersonGui implements Gui{
    private PersonAgent agent = null;

    private int xPos = -20, yPos = -20;//default player position
    private int xDestination = -20, yDestination = -20;//default start position
    public PersonGui(PersonAgent agent) {
        this.agent = agent;
    }
	@Override
	public void updatePosition() {
        if (xPos < xDestination){ xPos++; }
        else if (xPos > xDestination){ xPos--; }

        if (yPos < yDestination){ yPos++; }
        else if (yPos > yDestination){ yPos--; }
        
        if(yPos == yDestination && xPos == xDestination){
        	agent.msgAtDest(new Position(yPos, xPos));
        }
	}

	@Override
	public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
	}

	@Override
	public boolean isPresent() {
		return true;
	}
	
	public void DoGoTo(Position p){
		xDestination = p.getX();
		yDestination = p.getY();
	}
	
}
