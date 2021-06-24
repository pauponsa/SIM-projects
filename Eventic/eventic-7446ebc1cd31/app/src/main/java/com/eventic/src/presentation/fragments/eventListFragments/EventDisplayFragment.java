package com.eventic.src.presentation.fragments.eventListFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.eventic.src.domain.Event;
import com.eventic.src.domain.Tag;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventDisplayFragment extends EventContainer {
    //FragmentTransaction transaction;
    View v;
    ProgressBar progressBar;
    SearchView searchView;
    //EventListAdapter adapter;

    public EventDisplayFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //System.out.println("[DEBUG]: onCreateView...");
        return inflater.inflate(R.layout.fragment_event_display, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        eventList = new EventListFragment();
        eventList.setContainer(this);
        filterList = new FilterListFragment();
        filterList.setContainer(this);

        progressBar = view.findViewById(R.id.eventDisplayProgressBar);
        searchView = view.findViewById(R.id.searchView);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                eventList.filter(newText);
                return false;
            }
        });
        //setLoading(true);
        //System.out.println("[DEBUG]: onViewCreated...");

        //if (progressBar == null) System.out.println("[DEBUG/onViewCreated]: progressBar is null");
        ((userHomePageActivity)getActivity()).eventListFragmentReady();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.eventDisplayEventList, eventList);
        transaction.replace(R.id.filterDisplay, filterList).commitNow();

    }

    @Override
    public void clickEvent(EventItem item) {

        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("id", item.getId());

        getActivity().startActivity(intent);
    }


    public void clickFilter(String item, List<EventItem> l) {
        if(!item.equals("recently") && !item.equals("location") && !item.equals("price")) {
            //List<EventItem> filtered = searchEvents(item, l);
            //setEvents(filtered);

         //   eventList.searchEvents(item);
        }
        else{
            //List<EventItem> sorted = sortBy(item,l);
            //setEvents(sorted);

            eventList.sortBy(item);

        }

    }
    public void addFilter(String filter){
        eventList.addFilter(filter);
    }
    public void removeFilter(String filter){
        eventList.removeFilter(filter);
    }
    public void sortBy(String sort) {
        eventList.sortBy(sort);
    }

    /*private List<EventItem> sortBy(String item, List<EventItem> l) {
        if(item.equals("recently")) {
            Collections.sort(l, EventItem.fecha);
          }
        else if(item.equals("price")){
            Collections.sort(l,EventItem.precio);
        }

        return l;

    }

    private List<EventItem> searchEvents(String item, List<EventItem> l) {
       List<EventItem> result =  new ArrayList<>();
        for( EventItem i : l){
            for( String j : i.getTags()){
                if(j.equals(item) && !result.contains(i)){
                    result.add(i);
                }

            }

        }
        return result;
    }*/


    public void setLoading(boolean loading) {
        //System.out.println("[DEBUG]: setLoading...");


        //if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        //else progressBar.setVisibility(ProgressBar.INVISIBLE);

    }


}