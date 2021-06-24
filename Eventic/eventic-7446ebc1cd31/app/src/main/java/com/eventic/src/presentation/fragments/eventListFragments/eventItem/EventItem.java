package com.eventic.src.presentation.fragments.eventListFragments.eventItem;

import com.eventic.src.domain.Event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventItem {
    Integer id;
    String eventTitle;
    String eventImage;
    String date;
    Integer distance;
    List<String> tags;
    Integer joined;
    Integer eventCapacity;
    Integer price;

    public EventItem(Integer id, String eventTitle, String date, Integer joined, Integer eventCapacity, String eventImageURL, Integer price) {
        setId(id);
        setEventTitle(eventTitle);
        setEventImage(eventImageURL);
        setDate(date);
        setJoined(joined);
        setEventCapacity(eventCapacity);
        setPrice(price);
        tags = new ArrayList<String>();
    }

    private void setPrice(Integer price) { this.price = price;  }

    public Integer getPrice(){ return price;}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImageURL) {
        this.eventImage = eventImageURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag){
        this.tags.add(tag);
    }

    public Integer getJoined() {
        return joined;
    }

    public void setJoined(Integer joined) {
        this.joined = joined;
    }

    public Integer getEventCapacity() {
        return eventCapacity;
    }

    public static Comparator<EventItem> fecha = new Comparator<EventItem>(){

        @Override
        public int compare(EventItem o1, EventItem o2) {
            try {
                Integer day1 = Integer.parseInt(o1.getDate().substring(0, 2));
                Integer month1 = Integer.parseInt(o1.getDate().substring(3, 5));
                Integer year1 = Integer.parseInt(o1.getDate().substring(6));
                Integer day2 = Integer.parseInt(o2.getDate().substring(0, 2));
                Integer month2 = Integer.parseInt(o2.getDate().substring(3, 5));
                Integer year2 = Integer.parseInt(o2.getDate().substring(6));

                if (!year1.equals(year2)) return year1.compareTo(year2);
                if (!month1.equals(month2)) return month1.compareTo(month2);
                return day1.compareTo(day2);
            } catch (Exception e) {
                return o1.getDate().compareTo(o2.getDate());
            }

        }
    };
    public static Comparator<EventItem> precio = new Comparator<EventItem>(){

        @Override
        public int compare(EventItem o1, EventItem o2) {

            return o1.getPrice().compareTo(o2.getPrice());
        }
    };


    public void setEventCapacity(Integer eventCapacity) {
        this.eventCapacity = eventCapacity;
    }
}
