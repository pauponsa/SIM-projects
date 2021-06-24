package com.eventic.src.domain;

public class EventTag {
    Integer evento_id;
    Integer tag_id;


    public EventTag(Integer evento_id, Integer tag_id) {
        setEvento_id(evento_id);
        setTag_id(tag_id);
    }

    public Integer getEvento_id() {
        return evento_id;
    }

    public void setEvento_id(Integer evento_id) {
        this.evento_id = evento_id;
    }

    public Integer getTag_id() {
        return tag_id;
    }

    public void setTag_id(Integer tag_id) {
        this.tag_id = tag_id;
    }
}
