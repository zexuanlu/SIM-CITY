package simcity.gui;

import java.awt.*;

import javax.swing.ImageIcon;

import simcity.BusStopAgent; 
import utilities.Gui;

public class BusStopGui implements Gui{	
	
	private BusStopAgent busStop; 
	public int xPos, yPos; 
	public ImageIcon img = new ImageIcon(this.getClass().getResource("busstop.png"));
	public Image bsImg = img.getImage();
	
	public BusStopGui(BusStopAgent bs,int x, int y){
		busStop = bs; 
		xPos = x;
		yPos = y; 
	}
	
	public void draw(Graphics2D g) {  
        g.setColor(Color.MAGENTA);
		g.fillRect(xPos,yPos,10,10);
		g.drawImage(bsImg, xPos, yPos, 10, 10, null);
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
	
	public void setPresent(boolean b){
		
	}

}
