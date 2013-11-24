package person;

public class Apartment extends Location{
	
	private PersonAgent host;
	
	public Apartment(String n, PersonAgent person, Position p, LocationType type){
		super(n, type, p);
		this.host = person;
	}
	public PersonAgent getHost(){ return host; }
	public void setHost(PersonAgent h){ this.host = h; }
}
