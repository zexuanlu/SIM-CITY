package restaurant4.gui;

import restaurant4.Restaurant4CookRole;
import agent.Gui;

import java.awt.*;
import java.util.*;

/**
 * This class represents the 
 * customers in the restaurant in the animation
 */
public class Restaurant4CookGui implements Gui{

	private Restaurant4CookRole agent = null;
	private boolean isPresent = true;
	private ArrayList<FoodGui> foods = new ArrayList<FoodGui>();

	Restaurant4AnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;

	public Restaurant4CookGui(Restaurant4CookRole c, Restaurant4AnimationPanel gui){ //HostAgent m) {
		agent = c;
		xPos = 390;
		yPos = 150;
		xDestination = 385;
		yDestination = 150;
		this.gui = gui;
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
		g.setColor(Color.BLUE);
		g.fillRect(xPos, yPos, 20, 20);
		
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
		case "Steak": foods.add(new FoodGui("St", xPos+32, 32 + 20*grillNum)); break;
		case "Chicken" : foods.add(new FoodGui("Ch", xPos+32, 32 + 20*grillNum)); break;
		case "Salad" : foods.add(new FoodGui("Sa", xPos+32, 32 + 20*grillNum)); break;
		case "Pizza" : foods.add(new FoodGui("Pi", xPos+32, 32 + 20*grillNum)); break;
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
