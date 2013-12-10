package restaurant5.gui;


import restaurant5.Restaurant5CookAgent; 
import utilities.Gui;

import java.awt.*;
import java.util.*;

public class Restaurant5CookGui implements Gui {

	
    private int standX = 480; 
    private int standY = 20; 
    
    public boolean isPresent; 
	private int Ygrill; 
	private int YPlate; 
    private int xPos, yPos;//default waiter position
    private int xDestination, yDestination;//default start position
    private Restaurant5CookAgent agent = null;
    //private RestaurantGui gui; 
    
    private class drawGui {
    	int x;
    	int y; 
    	String s; 
    	int grill; 
    	boolean cooking; 
    	boolean plating; 
    }
    private Map<Integer, drawGui> drawguis = new HashMap<Integer, drawGui>();

    public Restaurant5CookGui(Restaurant5CookAgent c){
    	agent = c; 
    	xPos = 480; 
    	yPos = 75;
    	xDestination = 480; 
    	yDestination = 75; 
    }
    
    
    public void draw(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.fillRect(xPos, yPos, 20, 20);
        
		for (Map.Entry<Integer, drawGui> entry: drawguis.entrySet()){
				if (entry.getValue().cooking){
					g.setColor(Color.ORANGE);
		    		if (entry.getValue().s.equals("Belgium")){
		            	g.drawString("BG",entry.getValue().x,entry.getValue().y);
		    		}
		    		else if (entry.getValue().s.equals("Chicken")){
		            	g.drawString("CK",entry.getValue().x,entry.getValue().y);
		    		}
		    		else if (entry.getValue().s.equals("Sassy")){
		            	g.drawString("SA",entry.getValue().x,entry.getValue().y);
		    		}
		    		else if (entry.getValue().s.equals("Chocolate")){
		            	g.drawString("CH",entry.getValue().x,entry.getValue().y);
		    		}
					
				}
			}
        }

    
    public boolean isPresent() {
        return isPresent;
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
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == 560) & (yDestination == Ygrill)) {
        	agent.msgatGrill();
        	int tempgrill = 0;
        	if (yDestination == 30){
        		tempgrill = 1; 
            	drawguis.get(tempgrill).cooking = true; 
        	}
        	else if (yDestination == 80){
        		tempgrill = 2; 
            	drawguis.get(tempgrill).cooking = true; 
        	}
        	else if (yDestination == 130){
        		tempgrill = 3; 
            	drawguis.get(tempgrill).cooking = true; 
        	}
        }
        if (xPos == xDestination && yPos == yDestination && (xDestination == 480) && (yDestination == YPlate)){	
    		for (Map.Entry<Integer, drawGui> entry: drawguis.entrySet()){
    			if (entry.getValue().plating == true){
    				entry.getValue().plating = false; 
    				entry.getValue().cooking = true; 
    			}
    		}

        	agent.msgatPlate();
        }
        else if (xPos == standX && yPos == standY){
        	agent.msgatStand(); 
        }
        
        
    }
    
    public void gotoPlate(){
    	xDestination = 480;
    	yDestination = 75; 
    }
    
    public void gotoPlate(String s, int grillposition, int platenumber){
    	
    	if (platenumber == 1){
    		xDestination = 480; 
    		YPlate = 50; 
    		yDestination = 50; 
    	}
    	
    	if (platenumber == 2){
    		YPlate = 75; 
    	xDestination = 480;
    	yDestination = 75; 
    	}
    	if (platenumber == 3){
    		YPlate = 105; 
    		xDestination = 480;
    		yDestination = 105; 
    	}
    	
		for (Map.Entry<Integer, drawGui> entry: drawguis.entrySet()){
			if (entry.getValue().grill== grillposition && entry.getValue().cooking){
				entry.getValue().x = 455; 
				entry.getValue().y = YPlate; 
				entry.getValue().cooking = false; 
				entry.getValue().plating = true; 
			}
		}
    }
    public void gotoGrill(int grillposition){
    	if (grillposition == 1){
    		xDestination = 560; 
    		Ygrill = 30; 
    	}
    	else if (grillposition == 2){
    		xDestination = 560; 
    		Ygrill = 80; 
    	}
    	else if (grillposition == 3){
    		xDestination = 560; 
    		Ygrill = 130; 
    	}
    	xDestination = 560; 
    	yDestination = Ygrill; 
    	
    }
    
    public void pickedupOrder(int grillposition){
    	drawguis.get(grillposition).cooking = false; 
    }
    
    public void gotoGrill(int grillposition, String choice){
    	if (grillposition == 1){
    		xDestination = 560; 
    		Ygrill = 30; 
    	}
    	else if (grillposition == 2){
    		xDestination = 560; 
    		Ygrill = 80; 
    	}
    	else if (grillposition == 3){
    		xDestination = 560; 
    		Ygrill = 130; 
    	}
    	xDestination = 560; 
    	yDestination = Ygrill; 
    	
    	drawGui g = new drawGui();
    	g.x = xDestination; 
    	g.y = yDestination; 
    	g.s = choice; 
    	g.grill = grillposition; 
    	g.cooking = false; 
    	drawguis.put(grillposition, g);
    	
    }
    
    public void DoGoToStand(){
	  xDestination = standX; 
	  yDestination = standY; 
    }

}
