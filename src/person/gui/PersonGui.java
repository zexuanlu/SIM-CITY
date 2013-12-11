package person.gui;
import java.util.*; 
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import person.PersonAgent;
import person.Position;
import utilities.Gui;
import utilities.TrafficLightAgent;
import gui.panels.CityAnimationPanel;

public class PersonGui implements Gui{
	public List<simcity.gui.BusGui>busses = new ArrayList<simcity.gui.BusGui>();
	public simcity.gui.CarGui crashCar; 
	private PersonAgent agent = null;
	public TrafficLightAgent light;

	public int xPos, yPos;//default player position
	public int xDestination, yDestination;//default start position
	public int tempX, tempY;
	private boolean arrived; 
	private boolean tempActive;
	public boolean isPresent;
	public ImageIcon img = new ImageIcon(this.getClass().getResource("person.png"));
	public Image pImg = img.getImage();
	CityAnimationPanel cPanel;
	private boolean dead = false; 

	
	public boolean atlight = false;
	
	private int xtl = 329, ytl = 170;
	private int xtr = 440, ytr = 170;
	private int xbl = 330, ybl = 280;
	private int xbr = 440, ybr = 280;

	public PersonGui(PersonAgent agent, int posx, int posy, CityAnimationPanel cap) {
		xPos = posx; 
		yPos = posy; 
	//	light = agent.trafficlight; 
		xDestination = xPos; 
		yDestination = yPos; 
		this.agent = agent;
		this.cPanel = cap;
		arrived = false;
		isPresent = true;
	}
	public PersonGui(PersonAgent agent, TrafficLightAgent tlight){

		light = tlight;
		this.agent = agent;
		arrived = false;
		isPresent = false;
		//atLight = false;

	}
	public void updatePosition() {
		boolean moved = false;
		
		
		if(!atlight){
	    	if (xPos < xDestination && (yPos == 170 || yPos == 280)){
	            xPos++;
	            moved = true;
	    	}
	        else if (xPos > xDestination && (yPos == 170 || yPos == 280)){
	            xPos--;
	            moved = true;
	        }
	
	        if (yPos < yDestination && (xPos == 330 || xPos == 440)){
	            yPos++;
	            moved = true;
	        }
	        else if (yPos > yDestination && (xPos == 440 || xPos == 330)){
	        	yPos--;
	        	moved = true;
	        }
			if(yPos == yDestination && xPos != xDestination && !moved){
				tempActive = true;
				tempX = xDestination;
				tempY = yDestination;
				if(170-yDestination > 0)
					yDestination = 170;
				else
					yDestination = 280;
			}
			else if(yPos != yDestination && xPos == xDestination && !moved){
				tempActive = true;
				tempX = xDestination;
				tempY = yDestination;
				if(330-xDestination > 0)
					xDestination = 330;
				else
					xDestination = 440;
			}
			if(yPos == yDestination && xPos == xDestination && !arrived){
				if(tempActive){
					tempActive = false;
					xDestination = tempX;
					yDestination = tempY;
				}
				else{
					arrived = true;
					agent.msgAtDest(new Position(xPos, yPos));}
			}

		if (!dead){
			if(crashCar != null){
				if(checkCollision()){
					xDestination = xPos; 
					yDestination = yPos; 
					agent.msgDie(); 
					dead = true; 
					return; 
				
				}
			}
		}
			
			
	}
		
		
		
		 if((xPos == xtl && yPos == ytl)&&(xPos < xDestination || yPos < yDestination)){
			 if(agent.toString().equals("Walking Person")){
			if (!atlight){

				light.msgCheckLight(agent);
			}
			atlight = true;
			 }
		 }


		if((xPos == xtr && yPos == ytr)&&(xPos > xDestination || yPos < yDestination)){
			 if(agent.toString().equals("Walking Person")){
			if (!atlight){

				light.msgCheckLight(agent);
			}
			atlight = true;
			 }
		}
		if((xPos == xbl && yPos == ybl)&&(xPos < xDestination || yPos > yDestination)){
			 if(agent.toString().equals("Walking Person")){
			if (!atlight){

				light.msgCheckLight(agent);
			}
			atlight = true;
			 }
		}
		if((xPos == xbr && yPos == ybr)&&(xPos > xDestination || yPos > yDestination)){
			agent.msgAtLight();
			 if(agent.toString().equals("Walking Person")){
			if (!atlight){

				light.msgCheckLight(agent);
			}
			atlight = true;
			 }
		}
		
	}
			
			
			


	
	
	public boolean checkCollision(){
		Rectangle2D.Double player = new Rectangle2D.Double(xPos,yPos,10,10);
		Rectangle2D.Double car = new Rectangle2D.Double(crashCar.xPos, crashCar.yPos, 20, 20);
		if (player.intersects(car)){
			return true;
		}
		
		for (simcity.gui.BusGui bs: busses){
			car = new Rectangle2D.Double(bs.xPos, bs.yPos, 20,20);
			if (player.intersects(car)){
				return true; 
			}
		}
		
		return false;
	
	}
	
	public void talktolight(){
		light.msgCheckLight(agent);
	}

	public void draw(Graphics2D g) {
		g.drawImage(pImg, xPos, yPos, 10, 10, cPanel);
		g.setColor(Color.BLUE);
		g.drawString(agent.getName(), xPos-14, yPos-5);
	}

	public void setAnimationPanel(CityAnimationPanel cap){
		this.cPanel = cap;
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
