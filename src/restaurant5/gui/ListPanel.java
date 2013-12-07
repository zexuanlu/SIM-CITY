package restaurant5.gui;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */



public class ListPanel extends JPanel implements ActionListener, KeyListener {
    static JPanel testlabel = new JPanel();

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    public JScrollPane waiterPane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    
    private JPanel view = new JPanel();
    private JPanel waiterView = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private List<JButton> waiterList = new ArrayList<JButton>();

    private JButton addPersonB = new JButton("Add Customer");
    private JButton addWaiter = new JButton("New Waiter");
    private JButton pauseButton = new JButton("Pause");
    private JButton drainCook = new JButton("Drain Cook");
    private JButton drainMoney = new JButton("Drain Cash");
    private JButton addMoney = new JButton ("Add Cash");

    private RestaurantPanel5 restPanel;

    JTextField namel = new JTextField("");
    JTextField waiterName = new JTextField("");
    public JCheckBox hungryl = new JCheckBox();
    private String type;
        

  public void keyPressed(KeyEvent e) {
	  if (e.getSource()==namel){
		  hungryl.setEnabled(true);
	  }
  }
    
  public void keyReleased(KeyEvent e){
	  
  }
  
  public void keyTyped(KeyEvent e){
	  
  }
    
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPanel5 rp, String type) {
    	
    	
        restPanel = rp;
        this.type = type;

        //ADDED CODE HERE
        hungryl.setVisible(true);
        //hungryl.addActionListener(restPanel);
        namel.addKeyListener(this); 
        hungryl.setText("Hungry?");
  
        
        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addPersonB.addActionListener(this);
        addWaiter.addActionListener(this);
        pauseButton.addActionListener(this);
        drainCook.addActionListener(this);
        drainMoney.addActionListener(this);
        addMoney.addActionListener(this);
     
        
        //ADDED CODE
        waiterView.setLayout(new BoxLayout((Container) waiterView, BoxLayout.Y_AXIS));
        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        pane.setPreferredSize(new Dimension(200,75));
        waiterPane.setViewportView(waiterView);
        waiterPane.setPreferredSize(new Dimension(200,75));
        
        testlabel.setLayout(new FlowLayout());
        Dimension test = new Dimension(100,20);
        Dimension button = new Dimension(200,15);
        Dimension thinbutton = new Dimension (100, 15);
        namel.setPreferredSize(test);
        testlabel.add(namel);
        testlabel.add(hungryl);
        testlabel.setPreferredSize(new Dimension(200,100));
        addPersonB.setPreferredSize(button);
        testlabel.add(addPersonB);
        testlabel.add(pane);
        pauseButton.setPreferredSize(button);
        testlabel.add(pauseButton);

        testlabel.add(waiterName);
        waiterName.setPreferredSize(test);
        addWaiter.setPreferredSize(thinbutton);
        testlabel.add(addWaiter);
        testlabel.add(waiterPane);
        drainCook.setPreferredSize(button);
        testlabel.add(drainCook);
        drainMoney.setPreferredSize(thinbutton);
        testlabel.add(drainMoney);
        addMoney.setPreferredSize(thinbutton);
        testlabel.add(addMoney);
        add(testlabel);
        
        hungryl.setEnabled(false);
        

       // add(pane);
        
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
          //  addPerson(JOptionPane.showInputDialog("Please enter a name:"));
        	addPerson(namel.getText());
        	hungryl.setEnabled(false);
        	namel.setText("");
     
        }
        else if (e.getSource()== addWaiter){
        	addWaiter(waiterName.getText());
        }
        
        else if (e.getSource()== pauseButton){
        	System.out.println("pauseButtom");
        	restPanel.pauseAgents();
        }
        else if (e.getSource()==drainCook){
        	restPanel.drainCook();
        }
        else if (e.getSource()==addMoney){
        	restPanel.addMoneyCashier();
        }
        else if (e.getSource()==drainMoney){
        	restPanel.drainCashier();
        }
        else {
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        	for (JButton temp:waiterList){
        		if (e.getSource()==temp)
        			restPanel.showInfo("Waiters", temp.getText());
        	}
        }
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();    
            int buttonSizew = paneSize.width - 20;
            int buttonSizeh = paneSize.height/6;
            
            Dimension buttonSize = new Dimension(buttonSizew,
                    (int) (buttonSizeh));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            
            restPanel.addPerson(type, name);//puts customer on list
            
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
            if (hungryl.isSelected()){
            	restPanel.connectButton();
            }
        }
    }
    
    public void addWaiter(String name){
    	if (name != null){
    		JButton button = new JButton(name);
    		button.setBackground(Color.white);
    		Dimension paneSize = waiterPane.getSize();
    		int buttonSizew = paneSize.width - 20;
    		int buttonSizeh = paneSize.height/7;
    		Dimension buttonSize = new Dimension(buttonSizew, (int) (buttonSizeh));
    		button.setPreferredSize(buttonSize);
    		button.setMinimumSize(buttonSize);
    		button.setMaximumSize(buttonSize);
    		button.addActionListener(this);
    		
    		waiterList.add(button);
    		waiterView.add(button);
    		
        	restPanel.addSDWaiter(name);
    		restPanel.showInfo("Waiters", name);
    		validate();
    	}
    }
}
