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
		addPathTrace(this.getCurPos());
		
		if (getPower() < 1.25 || getMoves() >= 250) {
			System.out.print(getCoins());

			return;
		}
		
		// Automatically charge after every move
		boolean charged = inRangeOfStation(stations);
		
		List<ChargingStation> goodStationsInRange = new ArrayList<ChargingStation>();
		List<Direction> goodStationDirs = new ArrayList<Direction>();
		
		List<ChargingStation> badStationsInRange = new ArrayList<ChargingStation>();
		List<Direction> badStationDirs = new ArrayList<Direction>();
		
		Set<Direction> avoidDirs = new HashSet<Direction>();

		
		for (Direction dir : Direction.values()) {
			Position pos = new Position(getCurPos());
			Position nextPos = pos.nextPosition(dir);
//			ChargingStation closestFeature = Map.nearestFeature(stations, nextPos);
			Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
			ChargingStation closestFeature = Map.nearestFeature(stations, nextPoint);
			
			boolean inRange = Position.pythDistanceFrom(nextPoint, closestFeature.getPosition()) < 0.00025;
			
//			System.out.println("Dir: " + dir.toString() + " inRange: " + inRange + " LatLng: " + nextPoint.coordinates() + " isGood: " + closestFeature.isGood());
			if (inRange) {
				if (closestFeature.isGood() && closestFeature.getCoins() > 0 && closestFeature.getPower() > 0) {
					goodStationsInRange.add(closestFeature);
					goodStationDirs.add(dir);
				}
				else if (!closestFeature.isGood()) {
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
		
		// Compare the numerical value of the station by adding power and coins together
		Comparator<ChargingStation> compCoinsAndPower = 
				Comparator.comparing(x-> x.getCoins() + x.getPower());
		
		// Pick the best good station in range
		if (goodStationsInRange.size() > 0) {			
			ChargingStation bestStation = Collections.max(goodStationsInRange, compCoinsAndPower);
			index = goodStationsInRange.indexOf(bestStation);
			nextDir = goodStationDirs.get(index);
		}
		// Or the least bad station if surrounded by bad stations
		else if (badStationsInRange.size() == 16) {
			ChargingStation bestStation = Collections.max(badStationsInRange, compCoinsAndPower);
			index = badStationsInRange.indexOf(bestStation);
			nextDir = badStationDirs.get(index);
		}
		// Otherwise move so not in range of any bad station
		else {
			Set<Direction> possibleDirs = new HashSet<Direction>();
			Collections.addAll(possibleDirs, Direction.values());
			
			possibleDirs.removeAll(avoidDirs);
			
			int possibilities = possibleDirs.size();
			int randomInt = getRnd().nextInt(possibilities);
			nextDir = (Direction) possibleDirs.toArray()[randomInt];			
		}
		
		
		Point prevPos = getCurPos();
		move(nextDir);
		Logging.logToTxt(prevPos, getCurPos(), nextDir, getCoins(), getPower());
		
//		System.out.println("Moves: " + getMoves() +  " Power: " + getPower() + " Next Direction: " + nextDir.toString());
//		avoidDirs.forEach(dir -> System.out.print(dir.toString() + " "));
//		System.out.println(badStationsInRange.size());
//		badStationsInRange.forEach(station -> System.out.print(station.getId() + " " + station.getPosition().latitude() + " " + station.getPosition().longitude()));
//		System.out.println();
//		badStationDirs.forEach(dir -> System.out.print(dir.toString() + " "));
//		System.out.println();
//		goodStationsInRange.forEach(station -> System.out.print(station.getId() + " " + station.getPosition().latitude() + " " + station.getPosition().longitude()));
//		System.out.println();
//		goodStationDirs.forEach(dir -> System.out.print(dir.toString() + " "));
//		System.out.println();
		
		searchStrategy(stations);
		
	}

}
