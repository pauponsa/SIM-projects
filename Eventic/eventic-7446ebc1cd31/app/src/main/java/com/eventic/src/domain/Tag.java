package com.eventic.src.domain;

public class Tag implements Comparable<Tag> {
    //Class attributes
    Integer id;
    String tag_name;

    public Tag(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    @Override
    public int compareTo(Tag o) {
        return this.tag_name.compareTo(o.tag_name);
    }
}
