package csc2b.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		ZEDEMClientPane client = new ZEDEMClientPane();
		Scene scene = new Scene(client.getVbox(), 300,400);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
