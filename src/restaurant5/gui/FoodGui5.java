package restaurant5.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;


public class FoodGui5 implements Gui5 {
	
	public enum State {steak, chicken, salad, pizza};
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
        return true;
    }
    
    public void IconOn(){
    	showIcon = true;
    }

    
    public void IconOff(){
    	showIcon = false; 
    }
    
    public void setFood(String choice){
    	if (choice.equals( "Steak")){
    		Food = State.steak;
    	}
    	else if (choice.equals( "Chicken")){
    		Food = State.chicken;
    	}
    	else if (choice.equals("Salad")){
    		Food = State.salad;
    	}
    	else if (choice.equals("Pizza")){
    		Food = State.pizza; 
    	}
    	
    }
    
    public void draw(Graphics2D g) {
        if (showIcon){
    		g.setColor(Color.BLACK);
    		if (Food == State.steak){
            	g.drawString("ST",xPos,yPos);
    		}
    		else if (Food == State.chicken){
            	g.drawString("CH",xPos,yPos);
    		}
    		else if (Food == State.salad){
            	g.drawString("SA",xPos,yPos);
    		}
    		else if (Food == State.pizza){
            	g.drawString("PI",xPos,yPos);
    		}
        }
    	
    }
    


}
