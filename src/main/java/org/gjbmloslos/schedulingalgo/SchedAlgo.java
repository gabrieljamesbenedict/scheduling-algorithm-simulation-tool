package org.gjbmloslos.schedulingalgo;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class SchedAlgo {

    static private Stage schedAlgoStage;

    public void start(Stage stage) throws IOException {
        SchedAlgo.schedAlgoStage = stage;
        schedAlgoStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(SchedAlgo.class.getResource("schedalgo-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getSchedAlgoStage() {
        return schedAlgoStage;
    }

}