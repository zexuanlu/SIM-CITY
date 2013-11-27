package person;

public class Apartment extends Location{
	
	private PersonAgent host;
	int homeNumber;
	public Apartment(String n, PersonAgent person, Position p, int hn, LocationType type){
		super(n, type, p);
		this.host = person;
		this.homeNumber = hn;
	}
	public PersonAgent getHost(){ return host; }
	public void setHost(PersonAgent h){ this.host = h; }
	public int getNumber(){ return this.homeNumber; }
}
