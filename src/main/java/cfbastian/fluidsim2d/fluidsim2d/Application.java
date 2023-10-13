package cfbastian.fluidsim2d.fluidsim2d;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    public static int width = 1280, height = 720;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.setFill(new Color(0.1, 0.1, 0.1, 1));
        scene.getRoot().requestFocus();

        stage.setTitle("Fluid Sim 2D");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}