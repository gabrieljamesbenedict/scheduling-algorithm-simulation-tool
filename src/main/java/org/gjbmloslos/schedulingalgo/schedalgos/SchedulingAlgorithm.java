package org.gjbmloslos.schedulingalgo.schedalgos;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.gjbmloslos.schedulingalgo.Process;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;

abstract public class SchedulingAlgorithm {

    DecimalFormat df = new DecimalFormat(".###");

    Process CurrentProcessing;
    HashSet<Process> ProcessPool;
    Collection<Process> WaitingProcessPool;
    Collection<Process> CompletedProcessPool;
    TableView<Process> MasterProcessView;
    Label CurrentProcessText;
    HBox ReadyQueueContainer;
    HBox GanttChartContainer;

    public SchedulingAlgorithm(
            Collection<Process> WaitingProcessPool,
            Collection<Process> CompletedProcessPool,
            TableView<Process> MasterProcessView,
            Label CurrentProcessText,
            HBox ReadyQueueContainer,
            HBox GanttChartContainer) {
        this.WaitingProcessPool = WaitingProcessPool;
        this.CompletedProcessPool = CompletedProcessPool;
        this.MasterProcessView = MasterProcessView;
        this.CurrentProcessText = CurrentProcessText;
        this.ReadyQueueContainer = ReadyQueueContainer;
        this.GanttChartContainer = GanttChartContainer;

        CurrentProcessing = null;
        ProcessPool = new HashSet<>();

        ProcessPool.addAll(MasterProcessView.getItems());
    }

    public Label createLabelNode(Process p) {
        Label label = new Label("Process" + p.getProcessID());
        label.setPadding(new Insets(10));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(10), Insets.EMPTY)));
        return label;
    }

    public Label createLabelNode(Process p, String s) {
        Label label = new Label("Process" + p.getProcessID() + " " + s);
        label.setPadding(new Insets(10));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(10), Insets.EMPTY)));
        return label;
    }

    public Label createLabelNode(String s) {
        Label label = new Label(s);
        label.setPadding(new Insets(10));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(10), Insets.EMPTY)));
        return label;
    }

    public String getCurrentProcessingId () {
        return (CurrentProcessing == null)? "None" : "Process"+CurrentProcessing.getProcessID() + " BurstTimeRemaining: " + Double.parseDouble(df.format(CurrentProcessing.getRemainingBurstTime()));
    }

    public abstract void addProcessToReadyQueue();
    public abstract void  addProcessToCurrentProcessing();
    public abstract void runCurrentProcessing();
    public abstract void ejectCompletedProcessing();
    public abstract boolean completedAllProcess ();
}
