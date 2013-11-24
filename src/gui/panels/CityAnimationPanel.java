package gui.panels;
/**
 * This panel is where the main City animations
 * should be displayed
 * 
 */

import javax.swing.*;
import java.awt.*;


public class CityAnimationPanel extends JPanel {

	private String title = " City Animation ";
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	
	public CityAnimationPanel() {
		//PANEL SETUP
		this.setBorder(BorderFactory.createTitledBorder(title));
		
		//Panel size initiations
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

}
