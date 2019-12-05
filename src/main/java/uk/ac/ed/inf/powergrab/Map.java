package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

public final class Map {
	
	private static List<ChargingStation> stations;
	
	public static List<ChargingStation> getStations() {
		return stations;
	}
		
	public static void setStations(List<Feature> features) {
		List<ChargingStation> stations = new ArrayList<ChargingStation>();
	    for (Feature feature : features) {
	    	double coins = feature.getProperty("coins").getAsDouble();
	    	double power = feature.getProperty("power").getAsDouble();
	    	boolean isGood = coins > 0 && power > 0;
	    	String id = feature.getProperty("id").getAsString();
	    	ChargingStation station = new ChargingStation((Point) feature.geometry(), 
	    			coins, power, isGood, id); 
	    	stations.add(station);
	    }
	    Map.stations  = stations;
	    return;
	}
	
	public static ChargingStation nearestFeature(List<ChargingStation> stations, Point pos) {
		
		double shortestDistance = Integer.MAX_VALUE;
		ChargingStation nearestFeature = stations.get(0);
		
		for (ChargingStation station : stations) {
			
			double dist = Position.pythDistanceFrom(pos, station.getLocation());
			
			if (dist < shortestDistance) {
				shortestDistance = dist;
				nearestFeature = station;
			}
		}
		
		return(nearestFeature);
	}
	
	public static boolean inRange(Point p, Point q) {
		if (Position.pythDistanceFrom(p, q) > ChargingStation.chargeRange) {
			return(false);
		}
		else {
			return(true);
		}
	}
	
}
