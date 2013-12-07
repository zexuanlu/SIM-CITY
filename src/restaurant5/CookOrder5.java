package restaurant5;
import restaurant5.interfaces.Waiter5;

public class CookOrder5 {
	Waiter5 w; 
	String choice; 
	int tablenum; 
	public CookOrder5(Waiter5 _w, String _c, int _t){
		w = _w; 
		choice = _c; 
		tablenum = _t; 
	}
}
