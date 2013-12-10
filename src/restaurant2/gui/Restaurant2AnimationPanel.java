package restaurant2.gui;

import javax.swing.*;

import utilities.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class Restaurant2AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 540;
    private final int WINDOWY = 480;
    private final int tablex = 50;
    private final int tabley = 400;
    private final int tablex2 = 150;
    private final int tabley2 = 400;
    private final int tablex3 = 250;
    private final int tabley3 = 400;
    private final int tablex4 = 350;
    private final int tabley4 = 400;
    private final int tableW = 50;
    private final int CookStationx = 350;
    private final int CookStationy = 30;
    private final int GrillStationx = 450;
    private final int GrillStationy = 30;
    private Image bufferImage;
    private Dimension bufferSize;

    public List<Gui> guis = new ArrayList<Gui>();

    public Restaurant2AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    //	Timer timer = new Timer(20, this );
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
        
        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(tablex, tabley, tableW, tableW);//200 and 250 need to be table params
        g2.fillRect(tablex2, tabley2, tableW, tableW);
        g2.fillRect(tablex3, tabley3, tableW, tableW);
        g2.fillRect(tablex4, tabley4, tableW, tableW);
        g2.setColor(Color.GRAY);
        g2.fillRect(CookStationx, CookStationy, 25, 133);
        g2.fillRect(GrillStationx, GrillStationy, 25, 133);

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

    public void addGui(Restaurant2CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(Restaurant2HostGui gui) {
        guis.add(gui);
    }
    public void addGui(Restaurant2WaiterGui gui){
    	guis.add(gui);
    }
    public void addGui(Restaurant2CookGui gui)
    {
    	guis.add(gui);
    }
}
