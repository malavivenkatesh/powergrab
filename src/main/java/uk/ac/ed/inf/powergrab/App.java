package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

public class App 
{
    public static void main( String[] args ) {
    	// Error for argument parsing
        if (args.length < 7) {
        	System.out.println("Not enough arguments.");
        	System.out.println("Aruments needed:");
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
        
        List<Feature> featureList = Map.getFeatures(year, month, day);
        List<ChargingStation> stations = Map.getStations(featureList);
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
        
		List<ChargingStation> goodStations = stations.
				stream().filter(station -> station.isGood() && !station.isVisited()).
				collect(Collectors.toList());
		
		double sum = 0;
		for (ChargingStation station: goodStations) {
			sum += station.getCoins();
		}
		
		double sumP = 0;
		for (ChargingStation station: goodStations) {
			sumP += station.getPower();
		}
	
		
		try {
			Logging.setWriter(year, month, day, state);
		} catch (IOException e) {
			e.printStackTrace();
		}
        drone.searchStrategy(stations);
        Logging.logToGJson((ArrayList<Feature>) featureList, drone.getPathTrace(), 
        		year, month, day, state);
        try {
			Logging.bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        System.out.println();
        System.out.printf("Total    Coins: %f, Total    Power: %f", sum, sumP);
        System.out.println();
        System.out.printf("Gathered Coins: %f, Gathered Power: %f", drone.getCoins(), drone.getPower());
        System.out.println();
        return;
        
    }
}
