package org.gjbmloslos.schedulingalgo;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        new SchedAlgo().start(stage);
    }
}
