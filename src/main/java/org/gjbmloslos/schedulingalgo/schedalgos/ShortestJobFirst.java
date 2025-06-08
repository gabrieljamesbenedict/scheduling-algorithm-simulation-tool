package org.gjbmloslos.schedulingalgo.schedalgos;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.gjbmloslos.schedulingalgo.Process;
import org.gjbmloslos.schedulingalgo.SchedAlgoController;
import org.gjbmloslos.schedulingalgo.SimulationLogger;

import java.util.*;

public class ShortestJobFirst extends SchedulingAlgorithm{

    HashSet<Process> WaitingProcessSet;

    public ShortestJobFirst(SimulationLogger SimLog,
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
        WaitingProcessSet = new HashSet<>(WaitingProcessPool);
    }


    @Override
    public void addProcessToReadyQueue() {
        Iterator<Process> it = ProcessPool.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (SchedAlgoController.time >= p.getArrivalTime()*1000) {
                it.remove();
                p.setLabelRef(createLabelNode(p, Color.LIGHTBLUE));
                WaitingProcessSet.add(p);
                ReadyQueueContainer.getChildren().add(p.getLabelRef());
            }
        }
    }


    @Override
    public void addProcessToCurrentProcessing() {
        if (!WaitingProcessSet.isEmpty()) {

            // Get Process in Ready Queue with the lowest BurstTimeRemaining
            Process p = WaitingProcessSet
                    .stream()
                    .sorted(Comparator.comparingDouble(Process::getRemainingBurstTime))
                    .toList()
                    .getFirst();

            if (CurrentProcessing == null) {
                // Add to CurrentProcessing if no processes are happening
                CurrentProcessing = p;
                CurrentProcessText.setText(CurrentProcessing.getLabelRef().getText());
                WaitingProcessSet.remove(CurrentProcessing);
                ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
                GanttChartContainer.getChildren().add(createLabelNode(CurrentProcessing, "@"+((double) SchedAlgoController.time/1000)+"s", Color.LIGHTBLUE));
            } else if ( p.getRemainingBurstTime() < CurrentProcessing.getRemainingBurstTime() ) {
                // Add to CurrentProcessing if process has lower burst time than CurrentProcessing
                WaitingProcessSet.add(CurrentProcessing);
                CurrentProcessing.setLabelRef(createLabelNode(CurrentProcessing, Color.LIGHTSALMON));
                ReadyQueueContainer.getChildren().add(CurrentProcessing.getLabelRef());
                CurrentProcessing = p;
                CurrentProcessText.setText(CurrentProcessing.getLabelRef().getText());
                WaitingProcessSet.remove(CurrentProcessing);
                ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
                GanttChartContainer.getChildren().add(createLabelNode(CurrentProcessing, "@"+((double) SchedAlgoController.time/1000)+"s", Color.LIGHTBLUE));
            }
        }
    }


    @Override
    public void runCurrentProcessing() {
        if (CurrentProcessing != null) {
            CurrentProcessing.setRemainingBurstTime(
                    Double.parseDouble(df.format(CurrentProcessing.getRemainingBurstTime() - ((double) SchedAlgoController.timeSpeed/1000)))
            );
            CurrentProcessing.setTurnAroundTime(Double.parseDouble(df.format(CurrentProcessing.getTurnAroundTime() + ((double) SchedAlgoController.timeSpeed/1000))));
        }
        if (!WaitingProcessSet.isEmpty()) {
            Iterator<Process> it = WaitingProcessSet.iterator();
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
    public void ejectCompletedProcessing() {
        if (CurrentProcessing != null && CurrentProcessing.getRemainingBurstTime() <= 0) {
            CompletedProcessPool.add(CurrentProcessing);
            ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
            CurrentProcessText.setText("None");
            CurrentProcessing = null;
        }
    }


    @Override
    public boolean completedAllProcess() {
        if (ProcessPool.isEmpty() && WaitingProcessSet.isEmpty() && CurrentProcessing == null) {
            CurrentProcessText = createLabelNode("Done @"+((double) SchedAlgoController.time/1000)+"s", Color.LAWNGREEN);
            GanttChartContainer.getChildren().add(createLabelNode("Done @"+((double) SchedAlgoController.time/1000)+"s", Color.LAWNGREEN));
            return true;
        } else {
            return false;
        }
    }
}
