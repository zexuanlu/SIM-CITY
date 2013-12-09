package market.gui;

import javax.swing.*;

import utilities.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class MarketAnimationPanel extends JPanel implements ActionListener {
	
    private final int WINDOWX = 540;
    private final int WINDOWY = 480;
    private Image bufferImage;
    private Dimension bufferSize;

    public List<Gui> guis = new ArrayList<Gui>();

    public MarketAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
		this.setBorder(BorderFactory.createTitledBorder("Market"));

        
        bufferSize = this.getSize();
 
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

    
        g2.setColor(Color.BLUE);
        g2.fillRect(100, 160, 80, 80);
        
        g2.setColor(Color.green);
        g2.fillRect(250, 120, 240, 20);
        
        g2.setColor(Color.green);
        g2.fillRect(250, 260, 240, 20);


        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    
    public void addGui(MarketCustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(MarketEmployeeGui gui) {
        guis.add(gui);
    }
    
    
}
