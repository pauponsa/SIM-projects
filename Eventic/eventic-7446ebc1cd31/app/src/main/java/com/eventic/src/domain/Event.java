package com.eventic.src.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class Event {

    //Class attributes
    private Integer id;
    public Integer evento_id;
    private String title;
    private String description;
    private String author;
    private ArrayList<File> event_image_data;
    private String start_date;
    private String end_date;
    private Integer capacity;
    private String token;
    private Integer participants;
    private String latitude;
    private String longitude;
    private Integer price;
    private String URL_share;
    private String URL_page;
    private String start_time;
    private String end_time;
    private Integer id_creator;
    private Map<String,String> images_url;

    public Event(){}

    public Event(Integer id, String title, String description, String author, ArrayList<File> event_image_data, String start_date, String end_date, Integer capacity, String latitude, String longitude,
                 Integer participants, Integer price , String start_time, String end_time, String token){
        setId(id);
        setTitle(title);
        setDescription(description);
        setEvent_image_data(event_image_data);
        setStart_date(start_date);
        setEnd_date(end_date);
        setCapacity(capacity);
        setParticipants(participants);
        setLatitude(latitude);
        setLongitude(longitude);
        setPrice(price);
        setStart_time(start_time);
        setEnd_time(end_time);
        setToken(token);
        setAuthor(author);
    }


    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {this.author = author;}

    public String getAuthor(){ return author;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<File> getEvent_image_data() {
        return event_image_data;
    }

    public void setEvent_image_data(ArrayList<File> event_image_data) {
        this.event_image_data = event_image_data;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getURL_share() {
        return URL_share;
    }

    public void setURL_share(String URL_share) {
        this.URL_share = URL_share;
    }

    public String getURL_page() {
        return URL_page;
    }

    public void setURL_page(String URL_page) {
        this.URL_page = URL_page;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public Integer getId_creator() {
        return id_creator;
    }

    public void setId_creator(Integer id_creator) {
        this.id_creator = id_creator;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String,String> getImages_url() {
        return images_url;
    }

    public void setImages_url( Map<String,String> images_url) {
        this.images_url = images_url;
    }



}
