package restaurant5.gui;

import restaurant5.CustomerAgent5;
import restaurant5.WaiterAgent5; 
import restaurant5.interfaces.Waiter5; 
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
    Dimension test = new Dimension(100,20);

	JFrame animationFrame = new JFrame("Restaurant Animation");
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel5 restPanel = new RestaurantPanel5(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    private JButton onBreak; 
    private JPanel layoutPanel = new JPanel(); 
	private JLabel gap = new JLabel("");

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    //ADDED CODE so that button works
    public void testingbutton() {
    	stateCB.doClick();
    }
    
    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        //int WINDOWX = 450;
        //int WINDOWY = 350;
    	

        
        int WINDOWX = 1200;
        int WINDOWY = 700;
        
    	setBounds(50, 50, WINDOWX, WINDOWY);

    	setLayout(new GridLayout(2,2));

       // Dimension restDim = new Dimension(1000, (350));
    	Dimension restDim = new Dimension(500, (350));
        //restPanel.setLayout(new FlowLayout());
        restPanel.setPreferredSize(restDim);
        //restPanel.setMinimumSize(restDim);
        //restPanel.setMaximumSize(restDim);
        add(restPanel);
        add(animationPanel);
  
  
        // Now, setup the info panel
        Dimension gapDim = new Dimension(WINDOWX, 20);
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .05));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        gap.setPreferredSize(gapDim);
        onBreak = new JButton("Put Waiter on/off Break");
        onBreak.setVisible(false);
        onBreak.addActionListener(this);
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        //infoPanel.setLayout(new GridLayout(2, 4, 0, 0));
        infoPanel.setLayout(new FlowLayout());
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        infoPanel.add(gap);
        //infoPanel.add(choice);
        //infoPanel.add(sendChoice);
        infoPanel.add(onBreak);

        add(infoPanel);

    }
    

    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        currentPerson = person;

        if (person instanceof CustomerAgent5) {
        	onBreak.setVisible(false);
            stateCB.setVisible(true);
            CustomerAgent5 customer = (CustomerAgent5) person;
            stateCB.setText("Hungry?");
            stateCB.setSelected(customer.getGui().isHungry());
            stateCB.setEnabled(!customer.getGui().isHungry());
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        
        if (person instanceof WaiterAgent5){
        	WaiterAgent5 waiter = (WaiterAgent5) person; 
        	stateCB.setVisible(false);
        	onBreak.setVisible(true);
        	if (waiter.onBreak().equals("OnBreak")){
        		onBreak.setText("Put Waiter Off Break");
        	}
        	else if (waiter.onBreak().equals("OffBreak")){
        		onBreak.setText("Put Waiter On Break");
        	}
	    	else {
	    		onBreak.setText("Thinking");
	    		onBreak.setEnabled(false);
	    	}
        	infoLabel.setText(
        			"<html><pre>         Name: " + waiter.getName() + " </pre></html>"
        			);
        }
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent5) {
                CustomerAgent5 c = (CustomerAgent5) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
        if (e.getSource()== onBreak){
        	if (currentPerson instanceof WaiterAgent5){
        		WaiterAgent5 w = (WaiterAgent5) currentPerson; 
            	if (w.onBreak().equals("OnBreak")){
            		onBreak.setText("Put Waiter On Break");
        			w.msgoffBreak();
            	}
            	else if (w.onBreak().equals("OffBreak")){
            		onBreak.setText("Put Waiter Off Break");
        			w.msgWanttoGoonBreak();
            	}
    	    	else {
    	    		onBreak.setText("Put Waiter On Break");
    	    		onBreak.setEnabled(false);
    	    	}
        	}
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerAgent5 c) {
        if (currentPerson instanceof CustomerAgent5) {
            CustomerAgent5 cust = (CustomerAgent5) currentPerson;
            if (c == cust) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    
    public void Break(boolean obreak, Waiter5 w){
    	if (currentPerson == w){
	    	if (obreak){
	    		onBreak.setEnabled(true);
	    		onBreak.setText("Put Waiter Off Break");
	    	}
	    	else {
	    		onBreak.setEnabled(true);
	    		onBreak.setText("Put Waiter On Break");
	    	}
    	}
    }

    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
