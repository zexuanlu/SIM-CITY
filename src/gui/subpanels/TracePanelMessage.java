package gui.subpanels;

import agent.Agent;
/**
 * TracePanelMessage represents a message and contains the agent
 * that sent the message.
 */

public class TracePanelMessage {
	
	// Agent that sends the message
	Agent agent;
	
	// Message
	String message;

	public TracePanelMessage() {
		
	}
	
	public TracePanelMessage(String message, Agent a) {
		this.agent = a;
		this.message = message;
	}
	
	public Agent getAgent(){
		return agent;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public void setAgent(Agent a){
		this.agent = a;
	}
}
