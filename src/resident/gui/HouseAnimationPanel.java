package resident.gui;

import javax.swing.*;

import utilities.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class HouseAnimationPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 11;
	private final int WINDOWX = 540;
    private final int WINDOWY = 480;
    private static int fridgeX = 70;
    private static int fridgeY = 190;
    private static int stoveX = 100;
    private static int stoveY = 190;
    private static int tableX = 300;
    private static int tableY = 100;
    private static int sinkX = 130;
    private static int sinkY = 190;
    public int houseNumber;

    public List<Gui> guis = new ArrayList<Gui>();
    
    //Images
    //Images
    ImageIcon img = new ImageIcon(this.getClass().getResource("aptfloor.png"));
    Image image = img.getImage();
    
    ImageIcon img2 = new ImageIcon(this.getClass().getResource("bedfloor.png"));
    Image image2 = img2.getImage();
    
    ImageIcon img3 = new ImageIcon(this.getClass().getResource("aptbed.png"));
    Image image3 = img3.getImage();
    
    ImageIcon t = new ImageIcon(this.getClass().getResource("resttable.png"));
    Image timg = t.getImage();


    public HouseAnimationPanel(int n) {
    	houseNumber = n; 
    	
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
                
        this.setBorder(BorderFactory.createTitledBorder("House " + houseNumber));
 
    //	Timer timer = new Timer(8, this );
    //	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        //draw main floor
        g2.drawImage(image, 0, 0, WINDOWX, WINDOWY, null);
        
        // Drawing the grill
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(stoveX, stoveY, 20, 20);
        
        // Drawing the table
        g2.drawImage(timg, tableX, tableY, 40, 40, null);
        
        // Drawing the sink
        g2.setColor(Color.CYAN);
        g2.fillRect(sinkX, sinkY, 20, 20);
        
        // Drawing the front door
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(600,20,40,130);
        
        // Drawing the fridge
        g2.setColor(Color.BLUE);
        g2.fillRect(fridgeX, fridgeY, 20, 20);
        
        // Drawing the bedroom
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 230, 640, 450);
        
        g2.drawImage(image2, 0, 230, 640, 450, null);
        
        // Drawing the bed
        g2.drawImage(image3, 250, 300, 50, 100, null);
        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(HomeOwnerGui gui) {
        guis.add(gui);
    }

}
