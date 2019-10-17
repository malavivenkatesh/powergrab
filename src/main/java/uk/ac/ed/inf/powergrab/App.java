package uk.ac.ed.inf.powergrab;

import java.util.List;

import com.mapbox.geojson.Feature;

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
        
        String year = args[0];
        String month = args[1];
        String day = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        String state = args[6];
        
        List<Feature> featureList = Map.getFeatures(year, month, day);
        Position startPos = new Position(latitude, longitude);
        
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
        
        
    }
}
