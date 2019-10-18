package uk.ac.ed.inf.powergrab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Logging {
	
	public static ArrayList<Point> positionToPoint(ArrayList<Position> posArray) {
		ArrayList<Point> pointArray = new ArrayList<Point>();
		
		for (Position pos: posArray) {
			Point p = Point.fromLngLat(pos.longitude, pos.latitude);
			pointArray.add(p);
		}
		
		return(pointArray);
	}
	
	public static void logToGJson(ArrayList<Feature> featureList, ArrayList<Position> dronePathTrace) {
		Gson g = new Gson();
		
		// Adding drone's path to the feature list for required log file
		ArrayList<Point> dronePathTracePoint = Logging.positionToPoint(dronePathTrace);
		LineString lineTrace = LineString.fromLngLats(dronePathTracePoint);
		Feature lineTraceFeature = Feature.fromGeometry(lineTrace);
		featureList.add(lineTraceFeature);
		
        try {
        	FeatureCollection featureCol = FeatureCollection.fromFeatures(featureList);
            String jsonString = featureCol.toJson();
            FileWriter writer = new FileWriter("./logs/example.geojson");
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
//		Logging.logToGJson((ArrayList<Feature>) Map.getFeatures("2019", "01", "01"));
	}
}
