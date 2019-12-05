package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Point;

public class Position {
	public double latitude;
	public double longitude;
	public double h_score;
	public Direction dirToGetHere;
	
	
	// The maths and variable names for finding these values follow the course-work specification
	// Initialising return values
	
	// Different width changes for each step in direction from N to E
	private static final double w2 = Drone.moveRange * Math.cos(Math.toRadians(67.5));
	private static final double w3 = Drone.moveRange * Math.cos(Math.toRadians(45));
	private static final double w4 = Drone.moveRange * Math.cos(Math.toRadians(22.5));
	
	// Different height changes for each step in direction from N to E
	private static final double h2 = Drone.moveRange * Math.sin(Math.toRadians(67.5));
	private static final double h3 = Drone.moveRange * Math.sin(Math.toRadians(45));
	private static final double h4 = Drone.moveRange * Math.sin(Math.toRadians(22.5));
	
	public Position(double latitude, double longitude) { 
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position(Point point) {
		this.latitude = point.latitude();
		this.longitude = point.longitude();
	}
	
	public Position nextPosition(Direction direction) {
		// The maths and variable names for finding these values follow the course-work specification
		
		// Initialising return values
		double x1 = this.longitude;
		double y1 = this.latitude;
		
		/* Directions outside of the N to E quadrant use the same magnitude for 
		 * distance changes, but a different direction.
		 * Going South means you subtract the corresponding height change instead of adding it.
		 * Going West means you subtract the corresponding width instead of adding.
		*/
		
		// Calculate width and height change based on direction
		switch(direction) {
		case N:
			y1 += Drone.moveRange;
			break;
		case NNE:
			x1 += w2;
			y1 += h2;
			break;
		case NE:
			x1 += w3;
			y1 += h3;
			break;
		case ENE:
			x1 += w4;
			y1 += h4;
			break;
		case E:
			x1 += Drone.moveRange;
			break;
		case ESE:
			x1 += w4;
			y1 -= h4;
			break;
		case SE:
			x1 += w3;
			y1 -= h3;
			break;
		case SSE:
			x1 += w2;
			y1 -= h2;
			break;
		case S:
			y1 -= Drone.moveRange;
			break;
		case SSW:
			x1 -= w2;
			y1 -= h2;
			break;
		case SW:
			x1 -= w3;
			y1 -= h3;
			break;
		case WSW:
			x1 -= w4;
			y1 -= h4;
			break;
		case W:
			x1 -= Drone.moveRange;
			break;
		case WNW:
			x1 -= w4;
			y1 += h4;
			break;
		case NW:
			x1 -= w3;
			y1 += h3;
			break;
		case NNW:
			x1 -= w2;
			y1 += h2;
			break;
		}
		
		Position newPosition = new Position(y1, x1);
		return newPosition;
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
	
	// Pythagorean distance from one point to the other
	public static double pythDistanceFrom(Point p, Point q) {
		double dist = Math.sqrt(
				Math.pow(p.latitude() - q.latitude(), 2) 
				+ Math.pow(p.longitude() - q.longitude(), 2));
		
		return(dist);
	}
}
