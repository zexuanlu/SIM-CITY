package restaurant.gui;

import restaurant.Restaurant1CustomerRole;
import restaurant.Restaurant1WaiterRole;

import javax.swing.*;

import agent.Agent;

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
	//JFrame animationFrame = new JFrame("Restaurant Animation");
	Restaurant1AnimationPanel animationPanel = new Restaurant1AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB, pause, breakbutton;//part of infoLabel
    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */
    private int bound = 50;
    private JButton jiazhuozi,Break;

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 600;
        int WINDOWY = 1000;

//        animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
//        animationFrame.setVisible(true);
//    	animationFrame.add(animationPanel); 
    	
    	setBounds(bound, bound, WINDOWX, WINDOWY);

        setLayout(new BoxLayout((Container) getContentPane(), 
        		BoxLayout.Y_AXIS));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .2));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .2));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        
        jiazhuozi = new JButton();
        jiazhuozi.addActionListener(this);
        jiazhuozi.setText("add a table");
        

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        breakbutton = new JCheckBox();
        breakbutton.setVisible(false);
        breakbutton.addActionListener(this);
        
       pause = new JCheckBox();
       pause.addActionListener(this);
       pause.setText("pause");

      
       
        infoPanel.setLayout(new FlowLayout());
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        infoPanel.add(pause);
        infoPanel.add(jiazhuozi);
        infoPanel.add(breakbutton);
        add(infoPanel);
        
        Dimension animDim = new Dimension(WINDOWX, (int) (WINDOWY));
        animationPanel.setPreferredSize(animDim);
        animationPanel.setMinimumSize(animDim);
        animationPanel.setMaximumSize(animDim);
        add(animationPanel);
      
        
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        currentPerson = person;

        if (person instanceof Restaurant1CustomerRole) {
            stateCB.setVisible(true);
            Restaurant1CustomerRole customer = (Restaurant1CustomerRole) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        else if(person instanceof Restaurant1WaiterRole){
        	//Restaurant1WaiterRole waiter = (Restaurant1WaiterRole) person;
        	breakbutton.setVisible(true);
        	breakbutton.setText("Break");
        }
        infoPanel.validate();
        
    }
    
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == pause){ 
    		if(pause.isSelected() && pause.getText().compareTo("pause") == 0){
    		animationPanel.spause();
    		pause.setText("resume");
    		pause.setSelected(false);
    		}
    		if(pause.isSelected() && pause.getText().compareTo("resume") == 0)
    		{
    			animationPanel.zouni();	
    			pause.setText("pause");
    			pause.setSelected(false);
    		}
    		}
    	
    	if (e.getSource() == jiazhuozi){
    		String s = JOptionPane.showInputDialog("please enter x coordinate");
    		int a = Integer.parseInt(s);
    		String c = JOptionPane.showInputDialog("please enter y coordinate");
    		int b = Integer.parseInt(c);
    		    		
    		animationPanel.jia(a, b);
    	}
    	
    	if (e.getSource() == breakbutton){
            if (currentPerson instanceof Restaurant1WaiterRole) {
                Restaurant1WaiterRole w = (Restaurant1WaiterRole) currentPerson;
                w.IwantBreak();
                breakbutton.setSelected(false);
            }
    	}
    	
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof Restaurant1CustomerRole) {
                Restaurant1CustomerRole c = (Restaurant1CustomerRole) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(Restaurant1CustomerRole c) {
        if (currentPerson instanceof Restaurant1CustomerRole) {
            Restaurant1CustomerRole cust = (Restaurant1CustomerRole) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
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
