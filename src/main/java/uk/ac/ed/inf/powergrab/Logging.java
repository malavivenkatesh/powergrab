package uk.ac.ed.inf.powergrab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;

public class Logging {
	
	public static void logToGJson(ArrayList<Feature> featureList) {
		Gson g = new Gson();
        try {
            String jsonString = g.toJson(featureList);
            FileWriter writer = new FileWriter(".\\logs\\example.geojson");
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		Logging.logToGJson((ArrayList<Feature>) Map.getFeatures("2019", "01", "01"));
		
	}
}
