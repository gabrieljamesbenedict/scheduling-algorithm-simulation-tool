package org.gjbmloslos.schedulingalgo;

import javafx.scene.control.ListView;

public class SimulationLogger {

    static public ListView<String> ActivityLog;

    public SimulationLogger(ListView<String> ActivityLog) {
        SimulationLogger.ActivityLog = ActivityLog;
    }

    public void log(String s) {
        ActivityLog.getItems().add(
                s + " @" + SchedAlgoController.time + "ms (" + (int)SchedAlgoController.time/1000 + "s)"
        );
    }
}
