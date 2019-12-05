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
import com.mapbox.geojson.Point;

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
        Point startPos = Point.fromLngLat(longitude, latitude);
        
        Drone drone;
        
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
		
        System.out.println();
        System.out.printf("Total    Coins: %f, Total    Power: %f\n", coinSum, powerSum);
        System.out.printf("Gathered Coins: %f, Gathered Power: %f\n", drone.getCoins(), drone.getPower());
        return;
        
    }
    
	public static List<Feature> getFeatures(String year, String month, String day) {
		String stringUrl = formUrlString(year, month, day);
		String gjson = getNetwork(stringUrl);
		List<Feature> features = stringToFeatures(gjson);
		
		return(features);
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
}
