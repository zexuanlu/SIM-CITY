package restaurant4.gui;

import javax.swing.*;

import utilities.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/** 
 * The panel where all of the animation takes places
 * Handles movement and other animation things
 */
public class Restaurant4AnimationPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2L;
	private final int WINDOWX = 450;
    private final int WINDOWY = 350;
    static final int TIMERCOUNT = 20;
    static final int TABLEDIST = 100;
    static final int TABLEX = 50;
    static final int TABLEY = 200;
    static final int TABLEDIM = 50;

    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

    public Restaurant4AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(TIMERCOUNT, this );
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

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLEX, TABLEY, TABLEDIM, TABLEDIM);//200 and 250 need to be table params
        
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLEX + TABLEDIST, TABLEY, TABLEDIM, TABLEDIM);
        
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLEX + 2*TABLEDIST, TABLEY, TABLEDIM, TABLEDIM);

        //Customer Waiting Area
        g2.setColor(Color.GRAY);
        g2.fillRect(38, 14, 298, 24);
        
        //Waiter Home Area
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 10, 24, 234);
        
        //Grills
        g2.setColor(Color.CYAN);
        g2.fillRect(410, 38, 24, 234);
        
        //Plating Area
        g2.setColor(Color.YELLOW);
        g2.fillRect(355, 38, 24, 234);
        
        //Text for Areas
        g2.setColor(Color.BLACK);
        g2.drawString("Customer Waiting Area", 120, 10);
        g2.drawString("Waiters", 0, 10);
        g2.drawString("Plating", 350, 284);
        g2.drawString("Cooking", 394, 284);

        for(Gui gui : guis) {
            if (gui.isPresent()) {
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
    }

    public void addGui(Gui gui) {
        guis.add(gui);
    }
    
    public void removeGui(Gui gui){
    	guis.remove(gui);
    }
}
