package uk.ac.ed.inf.powergrab;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

public class StatelessDrone extends Drone {

	public StatelessDrone(Position curPos, int seed) {
		super(curPos, seed);
	}
	
	// TODO Decide on this: For testing, possibly remove
	public StatelessDrone(double power, double coins, Position pos, int seed) {
		super(power, coins, 0, pos, seed);
	}
	
	public Feature nearestFeature(ArrayList<Feature> featureList) {
		
		double shortestDistance = 0;
		Feature closestFeature = featureList.get(0);
		
		for (Feature feat : featureList) {
			Point point = (Point) feat.geometry();
			Position p = new Position(point.latitude(), point.longitude());
			
			double dist = getCurPos().pythDistanceFrom(p);
			
			if (dist < shortestDistance) {
				shortestDistance = dist;
				closestFeature = feat;
			}
		}
		
		return(closestFeature);
	}

	@Override
	public void searchStrategy(ArrayList<Feature> featureList) {
		if (getPower() < 1.25 || getMoves() > 250) {
			return;
		}
		
		ArrayList<AbstractMap.SimpleEntry<Feature, Direction>> goodStationsInRange = 
				new ArrayList<AbstractMap.SimpleEntry<Feature, Direction>>();
		ArrayList<AbstractMap.SimpleEntry<Feature, Direction>> badStationsInRange = 
				new ArrayList<AbstractMap.SimpleEntry<Feature, Direction>>();
		HashSet<Direction> avoidDirs = new HashSet<Direction>();  
		
		for (Direction dir : Direction.values()) {
			
			Position nextPos = getCurPos().nextPosition(dir);
			Feature closestFeature = Map.nearestFeature(featureList, nextPos);
			Point closestFeaturePoint = (Point) closestFeature.geometry();
			Position closestFeaturePos = new Position(closestFeaturePoint.latitude(), closestFeaturePoint.longitude());
			
			boolean inRange = nextPos.pythDistanceFrom(closestFeaturePos) <= 0.00025;
			boolean goodClosestFeature = closestFeature.getProperty("marker-symbol").getAsString().equals("lighthouse");
			boolean badClosestFeature = closestFeature.getProperty("marker-symbol").getAsString().equals("danger");
						
			if (inRange) {
				if (goodClosestFeature) {
					AbstractMap.SimpleEntry<Feature, Direction> tup = new AbstractMap.SimpleEntry<>(closestFeature, dir);
					goodStationsInRange.add(tup);
				}
				else if (badClosestFeature) {
					AbstractMap.SimpleEntry<Feature, Direction> tup = new AbstractMap.SimpleEntry<>(closestFeature, dir);
					badStationsInRange.add(tup);
					avoidDirs.add(dir);
				}
			}
		}
		
		// Picking the best value based on adding together the power and coins at each station
		// and choosing the station with the max value
		double bestVal = 0;
		int index = 0;
		Direction nextDir;
		
		if (goodStationsInRange.size() > 0) {
			for (int i=0; i < goodStationsInRange.size(); i++) {
				AbstractMap.SimpleEntry<Feature, Direction> station = goodStationsInRange.get(i);
				double stationCoins = station.getKey().getProperty("coins").getAsDouble();
				double stationPower = station.getKey().getProperty("power").getAsDouble();
				double stationVal = stationCoins + stationPower;
				
				if (stationVal > bestVal) {
					bestVal = stationVal;
					index = i;
				}
			}
			
			nextDir = goodStationsInRange.get(index).getValue();
		}
		else if (badStationsInRange.size() == 16) {
			for (int i=0; i < goodStationsInRange.size(); i++) {
				AbstractMap.SimpleEntry<Feature, Direction> station = goodStationsInRange.get(i);
				double stationCoins = station.getKey().getProperty("coins").getAsDouble();
				double stationPower = station.getKey().getProperty("power").getAsDouble();
				double stationVal = stationCoins + stationPower;
				
				if (stationVal > bestVal) {
					bestVal = stationVal;
					index = i;
				}
			}
			
			nextDir = badStationsInRange.get(index).getValue();
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
		searchStrategy(featureList);
		
	}

}
