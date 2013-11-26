package restaurant.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import restaurant.Restaurant1CookRole;

public class CookGui implements Gui{

	private Restaurant1CookRole cook = null;
	private boolean plating = false;
	private ImageIcon img = new ImageIcon("image/cook.jpeg");
	private ImageIcon img1 = new ImageIcon("image/refri.jpeg");
	private Image i = img.getImage();
	private Image j = img1.getImage();
	private int xPos = 540;
	private int yPos = 160;
	private int distance = 20;
	private int xfood = 540;
	private int yfood = 150;
	private int xRefri = 500;
	private int xRefriPic = 480;
	private int yRefri = 160;
	private int xDestination = 540;
	private int yDestination = 160;
	private int xPlate = 540;
	private int yPlate = 210;
	private String food = "";
	private String carryFood = "";
	RestaurantGui gui = null;
	private boolean cooking = false;
	private boolean refrigerator = false;
	
	
	
	public CookGui(Restaurant1CookRole cook, RestaurantGui gui){
		this.cook = cook;
		this.gui = gui;
	}
	
	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		
		if(xPos == xDestination && refrigerator && yPos == yDestination
        		& (xDestination == xRefri) & (yDestination == yRefri)){
			refrigerator = false;
			cook.msgAR();
		}
		if(xPos == xDestination && cooking && yPos == yDestination
        		& (xDestination == xfood) & (yDestination == yRefri)){
			cooking = false;
			cook.msgAR();
		}
		if(xPos == xDestination && plating && yPos == yDestination
        		& (xDestination == xPlate) & (yDestination == yPlate)){
			plating = false;
			cook.msgAR();
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(i,xPos, yPos, distance, distance, gui);
		g.drawImage(j,xRefriPic, yRefri, distance, distance, gui);
		g.setColor(Color.black);
        g.drawString(food, xfood, yfood);
        g.drawString(carryFood, xPos, yPos);
		// TODO Auto-generated method stub
	}
	
	public void DoGotoPlatingArea(){
		xDestination = 540;
		yDestination = 210;
		plating = true;
	}
	
	public void DoGotoRefri(){
		xDestination = 500;
		yDestination = 160;
		refrigerator = true;
	}
	
	public void DoGotoCookingArea(){
		xDestination = 540;
		yDestination = 160;
		cooking = true;
	}
	
    public void showfood(String order){
    	food = order;
    }

    public void hidefood(){
    	food = "";
    }

    public void showCarryFood(String order){
    	carryFood = order;
    }
    
    public void hideCarryFoood(){
    	carryFood ="";
    }
    
	@Override
	public boolean isPresent() {
		return true;
		// TODO Auto-generated method stub
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zou() {
		// TODO Auto-generated method stub
		
	}

}
