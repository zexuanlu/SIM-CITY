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
	public int xPos = 150, yPos = 90, xDestination = 150, yDestination = 90;
	public int overallX, overallY;
	public boolean atstop = false;

	private MarketTruckAgent myTruck;
	BuildingAnimationPanel buildPanel;
	private int xorigin = 150, yorigin = 90;
	private int x1origin  = 510, y1origin = 90;
	private int x1rest = 220;
	private int y1rest = 90;
	private int x2rest = 250, y2rest = 90;
	private int x3rest = 250, y3rest = 90;
	private int x4rest = 540, y4rest = 90;
	private int x5rest = 610, y5rest = 90;
	private int x6rest = 510, y6rest = 60;

	public enum GuiState {gotoStop, atStop,canStop};
	GuiState guistate;
	
	private ImageIcon t = new ImageIcon(this.getClass().getResource("5592cded-1.png"));
	private Image i = t.getImage();

	public MarketTruckGui(MarketTruckAgent c, BuildingAnimationPanel buildPanel, int x){
		this.myTruck = c;
		this.buildPanel = buildPanel;
		if(x ==1 ){
			 this.xPos = 150; 
			 this.yPos = 90; 
			 this.xDestination = 150; 
			 this.yDestination = 90;
		}
		else if( x ==2 ){
			 this.xPos = 510; 
			 this.yPos = 90; 
			 this.xDestination = 510; 
			 this.yDestination = 90;
		}
	}


	public void draw(Graphics2D g) {
		g.drawImage(i, xPos, yPos, distance, distance, buildPanel);
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
				& (xDestination == x1rest) & (yDestination == y1rest )) {
			atstop = false;
			myTruck.msgrelease();
		}

		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == x2rest) & (yDestination == y2rest )) {
			atstop = false;
			myTruck.msgrelease();
		}

		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == x3rest) & (yDestination == y3rest )) {
			atstop = false;
			myTruck.msgrelease();
		}

		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == x4rest) & (yDestination == y4rest )) {
			atstop = false;
			myTruck.msgrelease();
		}

		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == x5rest) & (yDestination == y5rest )) {
			atstop = false;
			myTruck.msgrelease();
		}

		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == x6rest) & (yDestination == y6rest )) {
			atstop = false;
			myTruck.msgrelease();
		}

		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == xorigin) & (yDestination == yorigin )) {
			atstop = false;
			myTruck.msgrelease();
		}

		if (xPos == xDestination &&atstop&& yPos == yDestination
				& (xDestination == x1origin) & (yDestination == y1origin )) {
			atstop = false;
			myTruck.msgrelease();
		}
	}

	public void GotoCook(int x, int y){
		xDestination = x;
		yDestination = y;
		atstop = true;
	}

	public void GoBack(int x){
		if (x == 1){
		xDestination = 150;
		yDestination = 90;
		}
		else if (x ==2 ){
			xDestination = 510;
			yDestination = 90;
		}
		atstop = true;
	}

}
