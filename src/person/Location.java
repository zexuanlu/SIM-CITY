package person;

public class Location {

	protected String name;
	public enum LocationType {Restaurant, Bank, Market, Home, BusStop};
	public LocationType type;
	protected Position position;
	Location(String name, LocationType type, Position p){
		this.name = name;
		this.type = type;
		this.position = p;
	}
	Location(){}//Blank constructor for copying etc
	public String getName(){ return this.name; }
	public LocationType getType(){ return this.type; }
	public Position getPosition(){ return this.position; }
	protected void setPosition(Position p){ this.position = p; } 

}
