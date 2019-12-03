package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class Map {
	public static ChargingStation nearestFeature(List<ChargingStation> stations, Point pos) {
		
		double shortestDistance = Integer.MAX_VALUE;
		ChargingStation nearestFeature = stations.get(0);
		
		for (ChargingStation station : stations) {
			
			double dist = Position.pythDistanceFrom(pos, station.getPosition());
			
			if (dist < shortestDistance) {
				shortestDistance = dist;
				nearestFeature = station;
			}
		}
		
		return(nearestFeature);
	}
	
	public static boolean inRange(Point p, Point q) {
		double range = 0.00025; 
		if (Position.pythDistanceFrom(p, q) > range) {
			return(false);
		}
		else {
			return(true);
		}
	}

	private static String formUrlString(String year, String month, String day) {
		String startPath = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
		String endPath = "powergrabmap.geojson";
		
		return(String.join("/", startPath, year, month, day, endPath));
	}
	
	// Function either returns string of geojson or empty string, if error occurs
	private static String getNetwork(String stringUrl) {
		String gjson = "";
		URL mapUrl;
		try {
			mapUrl = new URL(stringUrl);
			HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			
			InputStream inputStream = conn.getInputStream();
			
		    Scanner scanner = new Scanner(inputStream);
		    
		    while (scanner.hasNext()) {
		    	gjson += scanner.next();
		    }
		    scanner.close();
		    conn.disconnect();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return gjson;
	}
	
	private static List<Feature> stringToFeatures(String gjson) {
		FeatureCollection ft = FeatureCollection.fromJson(gjson);
		List<Feature> features = ft.features();
		return(features);
		
	}
	
	public static List<ChargingStation> getStations(List<Feature> features) {
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
	    return (stations);
	}
	
	public static List<Feature> getFeatures(String year, String month, String day) {
		String stringUrl = formUrlString(year, month, day);
		String gjson = getNetwork(stringUrl);
		List<Feature> features = stringToFeatures(gjson);
		
		return(features);
	}
	
	public static void main( String[] args ) {
				
	}
}
