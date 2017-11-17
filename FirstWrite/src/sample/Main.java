package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    @FXML
    TextField mac_1,mac_2,mac_3,tagmask1,tagmask2,tagmask3;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("写入EPC");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
