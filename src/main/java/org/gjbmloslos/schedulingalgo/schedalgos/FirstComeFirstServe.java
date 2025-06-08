package org.gjbmloslos.schedulingalgo.schedalgos;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.gjbmloslos.schedulingalgo.Process;
import org.gjbmloslos.schedulingalgo.SchedAlgoController;
import org.gjbmloslos.schedulingalgo.SimulationLogger;

import java.text.DecimalFormat;
import java.util.*;

public class FirstComeFirstServe extends SchedulingAlgorithm {

    ArrayDeque<Process> WaitingProcessQueue;

    public FirstComeFirstServe(SimulationLogger SimLog,
                               Collection<Process> WaitingProcessPool,
                               Collection<Process> CompletedProcessPool,
                               TableView<Process> MasterProcessView,
                               Label CurrentProcessText,
                               HBox ReadyQueueContainer,
                               HBox GanttChartContainer) {
        super(SimLog,
                WaitingProcessPool,
                CompletedProcessPool,
                MasterProcessView,
                CurrentProcessText,
                ReadyQueueContainer,
                GanttChartContainer);
        WaitingProcessQueue = new ArrayDeque<>(WaitingProcessPool);
    }


    @Override
    public void addProcessToReadyQueue () {
        Iterator<Process> it = ProcessPool.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (SchedAlgoController.time >= p.getArrivalTime()*1000) {
                it.remove();
                p.setLabelRef(createLabelNode(p, Color.LIGHTBLUE));
                WaitingProcessQueue.add(p);
                ReadyQueueContainer.getChildren().add(p.getLabelRef());
                SimLog.log("Added "+ p.toString() +" to ReadyQueue");
            }
        }
    }


    @Override
    public void addProcessToCurrentProcessing () {
        if (!WaitingProcessQueue.isEmpty() && CurrentProcessing == null) {
            CurrentProcessing = WaitingProcessQueue.remove();
            CurrentProcessText.setText(CurrentProcessing.getLabelRef().getText());
            WaitingProcessQueue.remove(CurrentProcessing);
            ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
            GanttChartContainer.getChildren().add(createLabelNode(CurrentProcessing, "@"+((double) SchedAlgoController.time/1000)+"s", Color.LIGHTBLUE));
            SimLog.log("Started " + CurrentProcessing.toString());
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
            CompletedProcessPool.add(CurrentProcessing);
            ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
            CurrentProcessText.setText("None");
            SimLog.log("Completed "+ CurrentProcessing.toString());
            CurrentProcessing = null;
        }
    }


    @Override
    public boolean completedAllProcess() {
        if (ProcessPool.isEmpty() && WaitingProcessQueue.isEmpty() && CurrentProcessing == null) {
            CurrentProcessText = createLabelNode("Done @"+((double) SchedAlgoController.time/1000)+"s", Color.LAWNGREEN);
            GanttChartContainer.getChildren().add(createLabelNode("Done @"+((double) SchedAlgoController.time/1000)+"s", Color.LAWNGREEN));
            SimLog.log("All Processes Completed");
            SimLog.log("Simulation Ended");
            return true;
        } else {
            return false;
        }
    }

}
