package restaurant5.gui;

import javax.swing.*;

import utilities.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class Restaurant5AnimationPanel extends JPanel implements ActionListener {
	
    private final int WINDOWX = 540;
    private final int WINDOWY = 480;
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public Restaurant5AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	//Timer timer = new Timer(8, this );
    	//timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(200, 250, 50, 50);//200 and 250 need to be table params
        
        g2.setColor(Color.orange);
        g2.fillRect(300,250,50,50);
        
        g2.setColor(Color.orange);
        g2.fillRect(400,250,50,50);
        
        g2.setColor(Color.blue);
        g2.fillRect(450,20,30,130);
        
        g2.setColor(Color.PINK);
        g2.fillRect(450, 20, 30, 10);
        
        g2.setColor(Color.blue);
        g2.fillRect(560,20,30,30);
        
        g2.setColor(Color.blue);
        g2.fillRect(560,70,30,30);
        
        g2.setColor(Color.blue);
        g2.fillRect(560,120,30,30);
   

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

    public void addGui(CustomerGui5 gui) {
        guis.add(gui);
    }

    public void addGui(HostGui5 gui) {
        guis.add(gui);
    }
    public void addGui(WaiterGui5 gui){
    	guis.add(gui);
    }
    public void addGui(FoodGui5 gui){
    	guis.add(gui);
    }
    public void addGui(CookGui5 gui){
    	guis.add(gui);
    }
}
