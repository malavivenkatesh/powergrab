package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

public class App 
{
	
	
    public static void main( String[] args ) {
    	// Error for argument parsing
        if (args.length != 7) {
        	System.out.println("Wrong arguments.");
        	System.out.println("Arguments needed:");
        	System.out.println("DD MM YYYY latitude longitude randomseed state");
        	return;
        }
        
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        String state = args[6];
        
        List<Feature> featureList = getFeatures(year, month, day);
        Map.setStations(featureList);
        Position startPos = new Position(latitude, longitude);
        
        Drone drone = null;
        
        if (state.equals("stateless")) {
        	drone = new StatelessDrone(startPos, seed);
        }
        else if (state.equals("stateful")) {
        	drone = new StatefulDrone(startPos, seed);
        }
        else {
        	System.out.print("Invalid state for drone.");
        	return;
        }
        
        // List of good stations to log best possible score in map
		List<ChargingStation> goodStations = Map.getStations().
				stream().filter(station -> station.isGood() && !station.isVisited()).
				collect(Collectors.toList());
		
		double coinSum = 0;
		for (ChargingStation station: goodStations) {
			coinSum += station.getCoins();
		}
		
		double powerSum = 0;
		for (ChargingStation station: goodStations) {
			powerSum += station.getPower();
		}
	
		
		drone.initSearchStrategy(year, month, day, state);
		Logging.logToGJson(featureList, drone.getPathTrace(), year, month, day, state);
		
        System.out.println();
        System.out.printf("Total    Coins: %f, Total    Power: %f\n", coinSum, powerSum);
        System.out.printf("Gathered Coins: %f, Gathered Power: %f\n", drone.getCoins(), drone.getPower());
        return;
        
    }
    
    
    /**
     * This method is used to gain a list of features from a specified map
     * @param year - the year for the map
     * @param month - the month for the map
     * @param day - the day for the map 
     * @return - a list of features from the map for the given date
     */
	public static List<Feature> getFeatures(String year, String month, String day) {
		String stringUrl = formUrlString(year, month, day);
		String gjson = getNetwork(stringUrl);
		List<Feature> features = stringToFeatures(gjson);
		
		return(features);
	}
    
	/**
	 * Forms a string in the correct format for a URL
     * @param year - the year for the URL
     * @param month - the month for the URL
     * @param day - the day for the URL
	 * @return - a string formatted as a URL
	 */
	private static String formUrlString(String year, String month, String day) {
		String startPath = "http://127.0.0.1:1920/stg/powergrab/";
		String endPath = "powergrabmap.geojson";
		
		return(String.join("/", startPath, year, month, day, endPath));
	}
	
	/**
	 * Method used to gain a string form of the GeoJson from the server
	 * @param stringUrl - the URL to connect to
	 * @return - a string form of the GeoJson for a map
	 */
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
	
	/**
	 * Converts a string of GeoJson to a list of features
	 * @param gjson - the input String to convert
	 * @return - a list of Features for the map
	 */
	private static List<Feature> stringToFeatures(String gjson) {
		FeatureCollection ft = FeatureCollection.fromJson(gjson);
		List<Feature> features = ft.features();
		return(features);
	}
}
