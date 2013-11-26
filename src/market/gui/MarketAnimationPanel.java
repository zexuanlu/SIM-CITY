package market.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class MarketAnimationPanel extends JPanel implements ActionListener {
	
    private final int WINDOWX = 640;
    private final int WINDOWY = 480;
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public MarketAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
		this.setBorder(BorderFactory.createTitledBorder("Market"));

        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(8, this );
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
//        g2.setColor(Color.ORANGE);
//        g2.fillRect(200, 250, 50, 50);//200 and 250 need to be table params
//        
        g2.setColor(Color.BLUE);
        g2.fillRect(100, 160, 80, 80);
        
        g2.setColor(Color.green);
        g2.fillRect(300, 260, 40, 40);
        
        g2.setColor(Color.green);
        g2.fillRect(300, 100, 40, 40);
        
        g2.setColor(Color.green);
        g2.fillRect(400, 260, 40, 40);
        
        g2.setColor(Color.green);
        g2.fillRect(400, 100, 40, 40);
        
        g2.setColor(Color.green);
        g2.fillRect(500, 260, 40, 40);

        g2.setColor(Color.green);
        g2.fillRect(500, 100, 40, 40);

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
