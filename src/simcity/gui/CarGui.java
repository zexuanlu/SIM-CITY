package simcity.gui;
import simcity.BusRole;
import simcity.astar.*; 
import simcity.gui.BusGui.GuiState;
import simcity.CarAgent; 
import utilities.Gui;

import java.util.*; 
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

	
public class CarGui implements Gui {
        
        int scale = 20;
        int deadpositionX = 720;
        int deadpositionY = 460;
        public boolean isPresent;
        boolean collided = false; 
        List<Dimension> moves = new ArrayList<Dimension>();
        boolean NorthSouth;
        boolean EastWest;
        boolean deadpos;
        public int xPos, yPos, xDestination, yDestination;
        public int overallX, overallY;
        
        private CarAgent myCar;
        
        public enum GuiState {gotoStop, atStop,canStop};
        GuiState guistate;
        
        public CarGui(CarAgent c, int x, int y){
                myCar = c;
            xPos = x;
            yPos = y;
            xDestination = 600;
            yDestination = 400;
            EastWest = true;
            deadpos = true;
        }
        
        
        public void draw(Graphics2D g) {
        	

            g.setColor(Color.RED);

        	if (collided ){
        		g.setColor(Color.BLACK);
        	}
        	g.drawString(myCar.myPerson.getName(), xPos-10, yPos);
            
                if (!deadpos){
                        if (EastWest){
                                g.fillRect(xPos, yPos, 20, 20);
                        }
                        else if (NorthSouth){
                                g.fillRect(xPos, yPos, 20, 20);
                        }
                }
    }
        
        public boolean isPresent() {
        return true;
    }
        
        public void Collide(){
        	xDestination = xPos; 
        	yDestination = yPos; 
        	collided = true; 
        }
        
        public void gotoDeadPos(){
            guistate = GuiState.atStop;
       	    myCar.deadPos(deadpositionX, deadpositionY);
            myCar.msgatDestination();
            xDestination = deadpositionX;
            yDestination = deadpositionY;
            atPosition(deadpositionX, deadpositionY);
       }
    
        
    public void updatePosition() {
            //check orientation of the bus
               if (xPos == xDestination && yPos == yDestination && guistate == GuiState.gotoStop){
                    guistate = GuiState.canStop;
                    myCar.msgatSlot();
                    return;
            }
            
            if (xPos == xDestination && yPos != yDestination){
                    NorthSouth = false;
                    EastWest = true; //y oriented
            }
            else if (yPos == yDestination && xPos != xDestination){
                    EastWest = false;
                    NorthSouth = true;
            }
            
            
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
   // if (xPos == xDestination && yPos == yDestination && guistate == GuiState.canStop){
        if (xPos == overallX && yPos == overallY && guistate == GuiState.canStop && !collided){
                guistate = GuiState.atStop;
                System.out.println("At Place where I'm supposed to be at/ dead position");
                myCar.deadPos(deadpositionX, deadpositionY);
                myCar.msgatDestination();
                xDestination = deadpositionX;
                yDestination = deadpositionY;
                atPosition(deadpositionX, deadpositionY);
        }
    }
    
    public void atPosition(int x, int y){
            if (deadpos){
                    deadpos = false;
            }
            else {
                    deadpos = true;
            }
            xPos = x;
            yPos = y;
            xDestination = x;
            yDestination = y; 
    }
    
        public void moveto(int x, int y){
            guistate = GuiState.gotoStop;
                xDestination = x*scale;
                yDestination = y*scale;
        }
    
    
}
