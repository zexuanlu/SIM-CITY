package restaurant5.gui;

import restaurant5.CustomerAgent5;
import restaurant5.HostAgent5;
import restaurant5.CookAgent5;
import restaurant5.MarketAgent5;
import restaurant5.CashierAgent5;
import restaurant5.RevolvingStand5; 
import restaurant5.WaiterBase5; 
import restaurant5.SDWaiterAgent5; 

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList; 
import java.util.Vector;

import restaurant5.WaiterAgent5;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel5 extends JPanel {
	boolean paused = false; 

	private int waitingX; 
	private int customerwaitingX;
	ArrayList<WaiterBase5> waiters = new ArrayList<WaiterBase5>(); 
	ArrayList<MarketAgent5> markets = new ArrayList<MarketAgent5>();

    static final int rows = 1; 
	static final int columns = 2;
	static final int setLayoutgap = 20;
	static final int groupgap = 10;
	
    //Host, cook, waiters and customers
	private RevolvingStand5 revolvingstand = new RevolvingStand5();
	private CookAgent5 cook = new CookAgent5("Cook");
	private CookGui5 cookGui = new CookGui5(cook);
	private MarketAgent5 market;
	private CashierAgent5 cashier = new CashierAgent5("Cashier");
    private HostAgent5 host = new HostAgent5("Sarah");
    private HostGui5 hostGui = new HostGui5(host);
	private WaiterGui5 wgui;
	private WaiterAgent5 w; 
	private SDWaiterAgent5 sdw; 


    private Vector<CustomerAgent5> customers = new Vector<CustomerAgent5>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private JPanel group = new JPanel();

    private RestaurantGui gui; //reference to main gui

    
	public void connectButton(){
		gui.testingbutton();
	}
    
    
    public RestaurantPanel5(RestaurantGui gui) {	
    	waitingX = 20; 
        this.gui = gui;
        host.setGui(hostGui);
        
        gui.animationPanel.addGui(hostGui);
        host.startThread();
        cashier.startThread();
        
        for (int i=0;i<3;i++){
	        market = new MarketAgent5("Market" + " " + (i+1));
	        market.startThread();
	        market.setCook(cook);
	        market.setCashier(cashier);
	        markets.add(market);
	        cook.addMarket(market);
        }
        
        cook.setGui(cookGui);
        cook.setRevolvingStand(revolvingstand); 
        gui.animationPanel.addGui(cookGui);
        cook.startThread();


        setLayout(new GridLayout(rows, columns, setLayoutgap, setLayoutgap));
        group.setLayout(new GridLayout(rows, columns, groupgap, groupgap));

        group.add(customerPanel);

        initRestLabel();
        add(restLabel);
        add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {
        if (type.equals("Customers")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent5 temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        

        if (type.equals("Waiters")){
        	for (int i=0; i< waiters.size(); i++){
        		WaiterBase5 temp = waiters.get(i);
        		if (temp.getName() == name){
        			gui.updateInfoPanel(temp);
        			}
        	}
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		CustomerAgent5 c = new CustomerAgent5(name);	
    		CustomerGui5 g = new CustomerGui5(c, gui,customerwaitingX);
    		//CustomerGui g = new CustomerGui(c, gui);

    		customerwaitingX = customerwaitingX + 25; 
    		FoodGui5 f = new FoodGui5();
  
    		gui.animationPanel.addGui(f);// dw
    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setGui(g);
    		c.setCashier(cashier);
    		c.setFoodGui(f);
    		customers.add(c);
    		c.startThread();
    	}
    }
    
    public void pauseAgents(){
    	if (!paused){
    		cook.pause();
    		cashier.pause();
    		host.pause();
    		for (WaiterBase5 _w: waiters){
    			_w.pause();
    		}
    		for (CustomerAgent5 _c: customers){
    			_c.pause();
    		}
    		paused = true; 
    	}
    	else {
    		cook.restart();
    		host.restart();
    		cashier.restart();
    		for (WaiterBase5 _w: waiters){
    			_w.restart();
    		}
    		for (CustomerAgent5 _c: customers){
    			_c.restart();
    		}
    		paused = false; 
    	}
    	
    }
    
    public void drainCook(){
    	System.out.println("Drain Cook's Inventory");
    	cook.drain();
    }
    
    public void drainCashier(){
    	System.out.println("Subtract all but 100 from Cashier's Cash");
    	cashier.msgDrainMoney();
    }
    
    public void addMoneyCashier(){
    	System.out.println("Add 1000 to Cashier's Cash");
    	cashier.msgAddMoney();
    }
    
    public void addWaiter(String name){
    	
    	w = new WaiterAgent5(name);
    	wgui = new WaiterGui5(w,waitingX);
        waitingX = waitingX + 25;
    	wgui.setGui(gui);
        gui.animationPanel.addGui(wgui);
    	w.setGui(wgui);
        w.setCook(cook);
        w.setHost(host);
        w.setRestaurantGui(gui);
        w.setCashier(cashier);
    	host.addWaiter(w);
        w.startThread();
    	if (waiters.isEmpty()){
        	host.msgfirstWaiter();
    	}
    	waiters.add(w);
    }
    
    public void addSDWaiter(String name){
    	sdw = new SDWaiterAgent5(name);
    	sdw.setStand(revolvingstand);
    	wgui = new WaiterGui5(sdw,waitingX);
        waitingX = waitingX + 25;
    	wgui.setGui(gui);
        gui.animationPanel.addGui(wgui);
    	sdw.setGui(wgui);
        sdw.setCook(cook);
        sdw.setHost(host);
        sdw.setRestaurantGui(gui);
        sdw.setCashier(cashier);
    	host.addWaiter(sdw);
        sdw.startThread();
    	if (waiters.isEmpty()){
        	host.msgfirstWaiter();
    	}
    	waiters.add(sdw);
    }	
    
}