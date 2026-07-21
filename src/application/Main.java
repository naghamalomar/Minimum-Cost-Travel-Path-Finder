package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        HelloController controller = new HelloController();
        primaryStage.setTitle("Minimum Cost Travel Path Finder");
        primaryStage.setScene(controller.createScene(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}