package gui.panels;
/**
 * This panel is where the zoomed in animations
 * within each building should be displayed
 * 
 */

import javax.swing.*;
import java.awt.*;


public class BuildingAnimationPanel extends JPanel {
	
	private static final long serialVersionUID = 1;
	private String title = " Building Animation ";
	public static final int WIDTH = 530;
	public static final int HEIGHT = 480;
	
	
	public BuildingAnimationPanel() {
		// PANEL SETUP
		this.setBorder(BorderFactory.createTitledBorder(title));
		
		// Panel size initiations
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

}
