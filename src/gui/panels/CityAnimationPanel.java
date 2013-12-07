

package gui.panels;
/**
 * This panel is where the main City animations
 * should be displayed
 * 
 */

import javax.swing.*; 

import market.gui.MarketTruckGui; 
import market.gui.MarketAnimationPanel;
import bank.gui.BankAnimationPanel;
import person.gui.PersonGui;
import resident.gui.ApartmentAnimationPanel;
import resident.gui.HomeOwnerGui;
import resident.gui.HouseAnimationPanel;
import restaurant1.gui.Restaurant1AnimationPanel;
import restaurant6.gui.Restaurant6AnimationPanel;
import simcity.CarAgent;
import simcity.gui.BusGui;
import simcity.gui.BusStopGui;
import simcity.gui.CarGui;
import simcity.gui.PassengerGui;
import utilities.Gui;
import person.gui.PersonGui;
import person.Location;
import person.Restaurant;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CityAnimationPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener{

	private BuildingAnimationPanel BuildPanel;
	public BankAnimationPanel bankPanel = new BankAnimationPanel();
	public MarketAnimationPanel marketPanel = new MarketAnimationPanel();
	public Restaurant1AnimationPanel rest1Panel = new Restaurant1AnimationPanel();
	public Restaurant6AnimationPanel rest6Panel = new Restaurant6AnimationPanel();
	
	// Creating the apartment panels
//	public ApartmentAnimationPanel apt1Panel = new ApartmentAnimationPanel(5);
//	public ApartmentAnimationPanel apt2Panel = new ApartmentAnimationPanel(6);
//	public ApartmentAnimationPanel apt3Panel = new ApartmentAnimationPanel(7);
//	public ApartmentAnimationPanel apt4Panel = new ApartmentAnimationPanel(8);
//	public ApartmentAnimationPanel apt5Panel = new ApartmentAnimationPanel(9);
//	public ApartmentAnimationPanel apt6Panel = new ApartmentAnimationPanel(10);
//	public ApartmentAnimationPanel apt7Panel = new ApartmentAnimationPanel(11);
//	public ApartmentAnimationPanel apt8Panel = new ApartmentAnimationPanel(12);
//	public ApartmentAnimationPanel apt9Panel = new ApartmentAnimationPanel(13);
//	public ApartmentAnimationPanel apt10Panel = new ApartmentAnimationPanel(14);
//	public ApartmentAnimationPanel apt11Panel = new ApartmentAnimationPanel(15);
//	public ApartmentAnimationPanel apt12Panel = new ApartmentAnimationPanel(16);
//	public ApartmentAnimationPanel apt13Panel = new ApartmentAnimationPanel(17);
//	public ApartmentAnimationPanel apt14Panel = new ApartmentAnimationPanel(18);
//	public ApartmentAnimationPanel apt15Panel = new ApartmentAnimationPanel(19);
//	public ApartmentAnimationPanel apt16Panel = new ApartmentAnimationPanel(20);
	
	public HouseAnimationPanel house1Panel = new HouseAnimationPanel(1);
	public HouseAnimationPanel house2Panel = new HouseAnimationPanel(2);
	public HouseAnimationPanel house3Panel = new HouseAnimationPanel(3);
	public HouseAnimationPanel house4Panel = new HouseAnimationPanel(4);
	private List<JPanel> panels = new ArrayList<JPanel>();
    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
    private List<radialButton> buttons = Collections.synchronizedList(new ArrayList<radialButton>());
    private List<HouseAnimationPanel> homes = new ArrayList<HouseAnimationPanel>();
    public List<ApartmentAnimationPanel> apartments = new ArrayList<ApartmentAnimationPanel>();
    private Map<String, Location> locations = new HashMap<String, Location>();
    private Image bufferImage;
    private Dimension bufferSize;
	private String title = " City Animation ";
	state s = state.none;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int BUILDINGSIZE = 60;
	public static final int APARTMENTSIZE = 10;
	Timer timer;
	
	enum state {none, bank, market, restaurant1, house1, house2, house3, house4, apartment}
	//Buttons for buildings
	Rectangle2D bank = new Rectangle2D.Double(140, 160, BUILDINGSIZE, BUILDINGSIZE);
	Rectangle2D market = new Rectangle2D.Double(220, 160, BUILDINGSIZE, BUILDINGSIZE);
	Rectangle2D restaurant1 = new Rectangle2D.Double(220,80, BUILDINGSIZE, BUILDINGSIZE);
	Rectangle2D house1 = new Rectangle2D.Double(340, 160, BUILDINGSIZE, BUILDINGSIZE);
	Rectangle2D house2 = new Rectangle2D.Double(340, 80, BUILDINGSIZE, BUILDINGSIZE);
	Rectangle2D house3 = new Rectangle2D.Double(450, 160, BUILDINGSIZE, BUILDINGSIZE);
	Rectangle2D house4 = new Rectangle2D.Double(540, 160, BUILDINGSIZE, BUILDINGSIZE);
	
	private List<Rectangle2D> apartmentComplex1Components = Collections.synchronizedList(new ArrayList<Rectangle2D>());
	
	// First apartment complex
	Rectangle2D apartmentComplex1 = new Rectangle2D.Double(340, 280, BUILDINGSIZE+10, BUILDINGSIZE+10);
//	Rectangle2D apartment1 = new Rectangle2D.Double(340, 280, APARTMENTSIZE, APARTMENTSIZE);
//	Rectangle2D apartment2 = new Rectangle2D.Double(340, 320, APARTMENTSIZE, APARTMENTSIZE);
//	Rectangle2D apartment3 = new Rectangle2D.Double(380, 280, APARTMENTSIZE, APARTMENTSIZE);
//	Rectangle2D apartment4 = new Rectangle2D.Double(380, 320, APARTMENTSIZE, APARTMENTSIZE);
	
	// Second apartment complex
	/*Rectangle2D apartmentComplex2 = new Rectangle2D.Double(340, 280, BUILDINGSIZE+10, BUILDINGSIZE+10);
	Rectangle2D apartment5 = new Rectangle2D.Double(340, 280, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment6 = new Rectangle2D.Double(340, 320, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment7 = new Rectangle2D.Double(380, 280, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment8 = new Rectangle2D.Double(380, 320, BUILDINGSIZE/2, BUILDINGSIZE/2);
	
	// Third apartment complex
	Rectangle2D apartmentComplex3 = new Rectangle2D.Double(340, 280, BUILDINGSIZE+10, BUILDINGSIZE+10);
	Rectangle2D apartment9 = new Rectangle2D.Double(340, 280, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment10 = new Rectangle2D.Double(340, 320, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment11 = new Rectangle2D.Double(380, 280, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment12 = new Rectangle2D.Double(380, 320, BUILDINGSIZE/2, BUILDINGSIZE/2);*/
	
	
	
	public CityAnimationPanel() {
		//PANEL SETUP
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBorder(BorderFactory.createTitledBorder(title));
		
		// Adding apartment buildings to the animation panel
		for (int j = 280; j < 350; j = j + 20) {
			for (int k = 340; k < 410; k = k + 20) {
				apartmentComplex1Components.add(new Rectangle2D.Double(k, j, APARTMENTSIZE, APARTMENTSIZE));
			}
		}
		
		//Panel size initiations
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		homes.add(house1Panel);
		homes.add(house2Panel);
		homes.add(house3Panel);
		homes.add(house4Panel);
		
//		apartments.add(apt1Panel);
//		apartments.add(apt2Panel);
//		apartments.add(apt3Panel);
//		apartments.add(apt4Panel);
//		apartments.add(apt5Panel);
//		apartments.add(apt6Panel);
//		apartments.add(apt7Panel);
//		apartments.add(apt8Panel);
//		apartments.add(apt9Panel);
//		apartments.add(apt10Panel);
//		apartments.add(apt11Panel);
//		apartments.add(apt12Panel);
//		apartments.add(apt13Panel);
//		apartments.add(apt14Panel);
//		apartments.add(apt15Panel);
//		apartments.add(apt16Panel);
		
		// Creating the apartment animation panel
		for (int i = 5; i < 70; ++i) {
			apartments.add(new ApartmentAnimationPanel(i));
		}
		
		panels.add(bankPanel);
		panels.add(marketPanel);
		panels.add(rest1Panel);
		
    	setSize(WIDTH, HEIGHT);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	timer = new Timer(8, this );
    	timer.start();
	}
	
	public void mouseClicked(MouseEvent me){
		   if (me.getButton() == 1 && bank.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));
			   BuildPanel.repaint();
			   BuildPanel.add(bankPanel);
		   }
		   else if(me.getButton() == 1 && market.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(marketPanel);
		   }
		   else if(me.getButton() == 1 && restaurant1.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(rest1Panel);
		   }
		   else if(me.getButton() == 1 && house1.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(house1Panel);
		   }
		   else if(me.getButton() == 1 && house2.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(house2Panel);
		   }
		   else if(me.getButton() == 1 && house3.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(house3Panel);
		   }
		   else if(me.getButton() == 1 && house4.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(house4Panel);
		   }
		   else {
			   for (Rectangle2D apt : apartmentComplex1Components) {
				   if (me.getButton() == 1 && apt.contains(me.getX(), me.getY())) {
					   if (BuildPanel.getComponentCount() > 0) {
						   BuildPanel.remove(BuildPanel.getComponent(0));
					   }
					   BuildPanel.repaint();
					   BuildPanel.add(apartments.get(apartmentComplex1Components.indexOf(apt)));
				   }
 			   }
		   }
		  /* else if(me.getButton() == 1 && apartment1.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(apt1Panel);
		   }
		   else if(me.getButton() == 1 && apartment2.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(apt2Panel);
		   }
		   else if(me.getButton() == 1 && apartment3.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(apt3Panel);
		   }
		   else if(me.getButton() == 1 && apartment4.contains(me.getX(), me.getY())){
			   if(BuildPanel.getComponentCount() > 0)
			   		BuildPanel.remove(BuildPanel.getComponent(0));			   
			   BuildPanel.repaint();
			   BuildPanel.add(apt4Panel);
		   }*/
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timer)
			repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
    	
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WIDTH, HEIGHT );

        //Here is the table
        g2.setColor(Color.GRAY);
        g2.fill(bank);
        g2.fill(apartmentComplex1);
        g2.fill(house1);
        g2.fill(house2);
        g2.fill(house3);
        g2.fill(house4);
        g2.fill(market);
        g2.fill(restaurant1);

        //draw out the roads
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(280, 0, 60, 480);
        g2.fillRect(0, 220, 640, 60);
        
        
        //Hover Text
    	g2.setColor(Color.BLACK);
        if(s == state.bank){
        	g2.drawString("Bank", 155, 195);
        }
        else if(s == state.market){
        	g2.drawString("Market", 232, 195);
        }
        else if(s == state.restaurant1){
        	g2.drawString("Restaurant 1", 217, 115);
        }
        else if(s == state.house1){
        	g2.drawString("House 1", 345, 195);
        }
        else if(s == state.house2){
        	g2.drawString("House 2", 345, 115);
        }
        else if(s == state.house3){
        	g2.drawString("House 3", 455, 195);
        }
        else if(s == state.house4){
        	g2.drawString("House 4", 545, 195);
        }
        else if(s == state.apartment){
        	g2.drawString("Apartments", 335, 320);
//            g2.fill(apartment1);
//            g2.fill(apartment2);
//            g2.fill(apartment3);
//            g2.fill(apartment4);
        	for (Rectangle2D apt : apartmentComplex1Components) {
        		g2.fill(apt);
        	}
        }
        //Update the position of the guis in the various buildings
        for(Gui gui : marketPanel.guis){
        	gui.updatePosition();
        }
        for(Gui gui : bankPanel.guis){
        	gui.updatePosition();
        }
        for(Gui gui : rest1Panel.guis){
        	gui.updatePosition();
        }
