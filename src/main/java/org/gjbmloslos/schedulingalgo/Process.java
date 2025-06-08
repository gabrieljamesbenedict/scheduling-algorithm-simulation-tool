package org.gjbmloslos.schedulingalgo;

import javafx.scene.control.Label;

public class Process {

    private int processID;
    private double arrivalTime;
    private double burstTime;
    private double waitingTime;
    private double turnAroundTime;
    private double remainingBurstTime;

    Label labelRef;

    public Process(int processID, double arrivalTime, double burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;

        remainingBurstTime = burstTime;
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(double burstTime) {
        this.burstTime = burstTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(double turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public Label getLabelRef() {
        return labelRef;
    }

    public void setLabelRef(Label labelRef) {
        this.labelRef = labelRef;
    }

    public double getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public void setRemainingBurstTime(double remainingBurstTime) {
        this.remainingBurstTime = remainingBurstTime;
    }

    @Override
    public String toString() {
        return "Process" + processID;
    }
}
