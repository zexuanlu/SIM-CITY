package market.gui;
import simcity.astar.*; 
import simcity.CarAgent;  
import utilities.Gui;
import market.MarketTruckAgent; 
import gui.panels.BuildingAnimationPanel;

import java.util.*; 
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

import javax.swing.ImageIcon;


public class MarketTruckGui implements Gui {

	public boolean isPresent;

	private int distance = 20;
	public int xPos = 200, yPos = 140, xDestination = 200, yDestination = 140;
	public int overallX, overallY;
	public boolean atstop = false;

	private MarketTruckAgent myTruck;
	BuildingAnimationPanel buildPanel;
	private int xrest = 200;
	private int yrest = 80;
	private int ymar = 140;

	public enum GuiState {gotoStop, atStop,canStop};
	GuiState guistate;
	
	private ImageIcon t = new ImageIcon(this.getClass().getResource("5592cded-1.png"));
	private Image i = t.getImage();

	public MarketTruckGui(MarketTruckAgent c, BuildingAnimationPanel buildPanel){
		myTruck = c;
		this.buildPanel = buildPanel;
	}


	public void draw(Graphics2D g) {
//		g.setColor(Color.red);
//		g.fillRect(xPos, yPos, distance, distance);
		g.drawImage(i, 170, 110, distance, distance, buildPanel);
	}

	public boolean isPresent() {
		return true;
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
		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == xrest) & (yDestination == yrest )) {
			atstop = false;
			myTruck.msgrelease();
		}

		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == xrest) & (yDestination == ymar )) {
			atstop = false;
			myTruck.msgrelease();
		}
	}

	public void GotoCook(){
		xDestination = 200;
		yDestination = 80;
		atstop = true;
	}

	public void GoBack(){
		xDestination = 200;
		yDestination = 140;
		atstop = true;
	}

}
