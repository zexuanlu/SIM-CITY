package simcity.gui;

import java.awt.*;

import simcity.BusStopAgent; 
import utilities.Gui;

public class BusStopGui implements Gui{	
	
	private BusStopAgent busStop; 
	public int xPos, yPos; 
	
	public BusStopGui(BusStopAgent bs,int x, int y){
		busStop = bs; 
		xPos = x;
		yPos = y; 
	}
	
	public void draw(Graphics2D g) {  
        g.setColor(Color.MAGENTA);
		g.fillRect(xPos,yPos,10,10);
	}

	public boolean isPresent() {
		return true;
	}

	public void updatePosition() { //should never move so this should never be used
	}
	
	public Dimension getDim(){
		Dimension d = new Dimension(xPos,yPos);
		return d; 
		
	}

}
