package restaurant2.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 700;
    private final int WINDOWY = 360;
    private final int tablex = 250;
    private final int tabley = 100;
    private final int tablex2 = 350;
    private final int tabley2 = 100;
    private final int tablex3 = 250;
    private final int tabley3 = 200;
    private final int tablex4 = 350;
    private final int tabley4 = 200;
    private final int tableW = 50;
    private final int CookStationx = 550;
    private final int CookStationy = 30;
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
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
        g2.fillRect(tablex, tabley, tableW, tableW);//200 and 250 need to be table params
        g2.fillRect(tablex2, tabley2, tableW, tableW);
        g2.fillRect(tablex3, tabley3, tableW, tableW);
        g2.fillRect(tablex4, tabley4, tableW, tableW);
        g2.fillRect(CookStationx, CookStationy, 25, 133);
        g2.fillRect(650, 30, 25, 133);

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

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    public void addGui(WaiterGui gui){
    	guis.add(gui);
    }
    public void addGui(CookGui gui)
    {
    	guis.add(gui);
    }
}
