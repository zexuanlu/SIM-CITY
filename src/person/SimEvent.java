package person;
/*
 * An event for the PersonAgent. With all information for one occurrence in a simperson's day
 * These events will be ordered by priority and time.
 * 
 * @author Grant Collins
 */
public class SimEvent {
	String directive;
    public Location location;
    public enum EventType {HostEvent, CustomerEvent, WaiterEvent, CookEvent, CashierEvent, 
    						EmployeeEvent, TellerEvent, GuardEvent, PassengerEvent, HomeEvent, 
    						BusEvent, AptTenantEvent, HomeOwnerEvent, LandlordEvent, 
    						MaintenenaceEvent} 
    public EventType type;
    int priority; // for ordering in queue
    int start; 
    boolean inProgress; 
    
    public SimEvent(Location location, int priority, int start, EventType type ) {
    	
    	this.location = location;
    	this.priority = priority;
    	this.start = start;
    	this.inProgress = false;
    	this.type = type;	
    }
    /*
     * create a time ambiguous event that will be ordered on the fly 
     * with regards to existing mandatory events
     */
    public SimEvent(String directive, Location l, int p, EventType t){ 
    	this.directive = directive;
    	this.location = l;
    	this.start = 0;
    	this.priority = p;
    	this.type = t;
    	this.inProgress = false;
    }
    public String getDirective(){
    	return this.directive;
    }
    void setLocation(Location l) {
    	this.location = l;
    }
    
    void setLocation(Position p) {   	
    	this.location.setPosition(p);
    }
    
    Location getLocation() {   	
    	return location;    	
    }
    
    public void setStart(int s) {    	
    	this.start = s;
    }
   
    int getStart() {   	
    	return this.start;    	
    }
    
    void setProgress(boolean inProgress) {    	
    	this.inProgress = inProgress;
    }
    
    boolean getProgress() {   	
    	return inProgress;
    }
    
}