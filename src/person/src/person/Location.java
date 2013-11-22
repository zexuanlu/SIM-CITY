package person;

import person.interfaces.Host;
import agent.Agent;
import agent.Role;

public class Location {

	protected String name;
	public enum LocationType {Restaurant, Bank, Market, Home};
	protected LocationType type;
	protected Position position;
	Location(String name, LocationType type, Position p){
		this.name = name;
		this.type = type;
		this.position = p;
	}
	Location(){}//Blank constructor for copying etc
	public String getName(){ return this.name; }
	public LocationType getType(){ return this.type; }
	protected Position getPosition(){ return this.position; }
	protected void setPosition(Position p){ this.position = p; } 

}
