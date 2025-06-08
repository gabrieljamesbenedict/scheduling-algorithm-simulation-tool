package org.gjbmloslos.schedulingalgo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.gjbmloslos.schedulingalgo.schedalgos.FirstComeFirstServe;
import org.gjbmloslos.schedulingalgo.schedalgos.SchedulingAlgorithm;
import org.gjbmloslos.schedulingalgo.schedalgos.ShortestJobFirst;
import org.gjbmloslos.schedulingalgo.schedalgos.ShortestRemainingTimeFirst;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

public class SchedAlgoController {

    int ProcessAmount = 5;
    public static Integer time;
    public static final int timeSpeed = 10;
    public boolean paused;

    SchedulingAlgorithm schedulingAlgorithm;
    SimulationLogger SimLog;

    @FXML Spinner<Integer> TableAmountSpinner;
    @FXML Button GenerateTableButton;
    @FXML ComboBox<String> ScheduleAlgorithmPicker;
    @FXML Button StartButton;
    @FXML Button PauseButton;
    @FXML Button EndButton;

    @FXML TableView<Process> ProcessView;
    @FXML TableColumn<Process, String> ProcessIDColumn;
    @FXML TableColumn<Process, String> ArrivalTimeColumn;
    @FXML TableColumn<Process, String> BurstTimeColumn;
    @FXML TableColumn<Process, String> WaitingTimeColumn;
    @FXML TableColumn<Process, String> TurnAroundTimeColumn;

    @FXML ListView<String> ActivityLog;

    @FXML Label AveWaitingTime;
    @FXML Label AveTurnAroundTime;
    @FXML Label MinWaitingTime;
    @FXML Label MinTurnAroundTime;
    @FXML Label MaxWaitingTime;
    @FXML Label MaxTurnAroundTime;
    @FXML Label TotalWaitingTime;
    @FXML Label TotalTurnAroundTime;

    @FXML Label ElapsedTimeText;
    @FXML public Label CurrentProcessText;
    @FXML public HBox ReadyQueueContainer;
    @FXML public HBox GanttChartContainer;

    ObservableList<Process> ProcessViewList;

    public ScheduledExecutorService service;
    Runnable srtf = new Runnable() {
        @Override
        public void run() {

            Platform.runLater(() -> {

                if (paused) return;

                int timeS = time/1000;
                ElapsedTimeText.setText(Integer.toString(time) + "ms (" + timeS + "s)");

                schedulingAlgorithm.addProcessToReadyQueue();
                schedulingAlgorithm.addProcessToCurrentProcessing();
                CurrentProcessText.setText(schedulingAlgorithm.getCurrentProcessingId());
                schedulingAlgorithm.runCurrentProcessing();
                schedulingAlgorithm.ejectCompletedProcessing();
                if (schedulingAlgorithm.completedAllProcess()) {
                    service.shutdown();
                }

                double totalWT = ProcessViewList.stream().map(Process::getWaitingTime).reduce((a, b) -> (a + b)).get();
                double totalTAT = ProcessViewList.stream().map(Process::getTurnAroundTime).reduce((a, b) -> (a + b)).get();
                double aveWT = totalWT / ProcessAmount;
                double aveTAT = totalTAT / ProcessAmount;
                double minWT = ProcessViewList.stream().map(Process::getWaitingTime).min(Double::compare).get();
                double minTAT = ProcessViewList.stream().map(Process::getTurnAroundTime).min(Double::compare).get();
                double maxWT = ProcessViewList.stream().map(Process::getWaitingTime).max(Double::compare).get();
                double maxTAT = ProcessViewList.stream().map(Process::getTurnAroundTime).max(Double::compare).get();

                DecimalFormat df = new DecimalFormat(".###");
                AveWaitingTime.setText(Double.toString(Double.parseDouble(df.format(aveWT))));
                AveTurnAroundTime.setText(Double.toString(Double.parseDouble(df.format(aveTAT))));
                MinWaitingTime.setText(Double.toString(Double.parseDouble(df.format(minWT))));
                MinTurnAroundTime.setText(Double.toString(Double.parseDouble(df.format(minTAT))));
                MaxWaitingTime.setText(Double.toString(Double.parseDouble(df.format(maxWT))));
                MaxTurnAroundTime.setText(Double.toString(Double.parseDouble(df.format(maxTAT))));
                TotalWaitingTime.setText(Double.toString(Double.parseDouble(df.format(totalWT))));
                TotalTurnAroundTime.setText(Double.toString(Double.parseDouble(df.format(totalTAT))));

                time += timeSpeed;

            });
        }
    };

