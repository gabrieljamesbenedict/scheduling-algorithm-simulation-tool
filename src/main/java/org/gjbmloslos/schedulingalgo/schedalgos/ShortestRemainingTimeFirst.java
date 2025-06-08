package org.gjbmloslos.schedulingalgo.schedalgos;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.gjbmloslos.schedulingalgo.Process;
import org.gjbmloslos.schedulingalgo.SchedAlgoController;
import org.gjbmloslos.schedulingalgo.SimulationLogger;

import java.util.Collection;
import java.util.Comparator;

public class ShortestRemainingTimeFirst extends ShortestJobFirst{
    public ShortestRemainingTimeFirst(SimulationLogger SimLog, Collection<Process> WaitingProcessPool, Collection<Process> CompletedProcessPool, TableView<Process> MasterProcessView, Label CurrentProcessText, HBox ReadyQueueContainer, HBox GanttChartContainer) {
        super(SimLog, WaitingProcessPool, CompletedProcessPool, MasterProcessView, CurrentProcessText, ReadyQueueContainer, GanttChartContainer);
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
            CurrentProcessing = p;
            CurrentProcessText.setText(CurrentProcessing.getLabelRef().getText());
            WaitingProcessSet.remove(CurrentProcessing);
            ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
            GanttChartContainer.getChildren().add(createLabelNode(CurrentProcessing, "@"+((double) SchedAlgoController.time/1000)+"s", Color.LIGHTBLUE));
            SimLog.log("Started " + CurrentProcessing.toString());

            if (CurrentProcessing == null) {
                // Add to CurrentProcessing if no processes are happening
                CurrentProcessing = p;
                CurrentProcessText.setText(CurrentProcessing.getLabelRef().getText());
                WaitingProcessSet.remove(CurrentProcessing);
                ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
                GanttChartContainer.getChildren().add(createLabelNode(CurrentProcessing, "@"+((double) SchedAlgoController.time/1000)+"s", Color.LIGHTBLUE));
                SimLog.log("Started " + CurrentProcessing.toString());
            } else if ( p.getRemainingBurstTime() < CurrentProcessing.getRemainingBurstTime() ) {
                // Add to CurrentProcessing if process has lower burst time than CurrentProcessing
                WaitingProcessSet.add(CurrentProcessing);
                CurrentProcessing.setLabelRef(createLabelNode(CurrentProcessing, Color.LIGHTSALMON));
                ReadyQueueContainer.getChildren().add(CurrentProcessing.getLabelRef());
                SimLog.log("Returned " + CurrentProcessing.toString() +" to ReadyQueue");
                CurrentProcessing = p;
                CurrentProcessText.setText(CurrentProcessing.getLabelRef().getText());
                WaitingProcessSet.remove(CurrentProcessing);
                ReadyQueueContainer.getChildren().remove(CurrentProcessing.getLabelRef());
                GanttChartContainer.getChildren().add(createLabelNode(CurrentProcessing, "@"+((double) SchedAlgoController.time/1000)+"s", Color.LIGHTBLUE));
                SimLog.log("Started " + CurrentProcessing.toString());
            }
        }

    }
}
