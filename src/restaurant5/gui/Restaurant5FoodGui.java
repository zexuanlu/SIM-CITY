package restaurant5.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

import utilities.Gui;


public class Restaurant5FoodGui implements Gui {
	
	public boolean isPresent; 
	public enum State {belgium,sassy,chocolate,chicken};
	private State Food; 
	
	public int xPos, yPos; 
	public int xDestination, yDestination; 
	
    boolean showIcon = false; //this boolean is responsible for whether or not the icon shows
	
	
	public void updatePosition(){
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
	}

    public boolean isPresent() {
        return isPresent;
    }
    
    public void IconOn(){
    	showIcon = true;
    }

    
    public void IconOff(){
    	showIcon = false; 
    }
    
    public void setFood(String choice){
    	if (choice.equals( "Belgium")){
    		Food = State.belgium;
    	}
    	else if (choice.equals( "Chicken")){
    		Food = State.chicken;
    	}
    	else if (choice.equals("Sassy")){
    		Food = State.sassy;
    	}
    	else if (choice.equals("Chocolate")){
    		Food = State.chocolate; 
    	}
    	
    }
    
    public void draw(Graphics2D g) {
        if (showIcon){
    		g.setColor(Color.BLACK);
    		if (Food == State.belgium){
            	g.drawString("BG",xPos,yPos);
    		}
    		else if (Food == State.chicken){
            	g.drawString("CK",xPos,yPos);
    		}
    		else if (Food == State.sassy){
            	g.drawString("SA",xPos,yPos);
    		}
    		else if (Food == State.chocolate){
            	g.drawString("CH",xPos,yPos);
    		}
        }
    	
    }
    


}