//        for(Gui gui : apt1Panel.guis){
//        	gui.updatePosition();
//        }
//        for(Gui gui : apt2Panel.guis){
//        	gui.updatePosition();
//        }
//        for(Gui gui : apt3Panel.guis){
//        	gui.updatePosition();
//        }
//        for(Gui gui : apt4Panel.guis){
//        	gui.updatePosition();
//        }
        for (ApartmentAnimationPanel apt : apartments) {
        	for (Gui gui : apt.guis) {
        		gui.updatePosition();
        	}
        }
        for(Gui gui : house1Panel.guis){
        	gui.updatePosition();
        }
        for(Gui gui : house2Panel.guis){
        	gui.updatePosition();
        }
        for(Gui gui : house3Panel.guis){
        	gui.updatePosition();
        }
        for(Gui gui : house4Panel.guis){
        	gui.updatePosition();
        }
        for(JPanel p : panels){
        	p.repaint();
        }
        for(JPanel p : homes){
        	p.repaint();
        }
        for(JPanel p : apartments){
        	p.repaint();
        }
        
        synchronized(guis){
	        for(Gui gui : guis) {
	                gui.updatePosition();
	        }
        }

        synchronized(guis){
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
        }
        synchronized(buttons){
        	for(radialButton button : buttons){
        		if(!button.type.equals("Border"))
        			g2.setColor(Color.WHITE);
        		else
        			g2.setColor(Color.RED);
        		g2.fill(button.button);
        		g2.setColor(Color.BLACK);
        		if(button.type.equals("Close") || button.type.equals("Open"))
        			g2.drawString(button.type, (int)button.button.getMinX()+5, (int)button.button.getCenterY()+5);
        		else if(button.type.equals("Empty Stock"))
        			g2.drawString(button.type, (int)button.button.getMinX()-15, (int)button.button.getCenterY()+5);
        	}
        }
    }

	public void startCar(CarAgent c){
		c.startThread();
	}
    public void addGui(BusGui gui) {
        guis.add(gui);
    }
    
    public void addGui(BusStopGui gui){
    	guis.add(gui);
    }
    
    public void addGui(PassengerGui gui){
    	guis.add(gui);
    }
    
    public void addGui(CarGui gui){
    	guis.add(gui);
    	System.out.println("CAR GUI ADDGUI CITY ANIM PANEL");
    }
    
    public void addGui(PersonGui gui) {
        guis.add(gui);
    }

    public void addGui(MarketTruckGui gui){
    	guis.add(gui);
    }
    
    public HouseAnimationPanel getHouseGui(int houseNumber){
    	for(HouseAnimationPanel h : homes){
    		if(h.houseNumber == houseNumber){
    			return h;
    		}
    	}
    	return null;
    }
    
    public ApartmentAnimationPanel getAptGui(int aptNum){
    	for(ApartmentAnimationPanel a : apartments){
    		if(a.aptNum == aptNum){
    			return a;
    		}
    	}
    	return null;
    }
    
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		//FIX - Need to check open/closed status of the particular location use it
		   if (me.getButton() == 3 && bank.contains(me.getX(), me.getY())){
			   radialButton temp = new radialButton(new Ellipse2D.Double(110, 130, BUILDINGSIZE/2+20, BUILDINGSIZE/2+20), "Border", locations.get("Banco Popular"));
			   buttons.add(temp);
			   temp = new radialButton(new Ellipse2D.Double(115, 135, BUILDINGSIZE/2+10, BUILDINGSIZE/2+10), "Close", locations.get("Banco Popular"));
			   buttons.add(temp);
		   }
		   else if (me.getButton() == 3 && market.contains(me.getX(), me.getY())){
			   radialButton temp = new radialButton(new Ellipse2D.Double(190, 130, BUILDINGSIZE/2+20, BUILDINGSIZE/2+20), "Border", locations.get("Pokemart"));
			   buttons.add(temp);
			   temp = new radialButton(new Ellipse2D.Double(195, 135, BUILDINGSIZE/2+10, BUILDINGSIZE/2+10), "Close", locations.get("Pokemart"));
			   buttons.add(temp);
		   }
		   else if (me.getButton() == 3 && restaurant1.contains(me.getX(), me.getY())){
			   radialButton temp = new radialButton(new Ellipse2D.Double(190, 50, BUILDINGSIZE/2+20, BUILDINGSIZE/2+20), "Border", locations.get("Rest 1"));
			   buttons.add(temp);
			   temp = new radialButton(new Ellipse2D.Double(195, 55, BUILDINGSIZE/2+10, BUILDINGSIZE/2+10), "Close", locations.get("Rest 1"));
			   buttons.add(temp);
			   temp = new radialButton(new Ellipse2D.Double(255, 50, BUILDINGSIZE/2+20, BUILDINGSIZE/2+20), "Border", locations.get("Rest 1"));
			   buttons.add(temp);
			   temp = new radialButton(new Ellipse2D.Double(260, 55, BUILDINGSIZE/2+10, BUILDINGSIZE/2+10), "Empty Stock", locations.get("Rest 1"));
			   buttons.add(temp);

		   }
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if(buttons.size() == 0){
			
		}
		else if(me.getButton() == 3 && buttons.get(1).button.contains(me.getX(), me.getY())){
			System.out.println(buttons.get(1).location.getName());
			/*
			if(location.isClosed())
				buttons.get(1).location.setClosed(false);
			else
				buttons.get(1).location.setClosed(true);
			 */
		}
		else if(buttons.size() > 2 && me.getButton() == 3 && buttons.get(3).button.contains(me.getX(), me.getY())){
				System.out.println("Emptying stock of Restaurant");
				((Restaurant)buttons.get(3).location).getCook().msgEmptyStock();
			/*
			buttons.get(3).location.getCook().msgOutOfFood();
			 */
		}
		buttons.clear();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		   if (bank.contains(me.getX(), me.getY())){
			   s = state.bank;
		   }
		   else if (market.contains(me.getX(), me.getY())){
			   s = state.market;
		   }
		   else if(restaurant1.contains(me.getX(), me.getY())){
			   s = state.restaurant1;
		   }
		   else if(apartmentComplex1.contains(me.getX(), me.getY())){
			   s = state.apartment;
		   }
		   else if(house1.contains(me.getX(), me.getY())){
			   s = state.house1;
		   }
		   else if(house2.contains(me.getX(), me.getY())){
			   s = state.house2;
		   }
		   else if(house3.contains(me.getX(), me.getY())){
			   s = state.house3;
		   }
		   else if(house4.contains(me.getX(), me.getY())){
			   s = state.house4;
		   }
		   else{
			   s = state.none;
		   }
		
	}

	public BuildingAnimationPanel getBuildPanel() {
		return BuildPanel;
	}
	
	public void addLocation(Location location){
		locations.put(location.getName(), location);
	}

	public void setBuildPanel(BuildingAnimationPanel buildPanel) {
		BuildPanel = buildPanel;
	}
    
	class radialButton{
		Ellipse2D button;
		String type;
		Location location;
		radialButton(Ellipse2D button, String type, Location location){
			this.button = button;
			this.type = type;
			this.location = location;
		}
	}
	
}


