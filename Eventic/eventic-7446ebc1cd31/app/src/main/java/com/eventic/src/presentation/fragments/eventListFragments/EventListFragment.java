package com.eventic.src.presentation.fragments.eventListFragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventListFragment extends Fragment {
    RecyclerView recyclerView;
    List<EventItem> mlist;
    List<EventItem> mlistfull;
    EventContainer mContainer;
    View view;
    EventListAdapter adapter;
    ArrayList<String> tagsFiltered;
    String busqueda;

    public EventListFragment() {
        // Required empty public constructor
    }

    public void setContainer(EventContainer mContainer) {
        this.mContainer = mContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_event_list, container, false);

        recyclerView = view.findViewById(R.id.myEventsEventList);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setup();
    }

    public void setup(){
        // setup recycler view with adapter
        busqueda = "";
        mlist = new ArrayList<>();
        mlistfull = new ArrayList<>();
        adapter = new EventListAdapter(getContext(), mlist, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tagsFiltered = new ArrayList<>();

    }
    public EventListAdapter getAdapter(){
        return adapter;
    }

    public void addEvent(EventItem event) {
        mlist.add((event));
        updateList();
    }

    public void setEvents(List<EventItem> events) {
        mlist = events;
        mlistfull = new ArrayList<>();
        mlistfull.addAll(mlist);
        updateList();
    }

    public void addFilter(String item) {
        tagsFiltered.add(item);
        updateFilters();
    }
    public void removeFilter(String item) {
        tagsFiltered.remove(item);
        updateFilters();
    }

    public void sortBy(String item) {
        if(item.equals("recently")) {
            Collections.sort(mlistfull, EventItem.fecha);
        }
        else if(item.equals("price")){
            Collections.sort(mlistfull,EventItem.precio);
        }

        updateFilters();
    }

    private void updateFilters(){
        if(busqueda.length() == 0){
            mlist.clear();
            mlist.addAll( mlistfull);

        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mlist.clear();
                List<EventItem> collect = mlistfull.stream()
                        .filter(i -> i.getEventTitle().toLowerCase().contains(busqueda))
                        .collect(Collectors.toList());

                mlist.addAll(collect);
            }
            else{
                mlist.clear();
                for(EventItem i : mlistfull){
                    if(i.getEventTitle().toLowerCase().contains(busqueda)){
                        mlist.add(i);
                    }
                }
            }
        }
        for( int i = 0; i < mlist.size(); ++i){
            if(!mlist.get(i).getTags().containsAll(tagsFiltered)){
                mlist.remove(i);
                --i;
            }

        }

        updateList();
    }


    public void updateList() {
        EventListAdapter adapter = new EventListAdapter(getContext(), mlist, this);
        recyclerView.setAdapter(adapter);
    }

    public void clickEvent(EventItem item) {
        mContainer.clickEvent(item);
    }

    public void filter( String search){
        busqueda = search.toLowerCase();
        updateFilters();

    }

    public void setUserEvents(List<EventItem> events) {
        mlist = events;
        updateList();
    }

    public void setUserJoinedEvents(List<EventItem> joined) {
        mlist = joined;
        updateList();
    }

    public void setUserLikedEvents(List<EventItem> liked) {
        mlist = liked;
        updateList();
    }


    public void setCompanyCreatedEvents(List<EventItem> events) {
        mlist = events;
        updateList();
    }
}