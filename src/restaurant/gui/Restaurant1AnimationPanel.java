package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class Restaurant1AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 600;
    private final int WINDOWY = 350;
    private final int lec = 200, lec1 = 300;
    private final int wid = 250, wid1 = 150, res = 50, origion = 0;
    private Image bufferImage;
    private Dimension bufferSize;
    
    private int xc = 0;
    private int yc = 0;
    private int checkx = 0;
    private int checky = 0;
    
    
    private List<Gui> guis = new ArrayList<Gui>();

    public Restaurant1AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

	
	public void jia(int a, int b){
		xc = a;
		yc = b;
	}
	
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(origion, origion, WINDOWX, WINDOWY );

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(lec, wid, res, res);//200 and 250 need to be table params
        
        g2.setColor(Color.ORANGE);
        g2.fillRect(lec1, wid, res, res);

        g2.setColor(Color.ORANGE);
        g2.fillRect(lec1, wid1, res, res);
        
		g2.setColor(Color.cyan);
		g2.fillRect(540, 230, 30, 30);
		g2.fillRect(540, 130, 30, 30);
     
        if(checkx != xc || checky!= yc ){
            g2.setColor(Color.ORANGE);
            g2.fillRect(xc, yc, res, res);    
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
    
    public void spause(){
    	for(Gui gui : guis){
    		gui.stop();
    	}
    }
    
    public void zouni(){
    	for(Gui gui : guis){
    		gui.zou();
    		}
    }
    
    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
    
    public void addGui(CookGui gui){
    	guis.add(gui);
    }
}
