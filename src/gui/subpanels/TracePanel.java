package gui.subpanels;

import gui.panels.*;

/**
 * Trace panel for back end communication between 
 * GUI and agents
 */

import java.awt.*;

import javax.swing.*;

/**
 * This is the panel for back end communication between
 * agents and GUI
 */

public class TracePanel extends JPanel{

	// PANEL SETUP DATA
	private String title = " Trace Panel ";
	private static final int WIDTH = 275;
	private static final int HEIGHT = 310;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	// RELEVANT REFERENCES
	CityControlPanel cntrlPanel;
	
	// Message display area
	JTextArea message = new JTextArea("Trace message output", 10, 200);
	
	// Scroll pane
	JScrollPane traceScroll;
	
	
	public TracePanel(CityControlPanel cp) {
		cntrlPanel = cp;
		
		// PANEL SETUP
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(Color.GRAY);
		
		// Panel size initiations
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		
		// Component initializations
		message.setBackground(Color.GRAY);
		message.setWrapStyleWord(true);
		message.setLineWrap(true);
		message.setEditable(false);
		traceScroll = new JScrollPane(message);
		
		// ADD COMPONENTS
		this.add(traceScroll);
		
	}

}
