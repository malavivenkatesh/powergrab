package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

import com.mapbox.geojson.Feature;

public class StatelessDrone extends Drone {

	public StatelessDrone() {
		super();
	}

	public StatelessDrone(double power, double coins) {
		super(power, coins);
	}

	@Override
	public void searchStrategy(ArrayList<Feature> featureList) {
		
	}

}
