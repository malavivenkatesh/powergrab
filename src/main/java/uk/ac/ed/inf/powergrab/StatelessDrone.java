package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

import com.mapbox.geojson.Feature;

public class StatelessDrone extends Drone {

	public StatelessDrone(Position curPos, int seed) {
		super(curPos, seed);
	}
	
	// TODO Decide on this: For testing, possibly remove
	public StatelessDrone(double power, double coins, Position pos, int seed) {
		super(power, coins, 0, pos, seed);
	}

	@Override
	public void searchStrategy(ArrayList<Feature> featureList) {
		
	}

}
