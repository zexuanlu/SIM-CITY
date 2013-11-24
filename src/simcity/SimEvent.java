package simcity;

public class SimEvent {
	String directive;
    Location location;
    public enum EventType {HostEvent, CustomerEvent, WaiterEvent, CookEvent, CashierEvent, 
    						EmployeeEvent, TellerEvent, GuardEvent, PassengerEvent, HomeEvent, 
    						FreeEvent, BusEvent, AptTenantEvent, HomeOwnerEvent, LandlordEvent, 
    						MaintenenaceEvent} 
    public EventType type;
    int priority; // for ordering in queue
    int start;
    int finish; 
    boolean inProgress; 
    
    public SimEvent(Location location, int priority, int start, int finish, EventType type ) {
    	
    	this.location = location;
    	this.priority = priority;
    	this.start = start;
    	this.finish = finish;
    	this.inProgress = false;
    	this.type = type;
    	
    }
    
    SimEvent(Location location, int start, int finish, EventType type ) {
    	
    	this.location = location;
    	this.start = start;
    	this.finish = finish;
    	this.inProgress = false;
    	this.priority = 5;
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
    	this.finish = 0;
    	this.priority = p;
    	this.type = t;
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
    
    void setFinish(int f) {    	
    	this.finish = f;    	
    }
    
    int getStart() {   	
    	return this.start;    	
    }
    
    int getFinish() {    	
    	return this.finish;
    }
    
    void setProgress(boolean inProgress) {    	
    	this.inProgress = inProgress;
    }
    
    boolean getProgress() {   	
    	return inProgress;
    }
    
}