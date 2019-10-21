package uk.ac.ed.inf.powergrab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Logging {
	
	public static void logToGJson(ArrayList<Feature> featureList, ArrayList<Point> dronePathTrace, 
			String year, String month, String day, String state) {
		
		String filename = String.join("-", state, day, month, year) + ".geojson";
		
		new File("logs").mkdir();
		
		// Adding drone's path to the feature list for required log file
		LineString lineTrace = LineString.fromLngLats(dronePathTrace);
		Feature lineTraceFeature = Feature.fromGeometry(lineTrace);
		featureList.add(lineTraceFeature);
		
        try {
        	FeatureCollection featureCol = FeatureCollection.fromFeatures(featureList);
            String jsonString = featureCol.toJson();
            String filePath = String.join("/", ".", "logs", filename);
            FileWriter writer = new FileWriter(filePath);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
//		Logging.logToGJson((ArrayList<Feature>) Map.getFeatures("2019", "01", "01"));
//		for (Direction dir: Direction.values()) {
//			System.out.println(dir.toString());
//		}
	}
}
