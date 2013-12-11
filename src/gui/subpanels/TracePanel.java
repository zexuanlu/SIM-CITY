package gui.subpanels;

import gui.panels.*;
import agent.Agent;

import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.Timer;

/**
 * Trace Panel for communication between Agents and GUI
 * 
 */

public class TracePanel extends JPanel implements ActionListener{

	// PANEL SETUP DATA
	private String title = " Trace Panel ";
	private static final int WIDTH = 275;
	private static final int HEIGHT = 500;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	// RELEVANT REFERENCES
	CityControlPanel cntrlPanel;
	
	// Message display area
	JTextArea message = new JTextArea("Trace message output");
	
	// Limit on num messages to display
	private final int messageLim = 1000;
	
	// Queue to hold trace panel messages
	Queue<TracePanelMessage> messages = new ArrayBlockingQueue<TracePanelMessage>(messageLim, true);
	
	// Timer to refresh trace panel
	Timer refTrace = new Timer(500, this);
	
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
		Dimension msgsize = new Dimension(275, 500);
		message.setBackground(Color.GRAY);
		message.setWrapStyleWord(true);
		message.setLineWrap(true);
		message.setEditable(false);
		traceScroll = new JScrollPane(message);
		traceScroll.setPreferredSize(msgsize);
		traceScroll.setMaximumSize(msgsize);
		traceScroll.setBackground(Color.GRAY);
		
		// ADD COMPONENTS
		this.add(traceScroll);
		
		// Start refresh timer
		refTrace.start();
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == refTrace){
			update();
		}
	}
	
	public void addTraceMessage(String m, Agent a){
		TracePanelMessage msg = new TracePanelMessage(m, a);
		if(messages.size() == messageLim){
			messages.remove();
		}
		messages.offer(msg);
	}

	public void print(String m, Agent a){
		addTraceMessage(m, a);
	}
	
	public void update(){
		StringBuilder trace = new StringBuilder();
		synchronized(messages){
			for(TracePanelMessage m : messages){
				trace.append(m.getMessage());
				trace.append("\n");
			}
		}
		
		this.message.setText(trace.toString());
		this.traceScroll.getVerticalScrollBar().setValue(this.traceScroll.getVerticalScrollBar().getMaximum());
	}
}
