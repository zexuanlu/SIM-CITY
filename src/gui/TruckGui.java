package gui;

import java.awt.Graphics2D;
import market.*;

public class TruckGui implements Gui{
	
	private TruckAgent agent;
	private int xPos, yPos;
	private int xDestination, yDestination;
	
	
	public void setAgent(TruckAgent agent){
		this.agent = agent;
	}

	public void updatePosition() {
		if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos =xPos - 1;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos = yPos - 1;
		
	}


	public void draw(Graphics2D g) {

		
	}


	public boolean isPresent() {

		return true;
	}

	public void DoGoToRestaurant(){
		
	}
	
	public void DoGoBack(){
		
	}
	
}
