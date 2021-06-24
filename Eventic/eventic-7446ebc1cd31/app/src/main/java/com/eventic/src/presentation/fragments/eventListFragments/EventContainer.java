package com.eventic.src.presentation.fragments.eventListFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;

import java.util.List;

public abstract class EventContainer extends Fragment {
    EventListFragment eventList;
    FilterListFragment filterList;
    MyEventsFragment userEventList;

    abstract public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void addEvent(EventItem event) {
        eventList.addEvent(event);
    }

    public void setEvents(List<EventItem> events) {
        eventList.setEvents(events);
    }

    public void setUserLikedEvents(List<EventItem> events) {
        userEventList.setUserLikedEvents(events);
    }

    public void setUserJoinedEvents(List<EventItem> events) {
        userEventList.setUserJoinedEvents(events);
    }

    public void setEventsForFilters(List<EventItem> events) {
        filterList.setEventsForFilters(events);
    }

    abstract public void clickEvent(EventItem item);


    public void setFilters(List<String> p){
        filterList.setFilters(p);

    }

}
