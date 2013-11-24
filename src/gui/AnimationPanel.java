package gui;

import javax.swing.*;

import person.interfaces.Person;
import resident.HomeOwnerRole;
import resident.interfaces.HomeOwner;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {
	
    private final int WINDOWX = 400;
    private final int WINDOWY = 600;
    private static int fridgeX = 70;
    private static int fridgeY = 190;
    private static int stoveX = 100;
    private static int stoveY = 190;
    private static int tableX = 200;
    private static int tableY = 190;
    private static int sinkX = 130;
    private static int sinkY = 190;
    
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
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
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        // Drawing the grill
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(stoveX, stoveY, 20, 20);
        
        // Drawing the table
        g2.setColor(Color.BLACK);
        g2.fillRect(tableX, tableY, 20, 20);
        
        // Drawing the sink
        g2.setColor(Color.CYAN);
        g2.fillRect(sinkX, sinkY, 20, 20);
        
        g2.setColor(Color.blue);
        g2.fillRect(450,20,30,130);
        
        g2.setColor(Color.blue);
        g2.fillRect(560,20,30,30);
        
        g2.setColor(Color.blue);
        g2.fillRect(560,70,30,30);
        
        g2.setColor(Color.blue);
        g2.fillRect(560,120,30,30);
        
        // Drawing the fridge
        g2.setColor(Color.BLUE);
        g2.fillRect(fridgeX, fridgeY, 20, 20);
        
        // Drawing the bedroom
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 230, 500, 450);
  
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

    public void addGui(HomeOwnerGui gui) {
        guis.add(gui);
    }
/**
    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    public void addGui(WaiterGui gui){
    	guis.add(gui);
    }
    public void addGui(FoodGui gui){
    	guis.add(gui);
    }
    public void addGui(CookGui gui){
    	guis.add(gui);
    }*/
    
}
