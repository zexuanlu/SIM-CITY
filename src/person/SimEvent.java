package person;
/*
 * An event for the PersonAgent. With all information for one occurrence in a simperson's day
 * These events will be ordered by priority and time.
 * 
 * @author Grant Collins
 */
public class SimEvent {
	public String directive;
    public Location location;
    public enum EventImportance {RecurringEvent, OneTimeEvent, EmergencyEvent}
    public enum EventType {HostEvent, CustomerEvent, WaiterEvent, SDWaiterEvent, CookEvent, CashierEvent, 
    						EmployeeEvent, TellerEvent, GuardEvent, PassengerEvent, HomeEvent, 
    						BusEvent, AptTenantEvent, HomeOwnerEvent, LandlordEvent, 
    						MaintenenaceEvent} 
    public EventType type;
    public EventImportance importance;
    int startTime;  
    
    public SimEvent(Location location, int startTime, EventType type ) {
    	this.importance = EventImportance.RecurringEvent;
    	this.location = location;
    	this.startTime = startTime;
    	this.type = type;	
    }
    /*
     * create a time ambiguous event that will be ordered on the fly 
     * with regards to existing mandatory events
     */
    public SimEvent(String directive, Location l, EventType t){ 
    	this.directive = directive;
    	this.location = l;
    	this.importance = EventImportance.OneTimeEvent;
    	this.startTime = -1;
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
    
    public void setstartTime(int s) {    	
    	this.startTime = s;
    }
   
    int getstartTime() {   	
    	return this.startTime;    	
    }
    
}