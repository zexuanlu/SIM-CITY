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

    public List<Gui> guis = new ArrayList<Gui>();
    
    ImageIcon flImg = new ImageIcon(this.getClass().getResource("restaurantfloor.png"));
    Image floorimg = flImg.getImage();
    ImageIcon table = new ImageIcon(this.getClass().getResource("resttable.png"));
    Image timg = table.getImage();

    public Restaurant5AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        this.setBorder(BorderFactory.createTitledBorder(" Restaurant 5 "));
        
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
        
        //draw floor
        g2.drawImage(floorimg, 0, 0, WINDOWX, WINDOWY, null);

        //Here is the table
        g2.drawImage(timg, 200, 250, 50, 50, null);

        g2.drawImage(timg, 300, 250, 50, 50, null);
        
        g2.drawImage(timg, 400, 250, 50, 50, null);
        
        g2.setColor(Color.blue);
        g2.fillRect(400,20,30,130);
        
        g2.setColor(Color.PINK);
        g2.fillRect(400, 20, 30, 10);
        
        g2.setColor(Color.blue);
        g2.fillRect(480,20,30,30);
        
        g2.setColor(Color.blue);
        g2.fillRect(480,70,30,30);
        
        g2.setColor(Color.blue);
        g2.fillRect(480,120,30,30);
   
//
//        for(Gui gui : guis) {
//            if (gui.isPresent()) {
//                gui.updatePosition();
//            }
//        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(Restaurant5CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(Restaurant5HostGui gui) {
        guis.add(gui);
    }
    public void addGui(Restaurant5WaiterGui gui){
    	guis.add(gui);
    }
    public void addGui(Restaurant5FoodGui gui){
    	guis.add(gui);
    }
    public void addGui(Restaurant5CookGui gui){
    	guis.add(gui);
    }
}
