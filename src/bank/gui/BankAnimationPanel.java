package bank.gui;

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
public class BankAnimationPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2L;
	private final int WINDOWX = 540;
    private final int WINDOWY = 480;
    static final int TIMERCOUNT = 20;
    static final int TELLERBOOTHSIZE = 60;
    static final int TELLERBOOTHX = 60;
    static final int TELLERBOOTHY = 350;
    static final int HOSTDESKWIDTH = 51;
    static final int HOSTDESKHEIGHT = 60;
    static final int HOSTDESKX = 430;
    static final int HOSTDESKY = 90;

    public List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

    public ImageIcon img = new ImageIcon(this.getClass().getResource("image/hostDesk.png"));
    public Image m1 = img.getImage();
    public ImageIcon img2 = new ImageIcon(this.getClass().getResource("image/tellerDesk.png"));
    public Image m2 = img2.getImage();
    public ImageIcon img3 = new ImageIcon(this.getClass().getResource("image/bank.png"));
    public Image m3 = img3.getImage();
    
    public BankAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
    	Dimension d = new Dimension(WINDOWX, WINDOWY);
    	setPreferredSize(d);
    	setVisible(true);
		this.setBorder(BorderFactory.createTitledBorder("Bank"));
		
//    	Timer timer = new Timer(TIMERCOUNT, this );
 //   	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        //draw background
        g2.drawImage(m3, 0, 0, WINDOWX, WINDOWY, null);

        //Here are the teller booths
        g2.setColor(Color.BLACK);
        for(int i = 0; i < 5; i++){
            g2.setColor(Color.BLACK);
            g2.drawImage(m2, TELLERBOOTHX + 80*i, TELLERBOOTHY, TELLERBOOTHSIZE, TELLERBOOTHSIZE, null);
        	//g2.fillRect(TELLERBOOTHX + 80*i, TELLERBOOTHY, TELLERBOOTHSIZE, TELLERBOOTHSIZE);
        }

        g2.drawImage(m1, HOSTDESKX, HOSTDESKY, HOSTDESKWIDTH, HOSTDESKHEIGHT, null);
        g2.setColor(Color.RED);
        //g2.fillRect(HOSTDESKX, HOSTDESKY, HOSTDESKWIDTH, HOSTDESKHEIGHT);

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
