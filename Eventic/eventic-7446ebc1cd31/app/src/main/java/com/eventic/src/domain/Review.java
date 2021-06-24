package com.eventic.src.domain;

public class Review {

    //Class attributes
    private Integer mark;
    private String text;

    //Relations with other classes
    private User reviewer;
    private Company company;

    public Review(){}

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
