package org.gjbmloslos.schedulingalgo.schedalgos;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.gjbmloslos.schedulingalgo.Process;
import org.gjbmloslos.schedulingalgo.SchedAlgoController;

import java.text.DecimalFormat;
import java.util.*;

public class FirstComeFirstServe implements SchedulingAlgorithm {

    Process CurrentProcessing;
    HashSet<Process> ProcessPool;
    Queue<Process> WaitingProcessQueue;
    Queue<Process> CompletedProcessQueue;
    TableView<Process> MasterProcessView;
    Label CurrentProcessText;
    HBox ReadyQueueContainer;
    HBox GanttChartContainer;

    DecimalFormat df = new DecimalFormat(".###");


    public FirstComeFirstServe(
            Process CurrentProcessing,
            HashSet<Process> ProcessPool,
            Queue<Process> WaitingProcessQueue,
            Queue<Process> CompletedProcessQueue,
            TableView<Process> MasterProcessView,
            Label CurrentProcessText,
            HBox ReadyQueueContainer,
            HBox GanttChartContainer) {
        this.CurrentProcessing = CurrentProcessing;
        this.ProcessPool = ProcessPool;
        this.WaitingProcessQueue = WaitingProcessQueue;
        this.CompletedProcessQueue = CompletedProcessQueue;
        this.MasterProcessView = MasterProcessView;
        this.CurrentProcessText = CurrentProcessText;
        this.ReadyQueueContainer = ReadyQueueContainer;
        this.GanttChartContainer = GanttChartContainer;
    }

    @Override
    public void addProcessToReadyQueue () {
        Iterator<Process> it = ProcessPool.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (SchedAlgoController.time >= p.getArrivalTime()*1000) {
                it.remove();
                p.setLabelRef(createLabelNode(p));
                WaitingProcessQueue.add(p);
                ReadyQueueContainer.getChildren().add(p.getLabelRef());
            }
        }
    }

    public String getCurrentProcessingId () {
        return (CurrentProcessing == null)? "None" : "Job"+CurrentProcessing.getJobID() + " BurstTimeRemaining: " + Double.parseDouble(df.format(CurrentProcessing.getRemainingBurstTime()));
    }

    @Override
    public void addProcessToCurrentProcessing () {
        if (!WaitingProcessQueue.isEmpty() && CurrentProcessing == null) {
            CurrentProcessing = WaitingProcessQueue.remove();
            CurrentProcessText = CurrentProcessing.getLabelRef();
            WaitingProcessQueue.remove(CurrentProcessing);
            ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
            System.out.println(WaitingProcessQueue.stream().map(Process::getJobID).toList().toString());
        }
    }

    @Override
    public void runCurrentProcessing () {
        if (CurrentProcessing != null) {
            CurrentProcessing.setRemainingBurstTime(
                    Double.parseDouble(df.format(CurrentProcessing.getRemainingBurstTime() - ((double) SchedAlgoController.timeSpeed/1000)))
            );
            CurrentProcessing.setTurnAroundTime(Double.parseDouble(df.format(CurrentProcessing.getTurnAroundTime() + ((double) SchedAlgoController.timeSpeed/1000))));
        }
        if (!WaitingProcessQueue.isEmpty()) {
            Iterator<Process> it = WaitingProcessQueue.iterator();
            while (it.hasNext()) {
                Process p = it.next();
                p.setWaitingTime(Double.parseDouble(df.format(p.getWaitingTime() + ((double) SchedAlgoController.timeSpeed/1000))));
                p.setTurnAroundTime(Double.parseDouble(df.format(p.getTurnAroundTime() + ((double) SchedAlgoController.timeSpeed/1000))));
            }
        }
        Iterator<Process> updater = MasterProcessView.getItems().iterator();
        while (updater.hasNext()) {
            Process p = updater.next();
            MasterProcessView.getItems().set(MasterProcessView.getItems().indexOf(p), p);
        }
    }

    @Override
    public void ejectCompletedProcessing () {
        if (CurrentProcessing != null && CurrentProcessing.getRemainingBurstTime() <= 0) {
            ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
            GanttChartContainer.getChildren().add(createLabelNode(CurrentProcessing, "@"+((double) SchedAlgoController.time/1000)+"s"));
            CurrentProcessText.setText("None");
            CurrentProcessing = null;
        }
    }

    @Override
    public TableView<Process> getUpdatedProcesses () {
        return MasterProcessView;
    }

}