    @FXML
    public void initialize () {
        SchedAlgo.getSchedAlgoStage().setOnCloseRequest(e -> {if (service != null)service.shutdownNow();});

        time = 0;

        TableAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        TableAmountSpinner.getValueFactory().setValue(ProcessAmount);

        SimLog = new SimulationLogger(ActivityLog);

        CurrentProcessText.setText("None");
        CurrentProcessText.setPadding(new Insets(5));
        CurrentProcessText.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(10), Insets.EMPTY)));

        ProcessIDColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("processID"));
        ArrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("arrivalTime"));
        BurstTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("burstTime"));
        WaitingTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("waitingTime"));
        TurnAroundTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("turnAroundTime"));

        ScheduleAlgorithmPicker.getItems().addAll(new String[]{
                "FCFS","SJF","SRTF"
        });
        ScheduleAlgorithmPicker.getSelectionModel().select("FCFS");
        setSchedulingAlgorithm();

        generateTable(ProcessAmount);
    }

    @FXML
    public void start () throws InterruptedException {
        PauseButton.setDisable(false);
        EndButton.setDisable(false);
        StartButton.setDisable(true);
        TableAmountSpinner.setDisable(true);
        GenerateTableButton.setDisable(true);
        ScheduleAlgorithmPicker.setDisable(true);
        SimLog.log("Started Simulation with " + ProcessView.getItems().size() + " Processes using " + ScheduleAlgorithmPicker.getSelectionModel().getSelectedItem() + " Algorithm");
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(srtf, 0, timeSpeed, TimeUnit.MILLISECONDS);
    }

    @FXML
    public void pause () {
        if (paused) {
            PauseButton.setText("Pause");
            paused = false;
            if (service != null) SimLog.log("Paused Simulation");
        } else {
            PauseButton.setText("Resume");
            paused = true;
            if (service != null) SimLog.log("Resume Simulation");
        }
    }

    @FXML
    public void stop () {
        if (service != null) SimLog.log("Ended Simulation Early");
        if (service != null) service.shutdownNow();
        PauseButton.setDisable(true);
        EndButton.setDisable(true);
        service = null;
    }

    @FXML
    public void generateTable () {
        ProcessAmount = TableAmountSpinner.getValue();
        generateTable(ProcessAmount);
    }

    private void generateTable (int am) {
        ProcessView.getItems().clear();
        ProcessViewList = FXCollections.observableArrayList();
        ProcessView.setItems(ProcessViewList);
        for (int i = 0; i < am; i++) {
            Random rand = new Random();
            //rand.nextInt(15)
            int at = rand.nextInt(am) + 1;
            int bt = rand.nextInt(20) + 1;
            Process process = new Process(i, at , bt);
            ProcessViewList.add(process);
        }
        setSchedulingAlgorithm();
    }

    @FXML
    public void setSchedulingAlgorithm () {
        String s = ScheduleAlgorithmPicker.getSelectionModel().getSelectedItem();
        if (s.equals("FCFS")) {
            schedulingAlgorithm = new FirstComeFirstServe(SimLog, new ArrayDeque<Process>(), new ArrayDeque<Process>(), ProcessView, CurrentProcessText, ReadyQueueContainer, GanttChartContainer);
        } else if (s.equals("SJF")) {
            schedulingAlgorithm = new ShortestJobFirst(SimLog, new HashSet<Process>(), new HashSet<Process>(), ProcessView, CurrentProcessText, ReadyQueueContainer, GanttChartContainer);;
        } else if (s.equals("SRTF")) {
            schedulingAlgorithm = new ShortestRemainingTimeFirst(SimLog, new HashSet<Process>(), new HashSet<Process>(), ProcessView, CurrentProcessText, ReadyQueueContainer, GanttChartContainer);;
        }
    }


}