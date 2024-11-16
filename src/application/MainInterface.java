package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainInterface extends Application {

	public static void main(String[] args) {
		//launches the stage, and scene
		launch();
	}

	@Override
	public void start(Stage primary) throws Exception {
	
		//setting the stage and scene with resizable set to false so it can keep the size
		try {
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
		primary.setTitle("Weather App");
		
		Image icon = new Image("application/clear.png");
		
		primary.getIcons().add(icon);
		
		primary.setResizable(false);
		primary.setScene(scene);
		primary.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	
}
	}
