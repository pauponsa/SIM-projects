package com.eventic.src.domain;

import java.io.File;

public class Chat {


    //Class attributes
    private Integer creatorID;
    private Integer eventID;
    private String eventTitle;
    private File eventImage;

    //Relations with other classes
    private Event event;
    private Message[] message;
    private Company company;
    private User user;

    public Chat(){}

    public Chat(Integer creatorID, Integer eventID){
        this.creatorID = creatorID;
        this.eventID = eventID;
    }

    public Chat(Integer creatorID, Integer eventID, String eventTitle){
        this.creatorID = creatorID;
        this.eventID = eventID;
        this.eventTitle = eventTitle;
    }

    public Integer getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public File getEventImage() {
        return eventImage;
    }

    public void setEventImage(File eventImage) {
        this.eventImage = eventImage;
    }

}
