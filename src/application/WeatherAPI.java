package application;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherAPI extends MainController {

	@SuppressWarnings("unchecked")

	//Starting API method
	public static  JSONObject weatherAPI(String locationName) {

		// calling for all possible locations using the getLocation method
		JSONArray locationData = getLocationData(locationName);


		//getting longitude & latitude data from JSON array
		JSONObject location = (JSONObject) locationData.get(0);
		double latitude = (double) location.get("latitude");
		double longitude = (double) location.get("longitude");


		//build API request URL using location coordinates


		//creates a string that has an HTTPS request with our latitude and longitude data 
		String urlString = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&hourly=temperature_2m,relative_humidity_2m,precipitation_probability,weather_code,wind_speed_10m&temperature_unit=fahrenheit&wind_speed_unit=mph";

		try {
			//calling API and getting response
			HttpURLConnection conn = fetchApiResponse(urlString);

			//checking for response 200ms as it means successful connection

			if (conn.getResponseCode()!= 200) {
				System.out.println("Error: Could not connect to API");
				return null;


			}
			//storing API results 
			StringBuilder resultJSON = new StringBuilder();
			Scanner input = new Scanner(conn.getInputStream());

			// reads and stores the JSON results into a string builder
			while(input.hasNext()) {

				//reading the lines of data that come in and stores it
				resultJSON.append(input.nextLine());




			}

			//closing scanner for connection input
			input.close();

			//closing connection for URL
			conn.disconnect();

			//parsing through data
			JSONParser parser = new JSONParser();
			JSONObject resultsJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

			//retrieve hourly data from JSON data
			JSONObject hourly = (JSONObject) resultsJSONObj.get("hourly");

			//getting current hour's data by getting the index of our current hour
			JSONArray time = (JSONArray) hourly.get("time");
			int index = findIndexofCurrentTime(time);

			//getting temperature data
			JSONArray temperaturedata = (JSONArray) hourly.get("temperature_2m");
			double temperature = (double) temperaturedata.get(index);

			//getting weather code (used to find weather description)
			JSONArray weatherCode = (JSONArray)  hourly.get("weather_code");
			String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

			//humidity data
			JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
			long humidity = (long) relativeHumidity.get(index);

			//precipitation data
			JSONArray precipitationData = (JSONArray) hourly.get("precipitation_probability");
			long precipitationChance = (long) precipitationData.get(index);


			// wind speed data
			JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
			double windSpeed = (double) windSpeedData.get(index);

			//building the weather JSON data  for front end using the data provided
			JSONObject weatherData = new JSONObject();
			weatherData.put("tempature" , temperature);
			weatherData.put("weather_condition" , weatherCondition);
			weatherData.put("humidity" , humidity);
			weatherData.put("wind_speed" , windSpeed);
			weatherData.put("precipitation", precipitationChance);

			//returning weatherData object
			return weatherData;












			//catches errors during execution 
		}catch(Exception e) {
			e.printStackTrace();
		}



		return null;



	}


	//converting weather code into something readable 
	private static  String convertWeatherCode(long weathercode) {
		//creating a string builder so we can add data based on the weather code given
		StringBuilder weatherCondition = new StringBuilder();

		//Checks to see if the passed in long is equal to one of the basic weather types via weather code table 
		if (weathercode == 0L) {
			//base case
			weatherCondition.append("Clear");
		}
		else if (weathercode > 0L && weathercode <= 3L) {
			//cloud case
			weatherCondition.append("Cloudy");
		}
		else if (weathercode >= 51L && weathercode <= 67L || weathercode >= 80L && weathercode <= 99L){
			//rain case
			weatherCondition.append("Rain");
		}
		else if (weathercode > 71L && weathercode <= 77L) {
			//snow case
			weatherCondition.append("Snow");
		}else {
			//in case of unknown weather code
			weatherCondition.append("Clear");
		}

		//takes the appended string and makes it equal to a normal string for return
		String condition = weatherCondition.toString();

		//return statement
		return condition;
	}

	static int findIndexofCurrentTime(JSONArray timeList) {

		//getting current time 
		String currentTime = getCurrentTime();

		//iterating through the time list and matching time if it is equal to a time in the list we will return the index of the that space
		for(int i = 0; i <timeList.size(); i++) {
			String time = (String) timeList.get(i);
			if(time.equalsIgnoreCase(currentTime)) {
				return i;

			}

		}
		// if no time if found return the first index
		return 0;
	}

	public static String getCurrentTime() {
		//Finding out exact current time and date
		LocalDateTime currentDateTime = LocalDateTime.now();

		//formatting the date and time to match array's date and time string
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");


		//creating a string with a formatted date and time 
		String formattedDateTime = currentDateTime.format(formatter);


		return formattedDateTime;
	}

	//retrieves geographic coordinates for given location name
	static JSONArray getLocationData(String locationName) {
		// replacing whitespace in location name to + to adhere to API request

		locationName = locationName.replaceAll(" ", "+");

		//build API URL with location parameter
		String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
				+ locationName + "&count=10&language=en&format=json";

		try {
			//call API and get response
			HttpURLConnection conn = fetchApiResponse(urlString);


			//check response status 
			//200 means successful connection
			if (conn.getResponseCode()!= 200) {
				System.out.println("Error: Could not connect to API");
				return null;


			}else {
				//storing API results 
				StringBuilder resultJSON = new StringBuilder();
				Scanner input = new Scanner(conn.getInputStream());

				// reads and stores the JSON results into a string builder
				while(input.hasNext()) {
					resultJSON.append(input.nextLine());
				}
				// close scanner
				input.close();

				//close URL connection
				conn.disconnect();

				//parse the JSON string data into a JSON object
				JSONParser parser = new JSONParser();
				JSONObject resultsJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

				//get the list of location data the API generated from the location name
				JSONArray locationData = (JSONArray) resultsJSONObj.get("results");

				return locationData;

			}

		}catch(Exception e) {
			e.printStackTrace();
		}



		return null;
	}


	
	static HttpURLConnection fetchApiResponse(String urlString) {


		try {
			// attempting a connection


			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();



			//set request method to get 
			conn.setRequestMethod("GET");


			//connect to API
			conn.connect();
			return conn;

		}
		catch(IOException e) {
			e.printStackTrace();
		}

		//can't make connection
		return null;

	}

}
