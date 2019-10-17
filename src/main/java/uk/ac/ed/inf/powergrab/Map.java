package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

public class Map {
	
	public static String formUrlString(String year, String month, String day) {
		String startPath = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
		String endPath = "powergrabmap.geojson";
		
		return(String.join("/", startPath, year, month, day, endPath));
	}
	
	// Function either returns string of geojson or empty string, if error occurs
	public static String getNetwork(String stringUrl) {
		String gjson = "";
		URL mapUrl;
		try {
			mapUrl = new URL(stringUrl);
			HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			
			InputStream inputStream = conn.getInputStream();
			
		    Scanner scanner = new Scanner(inputStream);
		    
		    while (scanner.hasNext()) {
		    	gjson += scanner.next();
		    }
		    scanner.close();
		    conn.disconnect();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return gjson;
	}
	
	// TODO add logging functionality so geojson file can be copied and stored
	public static List<Feature> stringToFeatures(String gjson) {
		FeatureCollection ft = FeatureCollection.fromJson(gjson);
		List<Feature> features = ft.features();
	    
	    return (features);
	}
	
	// returns list of features from a map for a specified day
	public static List<Feature> getFeatures(String year, String month, String day) {
		// executes relavent functions in the correct order to return feature list
		String stringUrl = formUrlString(year, month, day);
		String gjson = getNetwork(stringUrl);
		List<Feature> features = stringToFeatures(gjson);
		
		return(features);
	}
	
	public static void main( String[] args ) {
//		String urlPath = "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson";
//		try {
//			URL mapUrl = new URL(urlPath);
//			HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
//			conn.setReadTimeout(10000);
//			conn.setConnectTimeout(15000);
//			conn.setRequestMethod("GET");
//			conn.setDoInput(true);
//			conn.connect();
//			
//			InputStream inputStream = conn.getInputStream();
//			
//		    String text = "";
//		    Scanner scanner = new Scanner(inputStream);
//		    
//		    while (scanner.hasNext()) {
//		    	text += scanner.next();
//		    }
//		    scanner.close();
//		    conn.disconnect();
//		    
//		    FeatureCollection ft = FeatureCollection.fromJson(text);
//		    
//		    List<Feature> feat = ft.features();
//		    System.out.print(feat.get(0));
//		    
//		    
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
				
	}
}
