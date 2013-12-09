package restaurant3.gui;

import java.awt.*;
import javax.swing.*;

public class Restaurant3GUI extends JFrame {

	Restaurant3AnimationPanel aPanel = new Restaurant3AnimationPanel();
	ControlPanel cPanel = new ControlPanel(this, aPanel);
	
	//Dimensions
	static final int WIDTH = 940;
	static final int HEIGHT = 480;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	public Restaurant3GUI() {
		//Set up frame
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		
		
		//Add components
		this.add(aPanel);
		this.add(cPanel);
		
		
		//Set up GUI frame
		this.setSize(size);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args){
		Restaurant3GUI gui = new Restaurant3GUI();
	}

}
