package org.gjbmloslos.schedulingalgo.schedalgos;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.gjbmloslos.schedulingalgo.Updator;
import org.gjbmloslos.schedulingalgo.Process;

public interface SchedulingAlgorithm {


    public default Label createLabelNode(Process p) {
        Label label = new Label("Job" + p.getJobID());
        label.setPadding(new Insets(10));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(10), Insets.EMPTY)));
        return label;
    }

    public default Label createLabelNode(Process p, String s) {
        Label label = new Label("Job" + p.getJobID() + " " + s);
        label.setPadding(new Insets(10));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(10), Insets.EMPTY)));
        return label;
    }

    void addProcessToReadyQueue();
    void addProcessToCurrentProcessing();
    String getCurrentProcessingId ();
    void runCurrentProcessing();
    void ejectCompletedProcessing();
    TableView<Process> getUpdatedProcesses();
}
