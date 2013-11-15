package person;

import agent.Agent;

public class Location {

	private String building;
	private Agent contact;
	enum LocationType {Restaurant, Bank, Market, Home};
	LocationType type;
	private int x;
	private int y;
	
	Location(String buildingName, Agent contact, LocationType type, int id, int x, int y){
		
		this.building = buildingName;
		this.contact = contact;
		this.type = type;
		this.x = x;
		this.y = y;
		
	}
	Location(){
		
	}
	void setX(int x){
		this.x = x;
	}
	void setY(int y){
		this.y = y;
	}
	void setType(LocationType type){
		this.type = type;
	}
	int getX(){
		return x;
	}
	int getY(){
		return y;
	}
	LocationType getType(){
		return type;
	}
	Agent getContact(){
		return this.contact;
	}
	String getName(){
		return this.building;
	}
	

}
