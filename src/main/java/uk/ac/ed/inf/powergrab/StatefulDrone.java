package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mapbox.geojson.Point;

public class StatefulDrone extends Drone {
	
	public StatefulDrone(Point curPos, int seed) {
		super(curPos, seed);
	}

	public StatefulDrone(double power, double coins, Point pos, int seed) {
		super(power, coins, 0, pos, seed);
	}
	
	@Override
	public void searchStrategy(List<ChargingStation> stations) {
		// End conditions
		if (getPower() < 1.25 || getMoves() >= 250) {
			return;
		}
		
		// Automatically charge after every move
		inRangeOfStation(stations);	
		
		
		List<ChargingStation> goodStations = stations.
				stream().filter(station -> station.isGood()).
				collect(Collectors.toList());
		List<ChargingStation> badStations =  stations.
				stream().filter(station -> !station.isGood()).
				collect(Collectors.toList());
		
		ChargingStation nearestStation = Map.nearestFeature(goodStations, getCurPos());
		
			
	}
	
	public static void main() {
//		System.out.print(Direction.values());
	}

}
