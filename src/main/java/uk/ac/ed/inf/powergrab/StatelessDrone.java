package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mapbox.geojson.Point;

public class StatelessDrone extends Drone {

	public int iter = 0;
	
	public StatelessDrone(Point curPos, int seed) {
		super(curPos, seed);
	}
	
	// TODO Decide on this: For testing, possibly remove
	public StatelessDrone(double power, double coins, Point pos, int seed) {
		super(power, coins, 0, pos, seed);
	}

	@Override
	public void searchStrategy(List<ChargingStation> stations) {
		if (getPower() < 1.25 || getMoves() >= 250) {
			return;
		}
		
		// Automatically charge
		inRangeOfStation(stations);		
		
		List<ChargingStation> goodStationsInRange = new ArrayList<ChargingStation>();
		List<Direction> goodStationDirs = new ArrayList<Direction>();
		
		List<ChargingStation> badStationsInRange = new ArrayList<ChargingStation>();
		List<Direction> badStationDirs = new ArrayList<Direction>();
		
		Set<Direction> avoidDirs = new HashSet<Direction>();

		
		for (Direction dir : Direction.values()) {
			Position pos = new Position(getCurPos());
			Position nextPos = pos.nextPosition(dir);
			ChargingStation closestFeature = Map.nearestFeature(stations, nextPos);
			Position closestFeaturePos = new Position(closestFeature.getPosition());
			
			boolean inRange = Position.pythDistanceFrom(pos, closestFeaturePos) <= 0.00025;
						
			if (inRange) {
				if (closestFeature.isGood()) {
					goodStationsInRange.add(closestFeature);
					goodStationDirs.add(dir);
				}
				else {
					badStationsInRange.add(closestFeature);
					badStationDirs.add(dir);
					avoidDirs.add(dir);
				}
			}
			if (!nextPos.inPlayArea()) {
				avoidDirs.add(dir);
			}
		}
		
		// Picking the best value based on adding together the power and coins at each station
		// and choosing the station with the max value
		int index;
		Direction nextDir;
		
		Comparator<ChargingStation> compCoinsAndPower = 
				Comparator.comparing(ChargingStation::getCoins).thenComparing(ChargingStation::getPower);
		
		if (goodStationsInRange.size() > 0) {			
			ChargingStation bestStation = Collections.max(goodStationsInRange, compCoinsAndPower);
			index = goodStationsInRange.indexOf(bestStation);
			nextDir = goodStationDirs.get(index);
		}
		else if (badStationsInRange.size() == 16) {
			ChargingStation bestStation = Collections.max(badStationsInRange, compCoinsAndPower);
			index = badStationsInRange.indexOf(bestStation);
			nextDir = badStationDirs.get(index);
		}
		else {
			Set<Direction> possibleDirs = new HashSet<Direction>();
			Collections.addAll(possibleDirs, Direction.values());
			
			possibleDirs.removeAll(avoidDirs);
			
			int possibilities = possibleDirs.size();
			int randomInt = getRnd().nextInt(possibilities);
			nextDir = (Direction) possibleDirs.toArray()[randomInt];			
		}
		
		move(nextDir);
		searchStrategy(stations);
	}

}
