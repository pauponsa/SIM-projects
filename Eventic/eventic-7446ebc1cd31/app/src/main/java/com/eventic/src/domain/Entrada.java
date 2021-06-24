package com.eventic.src.domain;

import java.io.File;

public class Entrada {
    //Class attributes
    private Integer user_id;
    private Integer evento_id;
    private String code;
    private boolean ha_participat;

    public Entrada(Integer user_id, Integer evento_id, String code, boolean ha_participat)
    {
        this.user_id = user_id;
        this.evento_id = evento_id;
        this.code = code;
        this.ha_participat = ha_participat;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getEvento_id() {
        return evento_id;
    }

    public void setEvento_id(Integer evento_id) {
        this.evento_id = evento_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean haParticipat() {
        return ha_participat;
    }

    public void setParticipat(boolean ha_participat) {
        this.ha_participat = ha_participat;
    }
}
