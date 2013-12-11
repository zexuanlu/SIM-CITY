package restaurant2.gui;


import restaurant2.Restaurant2CookRole;
import restaurant2.Restaurant2CustomerRole;
import restaurant2.Restaurant2HostRole;
import utilities.Gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import person.PersonAgent;

public class Restaurant2CookGui implements Gui {

    private Restaurant2CookRole agent = null;
    
    public Map<Integer, String> grillLabels = new HashMap<Integer, String>();//holds grill label text
    public Map<Integer, String> stationLabels = new HashMap<Integer, String>();//holds cook station label text
    
    private int xPos = 400, yPos = 30;//default cook position
    private int xDestination = 400, yDestination = 30;//default start position
    public static final int xCS = 550; //cook station x position
    public static final int xGrillPos = 450;
    public static final int yGrillPos = 30;
    public static final int yGrillPos2 = 60;
    public static final int yGrillPos3 = 90;
    public static final int yGrillPos4 = 120;
    private boolean arrived;
    private boolean arrivedAtHome;
    private boolean isPresent = false;
	private String orderText = " "; 
	private ImageIcon img = new ImageIcon(this.getClass().getResource("cook.png"));
	private Image ck = img.getImage();
    
    public Restaurant2CookGui(Restaurant2CookRole agent) {
        this.agent = agent;
        grillLabels.put(1, "1");
        grillLabels.put(2, "2");
        grillLabels.put(3, "3");
        grillLabels.put(4, "4");
        stationLabels.put(1, " ");
        stationLabels.put(2, " ");
        stationLabels.put(3, " ");
        stationLabels.put(4, " ");
        arrived = false;
        //arrivedAtHome = false;
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
        	&& ((xDestination == xGrillPos) && (yDestination == yGrillPos) 
        	||(xDestination == xGrillPos) && (yDestination == yGrillPos2) 
        	||(xDestination == xGrillPos) && (yDestination == yGrillPos3) 
        	|| (xDestination == xGrillPos) && (yDestination == yGrillPos4) )
        	&& !arrived) 
        {
           agent.msgAtGrill();
           arrived = true;
        }
        if (xPos == xDestination && yPos == yDestination
           && (xPos == xCS ) )
        {
           agent.msgAtCS();
        
        }
        if(xPos == 600 && yPos == 30)
        {
        	//agent.msgBackHome();
        }
    }
	public void changeText(String text)
	{
		orderText = text;
	}
	public void changeGrillLabel(int position, String text)
	{
		grillLabels.put(position, text);
	}
	public void changeStationLabel(int position, String text)
	{
		stationLabels.put(position, text);
	}
    public void draw(Graphics2D g) {
       // g.fillRect(xPos, yPos, 20, 20);
        g.drawImage(ck, xPos, yPos, 20, 20, null);
        g.setColor(Color.MAGENTA);
        g.drawString(((PersonAgent)agent.getPerson()).getName(), xPos-14, yPos+30);
        g.drawString(orderText, xPos, yPos);
		for(Map.Entry<Integer, String> entry: grillLabels.entrySet())
		{
			g.drawString(entry.getValue(), xGrillPos+10, (int)(yGrillPos*entry.getKey())+20);
		}
		for(Map.Entry<Integer, String> entry: stationLabels.entrySet())
		{
			g.drawString(entry.getValue(), xCS+10, (int)(yGrillPos*entry.getKey())+20);
		}
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void DoTakeToCookStation(int position, String order) {

    	if(position == 1)
    	{
    		xDestination = xCS;
    		yDestination = yGrillPos;
    	}
    	else if(position == 2)
    	{
    		xDestination = xCS;
    		yDestination = yGrillPos2;
    	}
    	else if(position == 3)
    	{
    		xDestination = xCS;
    		yDestination = yGrillPos3;    		
    	}
    	else if(position == 4)
    	{
    		xDestination = xCS;
    		yDestination = yGrillPos4;    	
    	}
    }

    public void DoLeaveGrill() {
        xDestination = 400;
        yDestination = 30;
        arrived = false;
    }
    
    public void DoGoToGrill(int position){
    	if(position == 1)
    	{
    		xDestination = xGrillPos;
    		yDestination = yGrillPos;
    	}
    	else if(position == 2)
    	{
    		xDestination = xGrillPos;
    		yDestination = yGrillPos2;
    	}
    	else if(position == 3)
    	{
    		xDestination = xGrillPos;
    		yDestination = yGrillPos3;    		
    	}
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
	public void setPresent(boolean tf) {
		isPresent = tf;
	}
}
