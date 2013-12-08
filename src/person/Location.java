package person;
/*
 * A class encapsulating info about a location, a place on the city map. This construct simplifies contacting and
 * navigating to certain locations as the person and determining which actions to take on entrance. 
 *
 * @author Grant Collins 
 */
public class Location {

        protected String name; 
        public enum LocationType {Bank, Market, Home, Apartment, BusStop, Restaurant};
        public LocationType type;
        protected boolean isClosed;
        protected int Quadrant;
        public Position position; 
        public Location(String name, LocationType type, Position p){
                this.name = name;
                this.type = type;
                this.position = p;
        		if(p.getX() < 280 && p.getY() < 220)
        			this.Quadrant = 1;
        		else if(p.getX() > 280 && p.getY() < 220)
        			this.Quadrant = 2;
        		else if(p.getX() < 280 && p.getY() > 220)
        			this.Quadrant = 3;
        		else if(p.getX() > 280 && p.getY() > 220)
        			this.Quadrant = 4;
        }
        public Location(){}//Blank constructor for copying etc
        public String getName(){ return this.name; }
        public LocationType getType(){ return this.type; }
        public int getQuadrant(){return this.Quadrant; }
        public boolean isClosed(){return this.isClosed;}
        public void setClosed(boolean b){ this.isClosed = b;}
        public Position getPosition(){ return this.position; }
        public void setPosition(Position p){ this.position = p; } 
}