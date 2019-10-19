package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

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
		// TODO Auto-generated method stub
		
	}
	
//	public Feature nearestFeature(ArrayList<Feature> featureList) {
//		
//		double shortestDistance = 0;
//		Feature closestFeature = featureList.get(0);
//		
//		for (Feature feat : featureList) {
//			Point point = (Point) feat.geometry();
//			Position p = new Position(point.latitude(), point.longitude());
//			
//			double dist = getCurPos().pythDistanceFrom(p);
//			
//			if (dist < shortestDistance) {
//				shortestDistance = dist;
//				closestFeature = feat;
//			}
//		}
//		
//		return(closestFeature);
//	}

}
