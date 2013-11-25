package person;
/*
 * A class encapsulating info about a location, a place on the city map. This construct simplifies contacting and
 * navigating to certain locations as the person and determining which actions to take on entrance. 
 *
 * @author Grant Collins 
 */
public class Location {

	protected String name; 
	public enum LocationType {Restaurant, Bank, Market, Home, BusStop};
	public LocationType type;
	public Position position; 
	Location(String name, LocationType type, Position p){
		this.name = name;
		this.type = type;
		this.position = p;
	}
	public Location(){}//Blank constructor for copying etc
	public String getName(){ return this.name; }
	public LocationType getType(){ return this.type; }
	public Position getPosition(){ return this.position; }
	protected void setPosition(Position p){ this.position = p; } 

}
