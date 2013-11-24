package person.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import person.PersonAgent;

public class SimCityGui extends JFrame implements ActionListener {
	
    private final int WINDOWX = 400;
    private final int WINDOWY = 400;
    
	AnimationPanel animationPanel; 
	
	int ANI_X = 400;
	int ANI_Y = 400;
	
	public SimCityGui(){
		
		setBounds(50, 50, WINDOWX, (int)WINDOWY);

		setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.Y_AXIS));
		animationPanel = new AnimationPanel();
		Dimension aniDim = new Dimension(ANI_X, ANI_Y);
		animationPanel.setPreferredSize(aniDim);
		animationPanel.setMinimumSize(aniDim);
		animationPanel.setMaximumSize(aniDim);
		PersonAgent p = new PersonAgent("Grant");
		PersonGui pg = new PersonGui(p);
		animationPanel.addGui(pg);
		add(animationPanel);
		p.startThread();

	}
	
	public static void main(String[] args) {
		SimCityGui gui = new SimCityGui();
		gui.setTitle("Sim City");
		gui.setVisible(true);
		gui.setResizable(false);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
