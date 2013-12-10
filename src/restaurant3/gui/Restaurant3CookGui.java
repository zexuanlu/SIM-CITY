package restaurant3.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import java.awt.Image;
import restaurant3.Restaurant3CookRole;

public class Restaurant3CookGui implements Gui{
	
	//Required data
	Restaurant3CookRole agent;
	int xPos, yPos;

	//Dimensions
	private int width = 20;
	private int height = 20;
	
	//Positions
	private int homeX = Restaurant3AnimationPanel.oStandX + ((Restaurant3AnimationPanel.grillX-Restaurant3AnimationPanel.oStandX)/2) + width/2;
	private int homeY = Restaurant3AnimationPanel.kitchenY + 60;
	int xDestination = homeX;
	int yDestination = homeY;
	
	private int fridgeX = Restaurant3AnimationPanel.fridgeX - width;
	private int fridgeY = Restaurant3AnimationPanel.fridgeY - (height/2);
	
	private int grillX = Restaurant3AnimationPanel.grillX;
	private int grillY = Restaurant3AnimationPanel.fridgeY;
	
	private int oStandX = Restaurant3AnimationPanel.oStandX;
	private int oStandY = Restaurant3AnimationPanel.fridgeY;
	
	//Commands
	private enum Command {noCommand, GoToFridge, GoToGrill, GoToOrderStand};	//EDIT
	private Command command = Command.noCommand;
	public boolean isPresent = false;
	
	private ImageIcon img = new ImageIcon(this.getClass().getResource("cook.png"));
	private Image image = img.getImage();
	
	public Restaurant3CookGui(Restaurant3CookRole ck) {
		agent = ck;
		xPos = xDestination;
		yPos = yDestination;
	}

	@Override
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
			if (command==Command.GoToFridge) {
				agent.msgAtFrRelease();
			}
			else if(command == Command.GoToGrill){
				agent.msgAtFrRelease();
			}
			else if(command == Command.GoToOrderStand){
				agent.msgAtFrRelease();
			}
			
			command=Command.noCommand;
		}
		
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.MAGENTA);
		//g.fillRect(xPos, yPos, width, height);
		g.drawImage(image, xPos, yPos, 20, 20, null);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean b){
		isPresent = b;
	}
	
	public void DoGoToFridge(){
		this.xDestination = this.fridgeX;
		this.yDestination = this.fridgeY;
		
		command = Command.GoToFridge;
	}
	
	public void DoGoToGrill(){
		this.xDestination = this.grillX;
		this.yDestination = this.grillY;
		
		command = Command.GoToGrill;
	}
	
	public void DoGoToOrderStand(){
		this.xDestination = this.oStandX;
		this.yDestination = this.oStandY;
		
		command = Command.GoToOrderStand;
	}
}
