package restaurant2.gui;


import restaurant1.Restaurant1SDWaiterRole;
import restaurant1.Restaurant1WaiterRole;
import restaurant2.Restaurant2CustomerRole;
import restaurant2.Restaurant2HostRole;
import restaurant2.Restaurant2SDWaiterRole;
import restaurant2.Restaurant2WaiterRole;
import restaurant2.interfaces.Restaurant2Waiter;
import utilities.Gui;

import java.awt.*;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import person.PersonAgent;

public class Restaurant2WaiterGui implements Gui {

	private Restaurant2Waiter agent = null;

	private int xPos = -20, yPos = -20;//default waiter position
	private int xDestination = 20, yDestination = 20;//default start position

	public static final int xTable = 50;
	public static final int yTable = 400;
	public static final int xTable2 = 150;
	public static final int yTable2 = 400;
	public static final int xTable3 = 250;
	public static final int yTable3 = 400;
	public static final int xTable4 = 350;
	public static final int yTable4 = 400;
	public static final int xCook = 350;
	public static final int yCook = 30;

	private boolean arrived = false;
	private boolean isPresent = false;
	private ImageIcon icon = new ImageIcon("src/resources/white.jpg");
	private Image image = icon.getImage();
	private int skin; 
	private String text = " "; 
	
	public Restaurant2WaiterGui(Restaurant2Waiter agent) {
		//excuse the worthless icon param for now, will be useful when graphics are required
		this.agent = agent;
		//this.icon = icon;
		Random color = new Random();
		skin = color.nextInt(4);

	}
	public void changeText(String text)
	{
		this.text = text;
	}
	public void changeIcon(String path)
	{
		if(path.equals("clear"))
		{
			path = "src/resources/white.jpg";
			icon = new ImageIcon(path);
			image = icon.getImage();
		}
		else
		{
			path = "src/resources/"+path+".jpg";
			System.out.println(path);
			icon = new ImageIcon(path);
			image = icon.getImage();
		}

	}
	public void updatePosition() {
		if (xPos < xDestination)
			xPos+=2;
		else if (xPos > xDestination)
			xPos-=2;

		if (yPos < yDestination)
			yPos+=2;
		else if (yPos > yDestination)
			yPos-=2;

		if (!arrived && xPos == xDestination && yPos == yDestination
			&& ((xDestination == xTable + 20) && (yDestination == yTable - 20) 
			||(xDestination == xTable2 + 20) && (yDestination == yTable2 - 20) 
			|| (xDestination == xTable3 + 20) && (yDestination == yTable3 - 20)
			|| (xDestination == xTable4 + 20) && (yDestination == yTable4 - 20) ) ) 
		{
			agent.msgAtTable();
		}
		if(xPos == -20 && yPos == -20)
		{
			arrived = false;
			agent.msgBackHome();
			//agent.getHost().msgAvailable(agent);
		}
		if(xPos == 550)
		{
			agent.msgAtCook();
		}
	}

	public void draw(Graphics2D g) {
		switch (skin)//this is simply to identify individual waiters and what their current doings are
		{
		case 0:
			g.setColor(Color.BLACK);
			break;
		case 1:
			g.setColor(Color.BLACK);
			break;
		case 2:
			g.setColor(Color.BLACK);
			break;
		case 3:
			g.setColor(Color.BLACK);
			break;
		}
		g.fillRect(xPos, yPos, 20, 20);
		g.drawString(text, xPos, yPos);	
		
		if(agent instanceof Restaurant2WaiterRole){
        	g.drawString(((PersonAgent)((Restaurant2WaiterRole)agent).getPerson()).getName(), xPos-14, yPos+30);
        }
        else if(agent instanceof Restaurant2SDWaiterRole){
        	g.drawString(((PersonAgent)((Restaurant2SDWaiterRole)agent).getPerson()).getName(), xPos-14, yPos+30);
        }

	}

	public boolean isPresent() {
		return isPresent;
	}

	public void DoBringToTable(Restaurant2CustomerRole customer, int table) {
		
		System.out.println("seating"+ customer+" at" + table);
		if(table == 1)
		{
			xDestination = xTable + 20;
			yDestination = yTable - 20;
		}
		else if(table == 2)
		{
			xDestination = xTable2+20;
			yDestination = yTable2-20;
		}
		else if(table == 3)
		{
			xDestination = xTable3+20;
			yDestination = yTable3-20;    		
		}
		else if(table == 4)
		{
			xDestination = xTable4+20;
			yDestination = yTable4-20;  
		}
	}
	public void DoGoTakeOrder(Restaurant2CustomerRole customer, int table){
		
		if(table == 1)
		{
			xDestination = xTable + 20;
			yDestination = yTable - 20;
		}
		else if(table == 2)
		{
			xDestination = xTable2+20;
			yDestination = yTable2-20;
		}
		else if(table == 3)
		{
			xDestination = xTable3+20;
			yDestination = yTable3-20;    		
		}
		else if(table == 4)
		{
			xDestination = xTable4+20;
			yDestination = yTable4-20;  
		}
	}
	public void DoTakeToCook(){
		
		xDestination = xCook;
		yDestination = yCook;
	}
	public void DoLeaveCustomer() {
		
		xDestination = -20;
		yDestination = -20;
	}
	public void DoTakeFoodToCustomer(int table){
		
		if(table == 1)
		{
			xDestination = xTable + 20;
			yDestination = yTable - 20;
		}
		else if(table == 2)
		{
			xDestination = xTable2+20;
			yDestination = yTable2-20;
		}
		else if(table == 3)
		{
			xDestination = xTable3+20;
			yDestination = yTable3-20;    		
		}
		else if(table == 4)
		{
			xDestination = xTable4+20;
			yDestination = yTable4-20;  
		}

	}
	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}
	public void setPresent(boolean tf){
		isPresent = tf;
	}
}
