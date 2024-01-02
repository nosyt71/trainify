package nus.iss.trainify.model;

import nus.iss.trainify.validation.Duration;
import nus.iss.trainify.validation.Focus;
import nus.iss.trainify.validation.Location;

import jakarta.validation.constraints.NotBlank;

public class Video {
    private String url;

    @NotBlank(message = "Please select a duration.")
    @Duration(message = "Please select your workout duration.")
    private String duration;

    @NotBlank(message = "Please select a focus.")
    @Focus(message = "Please select a focus.")
    private String focus;

    @NotBlank(message = "Please select a location.")
    @Location(message= "Please select a your workout location.")
    private String location;

    private String VideoId;

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public Video(String url, String duration, String focus, String location) {
        this.url = url;
        this.duration = duration;
        this.focus = focus;
        this.location = location;
    }
    

    public Video(@NotBlank(message = "Please select a duration.") String duration,
            @NotBlank(message = "Please select a focus.") String focus,
            @NotBlank(message = "Please select a location.") String location) {
        this.duration = duration;
        this.focus = focus;
        this.location = location;
    }
    
    
    public Video() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
