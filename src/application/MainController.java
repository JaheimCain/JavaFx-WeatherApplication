package application;

import java.io.IOException;


import org.json.simple.JSONObject;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;



public class MainController {
	
	@FXML
	private TextField input;
	@FXML
	private Label conditionLabel;
	@FXML
	private Label tempLabel;
	@FXML
	private Label windLabel;
	@FXML
	private Label rainLabel;
	@FXML
	private Label humidityLabel;
	
	
	
	
	
	
	public void DisplayWeatherData(ActionEvent e) throws IOException {
		String data = input.getText();
		
		
		
		JSONObject weatherData = WeatherAPI.weatherAPI(data);
		
		
		
		double temperature = (double) weatherData.get("tempature");
		String condition = (String) weatherData.get("weather_condition");
		double wind = (double) weatherData.get("wind_speed");
		long precipitation = (long) weatherData.get("precipitation");
		long humidity = (long) weatherData.get("humidity");
		
		
		
		
		conditionLabel.setText("Weather Conditions: " + condition );
		tempLabel.setText("Temperature: " + temperature + " °F" );
		windLabel.setText("Wind Speed: " + wind + " Mph" );
		humidityLabel.setText("Humidity: " + humidity +"%");
		rainLabel.setText("Precipitation Chance: " + precipitation+"%");
		
		
		
		
	}




	




	
}

	
	
	
	
	

	