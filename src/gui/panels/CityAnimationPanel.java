

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
import restaurant2.gui.Restaurant2AnimationPanel;
import restaurant3.gui.Restaurant3AnimationPanel;
import restaurant4.gui.Restaurant4AnimationPanel;
import restaurant5.gui.Restaurant5AnimationPanel;
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
    public BankAnimationPanel bankPanel2 = new BankAnimationPanel();
    public MarketAnimationPanel marketPanel = new MarketAnimationPanel();
    public MarketAnimationPanel marketPanel2 = new MarketAnimationPanel();
    public Restaurant1AnimationPanel rest1Panel = new Restaurant1AnimationPanel();
    public Restaurant2AnimationPanel rest2Panel = new Restaurant2AnimationPanel();
    public Restaurant3AnimationPanel rest3Panel = new Restaurant3AnimationPanel(); 
    public Restaurant4AnimationPanel rest4Panel = new Restaurant4AnimationPanel();
    public Restaurant5AnimationPanel rest5Panel = new Restaurant5AnimationPanel();
    public Restaurant6AnimationPanel rest6Panel = new Restaurant6AnimationPanel();
    
    public ImageIcon img = new ImageIcon(this.getClass().getResource("image/market1.png"));
    public Image m1 = img.getImage();
    public ImageIcon img1 = new ImageIcon(this.getClass().getResource("image/bank.png"));
    public Image b = img1.getImage();
    public ImageIcon img2 = new ImageIcon(this.getClass().getResource("image/house1.png"));
    public Image h1 = img2.getImage();
    public ImageIcon img3 = new ImageIcon(this.getClass().getResource("image/market2.png"));
    public Image m2 = img3.getImage();
    public ImageIcon img4 = new ImageIcon(this.getClass().getResource("image/house2.png"));
    public Image h2 = img4.getImage();
    public ImageIcon img5 = new ImageIcon(this.getClass().getResource("image/house3.png"));
    public Image h3 = img5.getImage();
    public ImageIcon img6 = new ImageIcon(this.getClass().getResource("image/house4.png"));
    public Image h4 = img6.getImage();
    public ImageIcon img7 = new ImageIcon(this.getClass().getResource("image/house5.png"));
    public Image h5 = img7.getImage();
    public ImageIcon img8 = new ImageIcon(this.getClass().getResource("image/restaurant.png"));
    public Image rest = img8.getImage();
    
    
    public HouseAnimationPanel house1Panel = new HouseAnimationPanel(1);
    public HouseAnimationPanel house2Panel = new HouseAnimationPanel(2);
    public HouseAnimationPanel house3Panel = new HouseAnimationPanel(3);
    public HouseAnimationPanel house4Panel = new HouseAnimationPanel(4);
    public HouseAnimationPanel house5Panel = new HouseAnimationPanel(5);
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
        public static final int WIDTH = 740;
        public static final int HEIGHT = 480;
        public static final int BUILDINGSIZE = 60;
        public static final int BUTTONSIZE = 30;
        public static final int APARTMENTSIZE = 10;
        Timer timer;
        
        enum state {none, bank1, market1, bank2, market2, restaurant1, restaurant4, house1, house2, house3, house4, restaurant2, restaurant3, restaurant5, restaurant6, apartment1, apartment2, apartment3, apartment4, casino, house5}
        //Buttons for buildings
        Rectangle2D bank = new Rectangle2D.Double(60, 110, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D market = new Rectangle2D.Double(130, 110, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D restaurant1 = new Rectangle2D.Double(200, 110, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D restaurant2 = new Rectangle2D.Double(270, 110, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D restaurant3 = new Rectangle2D.Double(270, 40, BUILDINGSIZE, BUILDINGSIZE);

        Rectangle2D house1 = new Rectangle2D.Double(450, 290, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D house2 = new Rectangle2D.Double(450, 370, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D house3 = new Rectangle2D.Double(520, 290, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D house4 = new Rectangle2D.Double(590, 290, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D house5 = new Rectangle2D.Double(660, 290, BUILDINGSIZE, BUILDINGSIZE);
        
        private List<Rectangle2D> apartmentComplex1Components = Collections.synchronizedList(new ArrayList<Rectangle2D>());
        private List<Rectangle2D> apartmentComplex2Components = Collections.synchronizedList(new ArrayList<Rectangle2D>());
        private List<Rectangle2D> apartmentComplex3Components = Collections.synchronizedList(new ArrayList<Rectangle2D>());
        private List<Rectangle2D> apartmentComplex4Components = Collections.synchronizedList(new ArrayList<Rectangle2D>());
        
        // First apartment complex
        Rectangle2D apartmentComplex1 = new Rectangle2D.Double(20, 290, BUILDINGSIZE+10, BUILDINGSIZE+10);
        Rectangle2D apartmentComplex2 = new Rectangle2D.Double(100, 290, BUILDINGSIZE+10, BUILDINGSIZE+10);
        Rectangle2D apartmentComplex3 = new Rectangle2D.Double(180, 290, BUILDINGSIZE+10, BUILDINGSIZE+10);
        Rectangle2D apartmentComplex4 = new Rectangle2D.Double(260, 290, BUILDINGSIZE+10, BUILDINGSIZE+10);
        Rectangle2D casino = new Rectangle2D.Double(260, 370, BUILDINGSIZE+10, BUILDINGSIZE+10);
       
        Rectangle2D bank2 = new Rectangle2D.Double(660, 110, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D market2 = new Rectangle2D.Double(450, 110, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D restaurant4 = new Rectangle2D.Double(520, 110, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D restaurant5 = new Rectangle2D.Double(590, 110, BUILDINGSIZE, BUILDINGSIZE);
        Rectangle2D restaurant6 = new Rectangle2D.Double(450, 40, BUILDINGSIZE, BUILDINGSIZE);
        
        public CityAnimationPanel() {
                //PANEL SETUP
                this.addMouseListener(this);
                this.addMouseMotionListener(this);
                this.setBorder(BorderFactory.createTitledBorder(title));
                
                // Adding apartment buildings to the animation panel
                for (int j = 0; j < 70; j = j + 20) {
                    for (int k = 0; k < 70; k = k + 20) {
                        apartmentComplex1Components.add(new Rectangle2D.Double((int)apartmentComplex1.getX()+k, (int)apartmentComplex1.getY()+j, APARTMENTSIZE,APARTMENTSIZE));
                        apartmentComplex2Components.add(new Rectangle2D.Double((int)apartmentComplex2.getX()+k, (int)apartmentComplex2.getY()+j, APARTMENTSIZE,APARTMENTSIZE));
                        apartmentComplex3Components.add(new Rectangle2D.Double((int)apartmentComplex3.getX()+k, (int)apartmentComplex3.getY()+j, APARTMENTSIZE,APARTMENTSIZE));
                        apartmentComplex4Components.add(new Rectangle2D.Double((int)apartmentComplex4.getX()+k, (int)apartmentComplex4.getY()+j, APARTMENTSIZE,APARTMENTSIZE));
                    }
                }
                
                //Panel size initiations
                this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
                
                homes.add(house1Panel);
                homes.add(house2Panel);
                homes.add(house3Panel);
                homes.add(house4Panel);
                homes.add(house5Panel);
                
                // Creating the apartment animation panel

                for (int i = 5; i < 65; ++i) {
                        apartments.add(new ApartmentAnimationPanel(i));
                }
                
                panels.add(bankPanel);
                panels.add(bankPanel2);
                panels.add(marketPanel);
                panels.add(marketPanel2);
                panels.add(rest1Panel);
                panels.add(rest2Panel);
                panels.add(rest3Panel);
                panels.add(rest4Panel);
                panels.add(rest5Panel);
                panels.add(rest6Panel);
                
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
                   else if(me.getButton() == 1 && bank2.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(bankPanel2);
                   }
                   else if(me.getButton() == 1 && market.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(marketPanel);
                   }
                   else if(me.getButton() == 1 && market2.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(marketPanel2);
                   }
                   else if(me.getButton() == 1 && restaurant1.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(rest1Panel);
                   }
                   else if(me.getButton() == 1 && restaurant2.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(rest2Panel);
                   }
                   else if(me.getButton() == 1 && restaurant3.contains(me.getX(), me.getY())){
                       if(BuildPanel.getComponentCount() > 0)
                                       BuildPanel.remove(BuildPanel.getComponent(0));                           
                       BuildPanel.repaint();
                       BuildPanel.add(rest3Panel);
                   }                  
                   else if(me.getButton() == 1 && restaurant4.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(rest4Panel); 
                   }
                   else if(me.getButton() == 1 && restaurant5.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(rest5Panel); 
                   }
                   else if(me.getButton() == 1 && restaurant6.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(rest6Panel); 
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
                   else if(me.getButton() == 1 && house5.contains(me.getX(), me.getY())){
                           if(BuildPanel.getComponentCount() > 0)
                                           BuildPanel.remove(BuildPanel.getComponent(0));                           
                           BuildPanel.repaint();
                           BuildPanel.add(house5Panel);
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
        g2.fill(bank2);
        g2.fill(apartmentComplex1);
        g2.fill(apartmentComplex2);
        g2.fill(apartmentComplex3);
        g2.fill(apartmentComplex4);
        g2.fill(casino);
        g2.fill(house1);
        g2.fill(house2);
        g2.fill(house3);
        g2.fill(house4);
        g2.fill(house5);
        g2.fill(market);
        g2.fill(market2);
        g2.fill(restaurant1);
        g2.fill(restaurant2);
        g2.fill(restaurant3);
        g2.fill(restaurant5);
        g2.fill(restaurant6);
        g2.fill(restaurant4);

        // Draw out the sidewalks 
        g2.setColor(Color.WHITE);
        g2.fillRect(330, 0, 10, 480);
        g2.fillRect(0, 170, 740, 10);
        g2.fillRect(440, 0, 10, 480);
        g2.fillRect(0, 280, 740, 10);
        
        //draw out the roads
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(340, 0, 100, 480); //vertical
        g2.fillRect(0, 180, 740, 100); //horizontal
        
        g2.drawImage(b, 60, 110, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(b, 660, 110, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(m1, 130, 110, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(m2, 450, 110, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(h1, 450, 290, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(h2, 450, 370, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(h3, 520, 290, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(h4, 590, 290, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(h5, 660, 290, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        g2.drawImage(rest, 200, 110, BUILDINGSIZE, BUILDINGSIZE, BuildPanel);
        
        //Hover Text
            g2.setColor(Color.BLACK);
        if(s == state.bank1){
                g2.drawString("Bank 1", (int)bank.getX()+10, (int)bank.getY()+35);
        }
        else if(s == state.bank2){
                g2.drawString("Bank 2", (int)bank2.getX()+10, (int)bank2.getY()+35);
        }
        else if(s == state.market1){
                g2.drawString("Market 1", (int)market.getX()+8, (int)market.getY()+35);
        }
        else if(s == state.market2){
                g2.drawString("Market 2", (int)market2.getX()+8, (int)market2.getY()+35);
        }
        else if(s == state.restaurant1){
                g2.drawString("Restaurant 1", (int)restaurant1.getX()-5, (int)restaurant1.getY()+35);
        }
        else if(s == state.restaurant2){
                g2.drawString("Restaurant 2", (int)restaurant2.getX()-5, (int)restaurant2.getY()+35);
        }
        else if(s == state.restaurant3){
                g2.drawString("Restaurant 3", (int)restaurant3.getX()-5, (int)restaurant3.getY()+35);
        }
        else if(s == state.restaurant4){
                g2.drawString("Restaurant 4", (int)restaurant4.getX()-5, (int)restaurant4.getY()+35);
        }
        else if(s == state.restaurant5){
                g2.drawString("Restaurant 5", (int)restaurant5.getX()-5, (int)restaurant5.getY()+35);
        }
        else if(s == state.restaurant6){
                g2.drawString("Restaurant 6", (int)restaurant6.getX()-5, (int)restaurant6.getY()+35);
        }
        else if(s == state.house1){
                g2.drawString("House 1", (int)house1.getX()+7, (int)house1.getY()+35);
        }
        else if(s == state.house2){
                g2.drawString("House 2", (int)house2.getX()+7, (int)house2.getY()+35);
        }
        else if(s == state.house3){
                g2.drawString("House 3", (int)house3.getX()+7, (int)house3.getY()+35);
        }
        else if(s == state.house4){
                g2.drawString("House 4", (int)house4.getX()+7, (int)house4.getY()+35);
        }
        else if(s == state.house5){
                g2.drawString("House 5", (int)house5.getX()+7, (int)house5.getY()+35);
        }
        else if(s == state.apartment1){
                g2.drawString("Apartment 1", (int)apartmentComplex1.getX()+3, (int)apartmentComplex1.getY()+40);
                for (Rectangle2D apt : apartmentComplex1Components) {
                        g2.fill(apt);
                }
        }
        else if(s == state.apartment2){
                g2.drawString("Apartment 2", (int)apartmentComplex2.getX()+3, (int)apartmentComplex2.getY()+40);
                for (Rectangle2D apt : apartmentComplex2Components) {
                        g2.fill(apt);
                }
        }
        else if(s == state.apartment3){
                g2.drawString("Apartment 3", (int)apartmentComplex3.getX()+3, (int)apartmentComplex3.getY()+40);
                for (Rectangle2D apt : apartmentComplex3Components) {
                        g2.fill(apt);
                }
        }
        else if(s == state.apartment4){
                g2.drawString("Apartment 4", (int)apartmentComplex4.getX()+3, (int)apartmentComplex4.getY()+40);
                for (Rectangle2D apt : apartmentComplex4Components) {
                        g2.fill(apt);
                }
        }
        else if(s == state.casino){
                g2.drawString("Casino", (int)casino.getX()+15, (int)casino.getY()+40);
        }
        g2.setColor(Color.YELLOW);
        for(int i = 0; i < 330; i += 20){
        	g2.drawLine(i, 200, i+10, 200);
        	g2.drawLine(i, 260, i+10, 260);
        }
        g2.drawLine(0, 220, 330, 220);
        g2.drawLine(0, 240, 330, 240);
        for(int i = 450; i < 1280; i += 20){
        	g2.drawLine(i, 200, i+10, 200);
        	g2.drawLine(i, 260, i+10, 260);
        }
        g2.drawLine(450, 220, 1280, 220);
        g2.drawLine(450, 240, 1280, 240);
        for(int i = 0; i < 170; i +=20){
        	g2.drawLine(360, i, 360, i+10);
        	g2.drawLine(420, i, 420, i+10);
        }
        g2.drawLine(380, 0, 380, 170);
        g2.drawLine(400, 0, 400, 170);
        for(int i = 290; i < 480; i +=20){
        	g2.drawLine(360, i, 360, i+10);
        	g2.drawLine(420, i, 420, i+10);
        }
        g2.drawLine(380, 290, 380, 480);
        g2.drawLine(400, 290, 400, 480);
        
        g2.setColor(Color.WHITE);
        for(int i = 180; i < 270; i+=10){
        	if(i == 230)
        		i--;
        	g2.fillRect(330, i+3, 10, 5);
        	g2.fillRect(440, i+3, 10, 5);
        }
        for(int i = 340; i < 430; i+=10){
        	if(i == 390)
        		i--;
        	g2.fillRect(i+3, 170, 5, 10);
        	g2.fillRect(i+3, 280, 5, 10);
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
                        g2.setColor(Color.WHITE);
                        g2.fill(button.button);
                        g2.setColor(Color.BLACK);
                        if(button.type.equals("Close"))
                                g2.drawString(button.type, (int)button.button.getMinX()-2, (int)button.button.getCenterY()+5);
                        else if(button.type.equals("Open"))
                                g2.drawString(button.type, (int)button.button.getMinX(), (int)button.button.getCenterY()+5);
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
                           radialButton temp;
                           if(!locations.get("Banco Popular").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)bank.getX()-10, (int)bank.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Banco Popular"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)bank.getX()-10, (int)bank.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Banco Popular"));
                           buttons.add(temp);
                   }                   
                   else if (me.getButton() == 3 && bank2.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Banco Popular 2").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)bank2.getX()-10, (int)bank2.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Banco Popular 2"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)bank2.getX()-10, (int)bank2.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Banco Popular2"));
                           buttons.add(temp);
                   }
                   else if (me.getButton() == 3 && market.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Pokemart").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)market.getX()-10, (int)market.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Pokemart 1"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)market.getX()-10, (int)market.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Pokemart 1"));
                           buttons.add(temp);
                   }
                   else if (me.getButton() == 3 && market2.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Pokemart 2").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)market2.getX()-10, (int)market2.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Pokemart 2"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)market2.getX()-10, (int)market2.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Pokemart 2"));
                           buttons.add(temp);
                   }
                   else if (me.getButton() == 3 && restaurant1.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Rest 1").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant1.getX()-10, (int)restaurant1.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Rest 1"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant1.getX()-10, (int)restaurant1.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Rest 1"));
                           buttons.add(temp);
                           temp = new radialButton(new Ellipse2D.Double((int)restaurant1.getMaxX()-20, (int)restaurant1.getY()-10, BUTTONSIZE, BUTTONSIZE), "Empty Stock", locations.get("Rest 1"));
                           buttons.add(temp);
                   }
                   else if (me.getButton() == 3 && restaurant2.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Rest 2").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant2.getX()-10, (int)restaurant2.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Rest 2"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant2.getX()-10, (int)restaurant2.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Rest 2"));
                           buttons.add(temp);
                           temp = new radialButton(new Ellipse2D.Double((int)restaurant2.getMaxX()-20, (int)restaurant2.getY()-10, BUTTONSIZE, BUTTONSIZE), "Empty Stock", locations.get("Rest 2"));
                           buttons.add(temp);
                   }
                   else if (me.getButton() == 3 && restaurant3.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Rest 3").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant3.getX()-10, (int)restaurant3.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Rest 3"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant3.getX()-10, (int)restaurant3.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Rest 3"));
                           buttons.add(temp);
                           temp = new radialButton(new Ellipse2D.Double((int)restaurant3.getMaxX()-20, (int)restaurant3.getY()-10, BUTTONSIZE, BUTTONSIZE), "Empty Stock", locations.get("Rest 3"));
                           buttons.add(temp);
                   }
                   else if (me.getButton() == 3 && restaurant4.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Rest 4").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant4.getX()-10, (int)restaurant4.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Rest 4"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant4.getX()-10, (int)restaurant4.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Rest 4"));
                           buttons.add(temp);
                           temp = new radialButton(new Ellipse2D.Double((int)restaurant4.getMaxX()-20, (int)restaurant4.getY()-10, BUTTONSIZE, BUTTONSIZE), "Empty Stock", locations.get("Rest 4"));
                           buttons.add(temp);
                   }
                   else if (me.getButton() == 3 && restaurant5.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Rest 5").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant5.getX()-10, (int)restaurant5.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Rest 5"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant5.getX()-10, (int)restaurant5.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Rest 5"));
                           buttons.add(temp);
                           temp = new radialButton(new Ellipse2D.Double((int)restaurant5.getMaxX()-20, (int)restaurant5.getY()-10, BUTTONSIZE, BUTTONSIZE), "Empty Stock", locations.get("Rest 5"));
                           buttons.add(temp);
                   }
                   else if (me.getButton() == 3 && restaurant6.contains(me.getX(), me.getY())){
                           radialButton temp;
                           if(!locations.get("Rest 2").isClosed())
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant6.getX()-10, (int)restaurant6.getY()-10, BUTTONSIZE, BUTTONSIZE), "Close", locations.get("Rest 2"));
                           else
                                   temp = new radialButton(new Ellipse2D.Double((int)restaurant6.getX()-10, (int)restaurant6.getY()-10, BUTTONSIZE, BUTTONSIZE), "Open", locations.get("Rest 2"));
                           buttons.add(temp);
                           temp = new radialButton(new Ellipse2D.Double((int)restaurant6.getMaxX()-20, (int)restaurant6.getY()-10, BUTTONSIZE, BUTTONSIZE), "Empty Stock", locations.get("Rest 2"));
                           buttons.add(temp);
                   }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
                if(buttons.isEmpty()){
                        
                }
                else if(me.getButton() == 3 && buttons.get(0).button.contains(me.getX(), me.getY())){
                        buttons.get(0).location.setClosed(!buttons.get(0).location.isClosed());
                }
                else if(buttons.size() > 2 && me.getButton() == 3 && buttons.get(3).button.contains(me.getX(), me.getY())){
                        ((Restaurant)buttons.get(3).location).getCook().msgEmptyStock();
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
                           s = state.bank1;
                   }
                   else if(bank2.contains(me.getX(), me.getY())){
                           s = state.bank2;
                   }
                   else if (market.contains(me.getX(), me.getY())){
                           s = state.market1;
                   }
                   else if(market2.contains(me.getX(), me.getY())){
                           s = state.market2;
                   }
                   else if(restaurant1.contains(me.getX(), me.getY())){
                           s = state.restaurant1;
                   }
                   else if(restaurant2.contains(me.getX(), me.getY())){
                           s = state.restaurant2;
                   }
                   else if(restaurant3.contains(me.getX(), me.getY())){
                           s = state.restaurant3;
                   }
                   else if(restaurant4.contains(me.getX(), me.getY())){
                           s = state.restaurant4;
                   }
                   else if(restaurant5.contains(me.getX(), me.getY())){
                           s = state.restaurant5;
                   }
                   else if(restaurant6.contains(me.getX(), me.getY())){
                           s = state.restaurant6;
                   }
                   else if(apartmentComplex1.contains(me.getX(), me.getY())){
                           s = state.apartment1;
                   }
                   else if(apartmentComplex2.contains(me.getX(), me.getY())){
                           s = state.apartment2;
                   }
                   else if(apartmentComplex3.contains(me.getX(), me.getY())){
                           s = state.apartment3;
                   }
                   else if(apartmentComplex4.contains(me.getX(), me.getY())){
                           s = state.apartment4;
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
                   else if(house5.contains(me.getX(), me.getY())){
                           s = state.house5;
                   }
                   else if(casino.contains(me.getX(), me.getY())){
                           s = state.casino;
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

