package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {
	
    private final int WINDOWX = 400;
    private final int WINDOWY = 600;
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
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
        g2.fillRect(240, 250,80, 80);
        
        g2.setColor(Color.green);
        g2.fillRect(340, 400, 20, 20);
        
        g2.setColor(Color.green);
        g2.fillRect(220, 400, 20, 20);
        
        
        g2.setColor(Color.green);
        g2.fillRect(340, 460, 20, 20);

        g2.setColor(Color.green);
        g2.fillRect(220, 460, 20, 20);
        
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

    public void addGui(EmployeeGui gui) {
        guis.add(gui);
    }
    
    
}
