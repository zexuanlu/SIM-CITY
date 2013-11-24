package bank.gui;

import javax.swing.*;

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
public class BankAnimationPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2L;
	private final int WINDOWX = 640;
    private final int WINDOWY = 480;
    static final int TIMERCOUNT = 20;
    static final int TELLERBOOTHSIZE = 20;
    static final int TELLERBOOTHX = 40;
    static final int TELLERBOOTHY = 360;
    static final int HOSTDESKWIDTH = 60;
    static final int HOSTDESKHEIGHT = 20;
    static final int HOSTDESKX = 300;
    static final int HOSTDESKY = 100;

    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

    public BankAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
    	setVisible(true);
 
    	Timer timer = new Timer(TIMERCOUNT, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here are the teller booths
        g2.setColor(Color.BLACK);
        for(int i = 0; i < 10; i++){
            g2.setColor(Color.BLACK);
        	g2.fillRect(TELLERBOOTHX + 60*i, TELLERBOOTHY, TELLERBOOTHSIZE, TELLERBOOTHSIZE);
        	//g2.setColor(Color.BLUE);
        	//g2.fillRect(TELLERBOOTHX + 20, TELLERBOOTHY + 50*i, 20, 20);
        }

        g2.setColor(Color.RED);
        g2.fillRect(HOSTDESKX, HOSTDESKY, HOSTDESKWIDTH, HOSTDESKHEIGHT);
        
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
