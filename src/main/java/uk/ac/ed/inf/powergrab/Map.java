package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

public final class Map {
	
	// Stations in the map for a specified date
	private static List<ChargingStation> stations;
	
	public static List<ChargingStation> getStations() {
		return stations;
	}
	
	/**
	 * Sets the stations variable
	 * @param features - list of features from server
	 */
	public static void setStations(List<Feature> features) {
		List<ChargingStation> stations = new ArrayList<ChargingStation>();
	    for (Feature feature : features) {
	    	double coins = feature.getProperty("coins").getAsDouble();
	    	double power = feature.getProperty("power").getAsDouble();
	    	boolean isGood = coins > 0 && power > 0;
	    	String id = feature.getProperty("id").getAsString();
	    	Position pos = new Position((Point) feature.geometry());
	    	ChargingStation station = new ChargingStation(pos, coins, power, isGood, id); 
	    	stations.add(station);
	    }
	    Map.stations  = stations;
	    return;
	}
	
	/**
	 * Gets the closest charging station to a given position
	 * @param stations - a list of stations to search
	 * @param pos - the position to search from
	 * @return - the nearest charging station
	 */
	public static ChargingStation nearestFeature(List<ChargingStation> stations, Position pos) {
		
		double shortestDistance = Integer.MAX_VALUE;
		ChargingStation nearestFeature = stations.get(0);
		
		for (ChargingStation station : stations) {
			
			double dist = pos.pythDistanceFrom(station.getPos());
			
			if (dist < shortestDistance) {
				shortestDistance = dist;
				nearestFeature = station;
			}
		}
		
		return(nearestFeature);
	}
	
	/**
	 * Checks whether two points are in range of each other
	 * @param p - the first point to compare
	 * @param q - the second point to compare
	 * @return - true or false based on whether the positions are in range
	 */
	public static boolean inRange(Position p, Position q) {
		if (p.pythDistanceFrom(q) > ChargingStation.chargeRange) {
			return(false);
		}
		else {
			return(true);
		}
	}
	
}
