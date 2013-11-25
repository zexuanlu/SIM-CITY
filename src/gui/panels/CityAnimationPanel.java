package gui.panels;
/**
 * This panel is where the main City animations
 * should be displayed
 * 
 */

import javax.swing.*;

import person.gui.PersonGui;
import simcity.gui.BusGui;
import simcity.gui.BusStopGui;
import simcity.gui.CarGui;
import simcity.gui.Gui;
import simcity.gui.PassengerGui;
import person.gui.PersonGui; 

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class CityAnimationPanel extends JPanel implements ActionListener {

    private List<Gui> guis = new ArrayList<Gui>();
    private Image bufferImage;
    private Dimension bufferSize;
	private String title = " City Animation ";
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	
	public CityAnimationPanel() {
		//PANEL SETUP
		this.setBorder(BorderFactory.createTitledBorder(title));
		
		//Panel size initiations
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		
    	setSize(WIDTH, HEIGHT);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(8, this );
    	timer.start();
	}
	

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WIDTH, HEIGHT );

        //Here is the table
        
        //draw out the roads
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(280, 0, 60, 480);
        g2.fillRect(0, 220, 640, 60);
        
        //draw out random buildings
        g2.setColor(Color.GRAY);
        //homes
        g2.fillRect(400, 60, 50, 50); 
        g2.fillRect(500, 60, 50, 50); 
        g2.fillRect(500, 120, 50, 50); 
        g2.fillRect(400, 120, 50, 50); 



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
    }
    
    public void addGui(PersonGui gui) {
        guis.add(gui);
    }
    
	
}
