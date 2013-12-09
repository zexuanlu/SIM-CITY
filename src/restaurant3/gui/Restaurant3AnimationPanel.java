package restaurant3.gui;

import restaurant3.Restaurant3HostRole;

import java.awt.*;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class Restaurant3AnimationPanel extends JPanel implements ActionListener {

	//Dimensions
	static final int WIDTH = 640;
	static final int HEIGHT = 480;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	//***************** Kitchen locations and dimensions ********************
	static final int kitchenY = 350;
	
	//Refrigerator
	static final int fridgeX = 440;
	static final int fridgeY = 390;
	private final int fridgeW = 60;
	private final int fridgeH = 60;
	
	//Grill
	private final int grillW = 40;
	private final int grillH = 40;
	static final int grillX = 400;
	
	//Order stand
	private final int oStandW = 40;
	private final int oStandH = 40;
	static final int oStandX = 320;
	
	//Tables
	private final int TableW = 50;
	private final int TableH = 50;
	
	//************************************************
	
	//Number of tables
	private int nTables = Restaurant3HostRole.NTABLES;
	
	//Animation utilities
	Timer animTimer = new Timer(5, this);
	private List<Gui> guis = new ArrayList<Gui>();
	
	public Restaurant3AnimationPanel() {
		//Set up panel
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
		
		//Start timer for animation
		animTimer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	public void paintComponent(Graphics g){
		 Graphics2D g2 = (Graphics2D)g;
		 
		 //Clear the screen by painting a rectangle the size of the frame
	     g2.setColor(getBackground());
	     g2.fillRect(0, 0, WIDTH, HEIGHT );
	        
	     //Draw table
	     g2.setColor(Color.ORANGE);
	     for(int i = 1; i <= nTables; i++){
	    	int row;
	     	if(i%3 !=0){
	     		row = i/3 + 1;
			}
			else {
				row = i/3;
			}
	        int TABLEX = (((i-1)%3)+1)*100;
	        int TABLEY = row*100;
	        g2.fillRect(TABLEX, TABLEY, TableW, TableH);
	     }
	        
	     //Draw fridge
	     g2.setColor(Color.BLUE);
	     g2.fillRect(fridgeX, fridgeY, fridgeW, fridgeH);
	     
	     //Draw grill
	     g2.setColor(Color.BLACK);
	     g2.fillRect(grillX, kitchenY, grillW, grillH);
	        
	     //Draw grill
	     g2.setColor(Color.BLACK);
	     g2.fillRect(oStandX, kitchenY, oStandW, oStandH);
	        
	     //Update all character guis
	     for(Gui gui : guis) {
	         if (gui.isPresent()) {
	             gui.updatePosition();
	         }
	     }
	        
	     //Draw all character guis
	     for(Gui gui : guis) {
	         if (gui.isPresent()) {
	             gui.draw(g2);
	         }
	     }
	}
	
	public void addGui(Restaurant3CustomerGui gui) {
	        guis.add(gui);
	}
	
	public void addGui(Restaurant3WaiterGui gui) {
		guis.add(gui);
	}
	
	public void addGui(Restaurant3CookGui gui) {
		guis.add(gui);
	}
}
