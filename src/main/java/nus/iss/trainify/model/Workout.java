package nus.iss.trainify.model;

import java.io.Serializable;

public class Workout implements Serializable {

    private String username;
    private String description;
    private boolean completed;

    private int pushUpCount;

    private int sitUpCount;

    private int age;

    private double runTime;

    private double ipptScore;


    public Workout() {
    }

    public Workout(String username, String description, boolean completed) {
        this.username = username;
        this.description = description;
        this.completed = completed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public double getIpptScore() {
        return ipptScore;
    }

    public double getRunTime() {
        return runTime;
    }

    public int getPushUpCount() {
        return pushUpCount;
    }

    public int getSitUpCount() {
        return sitUpCount;
    }

    public void setIpptScore(double ipptScore) {
        this.ipptScore = ipptScore;
    }

    public void setPushUpCount(int pushUpCount) {
        this.pushUpCount = pushUpCount;
    }

    public void setRunTime(double runTime) {
        this.runTime = runTime;
    }

    public void setSitUpCount(int sitUpCount) {
        this.sitUpCount = sitUpCount;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String toString() {
        return "Workout{" +
                "description='" + description + '\'' +
                ", completed=" + completed +
                ", pushUpCount=" + pushUpCount +
                ", sitUpCount=" + sitUpCount +
                ", runTime=" + runTime +
                ", ipptScore=" + ipptScore +
                '}';
    }

    
}
