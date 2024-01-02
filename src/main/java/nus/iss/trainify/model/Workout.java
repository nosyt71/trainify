package nus.iss.trainify.model;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Workout implements Serializable {

    private String username;
    private String description;
    private boolean completed;

    @NotNull(message = "Push up count is required")
    @Min(value = 0, message = "Push up count must be at least 0")
    @Max(value = 60, message = "Push up count must be at most 60")
    private int pushUpCount;

    @NotNull(message = "Sit up count is required")
    @Min(value = 0, message = "Sit up count must be at least 0")
    @Max(value = 60, message = "Sit up count must be at most 60")
    private int sitUpCount;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be at least 0")
    private int age;

    @NotNull(message = "Run time is required")
    @Min(value = 0, message = "Run time must be at least 0")
    private int runTime;

    private int ipptScore;


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

    public int getIpptScore() {
        return ipptScore;
    }

    public int getRunTime() {
        return runTime;
    }

    public int getPushUpCount() {
        return pushUpCount;
    }

    public int getSitUpCount() {
        return sitUpCount;
    }

    public void setIpptScore(int ipptScore) {
        this.ipptScore = ipptScore;
    }

    public void setPushUpCount(int pushUpCount) {
        this.pushUpCount = pushUpCount;
    }

    public void setRunTime(int runTime) {
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
