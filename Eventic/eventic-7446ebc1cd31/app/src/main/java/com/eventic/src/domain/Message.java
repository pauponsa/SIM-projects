package com.eventic.src.domain;

import java.util.Date;

public class Message {

    public enum Type {
        sent,
        received,
        system
    };

    //Class attributes
    private Date date;
    private String text;
    private Type type;
    private Integer senderID;
    private Long dateLong;

    //Relations with other classes
    private Chat c;

    public Message(){}

    public Message(String text, Date date, Type type){
        this.text = text;
        this.type = type;
        this.date = date;
    }

    public Message(String text, Date date, Type type, Integer senderID){
        this.text = text;
        this.date = date;
        this.type = type;
        this.senderID = senderID;
    }

    public Message(String text, Type type, Integer senderID, Long dateLong){
        this.text = text;
        this.type = type;
        this.senderID = senderID;
        this.dateLong = dateLong;
        this.setDateLong(dateLong);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDateLong(Long dateLong)
    {
        this.dateLong = dateLong;
        this.setDate(new Date(dateLong));
    }

    public long getDateLong() { return dateLong; }

    public Integer getSenderID() {
        return senderID;
    }

    public void setSenderID(Integer senderID) {
        this.senderID = senderID;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
