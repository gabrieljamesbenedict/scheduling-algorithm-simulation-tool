package org.gjbmloslos.schedulingalgo.schedalgos;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.gjbmloslos.schedulingalgo.Process;
import org.gjbmloslos.schedulingalgo.SchedAlgoController;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.stream.Collectors;

public class ShortestJobFirst implements SchedulingAlgorithm{

    Process CurrentProcessing;
    HashSet<Process> ProcessPool;
    HashSet<Process> WaitingProcessSet;
    HashSet<Process> CompletedProcessSet;
    TableView<Process> MasterProcessView;
    Label CurrentProcessText;
    HBox ReadyQueueContainer;
    HBox GanttChartContainer;

    DecimalFormat df = new DecimalFormat(".###");

    public ShortestJobFirst(
            HashSet<Process> WaitingProcessSet,
            HashSet<Process> CompletedProcessSet,
            TableView<Process> masterProcessView,
            Label currentProcessText,
            HBox readyQueueContainer,
            HBox ganttChartContainer) {
        this.WaitingProcessSet = WaitingProcessSet;
        this.CompletedProcessSet = CompletedProcessSet;
        this.MasterProcessView = masterProcessView;
        this.CurrentProcessText = currentProcessText;
        this.ReadyQueueContainer = readyQueueContainer;
        this.GanttChartContainer = ganttChartContainer;

        CurrentProcessing = null;
        ProcessPool = new HashSet<>();
    }

    @Override
    public Label createLabelNode(Process p) {
        return SchedulingAlgorithm.super.createLabelNode(p);
    }

    @Override
    public Label createLabelNode(Process p, String s) {
        return SchedulingAlgorithm.super.createLabelNode(p, s);
    }

    @Override
    public void addProcessToReadyQueue() {
        Iterator<Process> it = ProcessPool.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (SchedAlgoController.time >= p.getArrivalTime()*1000) {
                it.remove();
                p.setLabelRef(createLabelNode(p));
                WaitingProcessSet.add(p);
                ReadyQueueContainer.getChildren().add(p.getLabelRef());
            }
        }
    }

    @Override
    public void addProcessToCurrentProcessing() {
        if (!WaitingProcessSet.isEmpty() && CurrentProcessing == null) {

            Process p = WaitingProcessSet
                    .stream()
                    .sorted((a,b) -> (int)(a.getBurstTime() - b.getBurstTime()))
                    .toList()
                    .getFirst();
            System.out.println("Job"+p.getJobID() + " BT:" + p.getBurstTime());

            WaitingProcessSet.remove(p);
        }

    }

    @Override
    public String getCurrentProcessingId() {
        return "";
    }

    @Override
    public void runCurrentProcessing() {
        String s = WaitingProcessSet.stream().map(Process::getBurstTime).sorted(Comparator.naturalOrder()).toList().toString();
        System.out.println(s);
    }

    @Override
    public void ejectCompletedProcessing() {

    }

    @Override
    public TableView<Process> getUpdatedProcesses() {
        return null;
    }
}
