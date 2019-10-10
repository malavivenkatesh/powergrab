package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) { 
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) {
		return null;
	}
	
	public boolean inPlayArea() {
		
		// Note that the boundary is not in the play area
		if(this.latitude >= 55.946233 || this.latitude <= 55.942617) {
			return false;
		}
		
		if(this.longitude >= -3.184319 || this.longitude <= -3.192473) {
			return false;
		}
		
		return true;
	}
}
