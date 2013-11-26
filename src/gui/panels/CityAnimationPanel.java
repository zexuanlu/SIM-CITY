

package gui.panels;
/**
 * This panel is where the main City animations
 * should be displayed
 * 
 */

import javax.swing.*; 

import market.gui.MarketAnimationPanel;
import bank.gui.BankAnimationPanel;
import person.gui.PersonGui;
import resident.gui.ApartmentAnimationPanel;
import resident.gui.HomeOwnerGui;
import resident.gui.HouseAnimationPanel;
import restaurant.gui.Restaurant1AnimationPanel;
import simcity.gui.BusGui;
import simcity.gui.BusStopGui;
import simcity.gui.CarGui;
import simcity.gui.Gui;
import simcity.gui.PassengerGui;
import person.gui.PersonGui; 

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


public class CityAnimationPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener{

	public BuildingAnimationPanel BuildPanel;
	public BankAnimationPanel bankPanel = new BankAnimationPanel();
	public MarketAnimationPanel marketPanel = new MarketAnimationPanel();
	public Restaurant1AnimationPanel rest1Panel = new Restaurant1AnimationPanel();
	public ApartmentAnimationPanel apt1Panel = new ApartmentAnimationPanel(1);
	public ApartmentAnimationPanel apt2Panel = new ApartmentAnimationPanel(2);
	public ApartmentAnimationPanel apt3Panel = new ApartmentAnimationPanel(3);
	public ApartmentAnimationPanel apt4Panel = new ApartmentAnimationPanel(4);
	public HouseAnimationPanel house1Panel = new HouseAnimationPanel(1);
	public HouseAnimationPanel house2Panel = new HouseAnimationPanel(2);
	public HouseAnimationPanel house3Panel = new HouseAnimationPanel(3);
	public HouseAnimationPanel house4Panel = new HouseAnimationPanel(4);
    private List<Gui> guis = new ArrayList<Gui>();
    private List<HouseAnimationPanel> homes = new ArrayList<HouseAnimationPanel>();
    private List<ApartmentAnimationPanel> apartments = new ArrayList<ApartmentAnimationPanel>();
    private Image bufferImage;
    private Dimension bufferSize;
	private String title = " City Animation ";
	state s = state.none;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int BUILDINGSIZE = 60;
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
	Rectangle2D apartment = new Rectangle2D.Double(340, 280, BUILDINGSIZE+10, BUILDINGSIZE+10);
	Rectangle2D apartment1 = new Rectangle2D.Double(340, 280, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment2 = new Rectangle2D.Double(340, 320, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment3 = new Rectangle2D.Double(380, 280, BUILDINGSIZE/2, BUILDINGSIZE/2);
	Rectangle2D apartment4 = new Rectangle2D.Double(380, 320, BUILDINGSIZE/2, BUILDINGSIZE/2);
	
	public CityAnimationPanel() {
		//PANEL SETUP
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBorder(BorderFactory.createTitledBorder(title));
		
		//Panel size initiations
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		homes.add(house1Panel);
		homes.add(house2Panel);
		homes.add(house3Panel);
		homes.add(house4Panel);
		
		apartments.add(apt1Panel);
		apartments.add(apt2Panel);
		apartments.add(apt3Panel);
		apartments.add(apt4Panel);
		
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
		   else if(me.getButton() == 1 && apartment1.contains(me.getX(), me.getY())){
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
		   }
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
        g2.fill(apartment);
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
            g2.fill(apartment1);
            g2.fill(apartment2);
            g2.fill(apartment3);
            g2.fill(apartment4);
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
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
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
		   else if(apartment.contains(me.getX(), me.getY())){
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
    
	
}


