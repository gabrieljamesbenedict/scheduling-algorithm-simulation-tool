package org.gjbmloslos.schedulingalgo;

import javafx.scene.control.ListView;

import static org.gjbmloslos.schedulingalgo.SchedAlgoController.time;

public class SimulationLogger {

    static public ListView<String> ActivityLog;

    public SimulationLogger(ListView<String> ActivityLog) {
        SimulationLogger.ActivityLog = ActivityLog;
    }

    public void log(String s) {
        ActivityLog.getItems().add(
                s + " @" + time + "ms (" + (float)Math.round((float)time/100)/10 + "s)"
        );
    }

    public void logNoTime(String s) {
        ActivityLog.getItems().add(
                s
        );
    }
}
