package restaurant4.gui;

import restaurant4.Restaurant4CookRole;
import utilities.Gui;

import java.awt.*;
import java.util.*;

import javax.swing.ImageIcon;

import person.PersonAgent;

/**
 * This class represents the 
 * customers in the restaurant in the animation
 */
public class Restaurant4CookGui implements Gui{

	private Restaurant4CookRole agent = null;
	public boolean isPresent = false;

	private ArrayList<FoodGui> foods = new ArrayList<FoodGui>();

	private int xPos, yPos;
	private int xDestination, yDestination;
	
	//public ImageIcon img = new ImageIcon(this.getClass().getResource("cook.png"));
	//public Image image = img.getImage();

	public Restaurant4CookGui(Restaurant4CookRole c){ //HostAgent m) {
		this.agent = c;
		xPos = 440;
		yPos = 250;
		xDestination = 435;
		yDestination = 250;
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

		if (xPos == xDestination && yPos == yDestination) {
			
		}

	}

	public void draw(Graphics2D g) {
		//g.fillRect(xPos, yPos, 20, 20);
		//g.drawImage(image, xPos, yPos, 20, 20, null);
		g.drawString(((PersonAgent)agent.getPerson()).getName(), xPos-14, yPos+30);
		
		for(FoodGui fg : foods){
			g.setColor(Color.BLACK);
			g.drawString(fg.type, fg.xPos, fg.yPos);
		}
	}

	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToPos(int x, int y){
		xDestination = x;
		yDestination = y;
	}

	public void removeFood(String choice, int table){
		for(FoodGui fg : foods){
			if(fg.type.equals(choice.substring(0, 2)) && fg.table == table){
				foods.remove(fg);
				return;
			}
		}
	}
	
	public void DoPrepFood(String choice, int table){
		for(FoodGui fg : foods){
			if(fg.type.equals(choice.substring(0, 2)) && fg.table == table){
				fg.xPos -= 55;
				return;
			}
		}

	}
	public void DoCookFood(String choice, int grillNum, int table){
		switch (choice){
			case "Shrimp": foods.add(new FoodGui("Sh", xPos+32, yPos - 80 + 20*grillNum)); break;
			case "Scallops" : foods.add(new FoodGui("Sc", xPos+32, yPos - 80 + 20*grillNum)); break;
			case "Lobster" : foods.add(new FoodGui("Lo", xPos+32, yPos - 80 + 20*grillNum)); break;
			case "Crab" : foods.add(new FoodGui("Cr", xPos+32, yPos - 80 + 20*grillNum)); break;
		}
		foods.get(foods.size()-1).table = table;
	}
	
	class FoodGui{
		int xPos;
		int yPos;
		int table;
		String type;
		FoodGui(String type, int xPos, int yPos){
			this.type = type;
			this.xPos = xPos;
			this.yPos = yPos;
		}
	}
}
