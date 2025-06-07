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

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

public class SchedAlgoController {

    int ProcessAmount = 5;
    public static int time;
    public static final int timeSpeed = 10;
    public boolean paused;

    SchedulingAlgorithm schedulingAlgorithm;

    @FXML Spinner<Integer> TableAmountSpinner;
    @FXML ComboBox<String> ScheduleAlgorithmPicker;
    @FXML Button PauseButton;

    @FXML TableView<Process> ProcessView;
    @FXML TableColumn<Process, String> JobIDColumn;
    @FXML TableColumn<Process, String> ArrivalTimeColumn;
    @FXML TableColumn<Process, String> BurstTimeColumn;
    @FXML TableColumn<Process, String> WaitingTimeColumn;
    @FXML TableColumn<Process, String> TurnAroundTimeColumn;

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

    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    Runnable srtf = new Runnable() {
        @Override
        public void run() {

            int timeS = time/1000;
            Platform.runLater(() -> {

                if (paused) return;

                ElapsedTimeText.setText(Integer.toString(time) + "ms");

                schedulingAlgorithm.addProcessToReadyQueue();
                schedulingAlgorithm.addProcessToCurrentProcessing();
                CurrentProcessText.setText(schedulingAlgorithm.getCurrentProcessingId());
                schedulingAlgorithm.runCurrentProcessing();
                schedulingAlgorithm.ejectCompletedProcessing();

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

        TableAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));

        CurrentProcessText.setText("None");
        CurrentProcessText.setPadding(new Insets(5));
        CurrentProcessText.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(10), Insets.EMPTY)));

        JobIDColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("jobID"));
        ArrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("arrivalTime"));
        BurstTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("burstTime"));
        WaitingTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("waitingTime"));
        TurnAroundTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, String>("turnAroundTime"));

        ScheduleAlgorithmPicker.getItems().addAll(new String[]{
                "FCFS","SJF"
        });
        ScheduleAlgorithmPicker.getSelectionModel().select("FCFS");
        setSchedulingAlgorithm();

        generateTable(ProcessAmount);
    }

    @FXML
    public void start () throws InterruptedException {
        time = 0;
        service.scheduleWithFixedDelay(srtf, 0, timeSpeed, TimeUnit.MILLISECONDS);
        //service.submit(srtf);

    }

    @FXML
    public void pause () {
        if (paused) {
            PauseButton.setText("Pause");
            paused = false;
        } else {
            PauseButton.setText("Resume");
            paused = true;
        }
    }

    @FXML
    public void stop () {
        service.shutdownNow();
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
            int bt = rand.nextInt(15) + 1;
            Process process = new Process(i, at , bt);
            ProcessViewList.add(process);
        }
        setSchedulingAlgorithm();
    }

    @FXML
    public void setSchedulingAlgorithm () {
        String s = ScheduleAlgorithmPicker.getSelectionModel().getSelectedItem();
        if (s.equals("FCFS")) {
            schedulingAlgorithm = new FirstComeFirstServe(new ArrayDeque<Process>(), new ArrayDeque<Process>(), ProcessView, CurrentProcessText, ReadyQueueContainer, GanttChartContainer);
        } else if (s.equals("SJF")) {
            schedulingAlgorithm = new ShortestJobFirst(new HashSet<Process>(), new HashSet<Process>(), ProcessView, CurrentProcessText, ReadyQueueContainer, GanttChartContainer);;
        }
    }


}