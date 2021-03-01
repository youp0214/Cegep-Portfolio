package application;

import ctrl.Ctrl;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vue.Collision_Vue;

public class Application extends javafx.application.Application {

	Ctrl mainCtrl;

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainCtrl = new Ctrl();
		primaryStage.setScene(mainCtrl.getVue().getScene());
		primaryStage.sizeToScene();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {
			if (!mainCtrl.quitter())
				e.consume();
		});

	}

	public static void main(String[] args) {
		Application.launch();
	}

}
