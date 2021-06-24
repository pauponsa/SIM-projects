package com.eventic.src.domain;

public class Ticket {
    //Class attributes
    private String QRcode;
    //Relations with other classes
    private User u;
    private Event e;

    public String getQRcode(){
        return QRcode;
    }

    public void setQRcode(String QRcode) {
        this.QRcode = QRcode;
    }
}
