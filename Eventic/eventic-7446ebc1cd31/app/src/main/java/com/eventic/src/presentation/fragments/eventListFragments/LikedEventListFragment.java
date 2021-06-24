package com.eventic.src.presentation.fragments.eventListFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;

import java.util.ArrayList;
import java.util.List;

public class LikedEventListFragment extends Fragment {
    RecyclerView recyclerView;
    List<EventItem> mlist;
    EventContainer mContainer;
    View view;

    public LikedEventListFragment() {
        // Required empty public constructor
    }

    public void setContainer(EventContainer mContainer) {
        this.mContainer = mContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_liked_event_list, container, false);

        recyclerView = view.findViewById(R.id.likedEventsList);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setup();
        //((userHomePageActivity)getActivity()).likedFragmentReady();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((userHomePageActivity)getActivity()).likedFragmentReady();
    }

    public void setup(){
        // setup recycler view with adapter

        mlist = new ArrayList<>();

        LikedEventListAdapter adapter = new LikedEventListAdapter(getContext(), mlist, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void addEvent(EventItem event) {
        mlist.add((event));
        updateList();
    }

    public void setEvents(List<EventItem> events) {
        mlist = events;
        updateList();
    }

    public void updateList() {
        LikedEventListAdapter adapter = new LikedEventListAdapter(getContext(), mlist, this);
        recyclerView.setAdapter(adapter);
    }

    public void clickEvent(EventItem item) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("id", item.getId());

        getActivity().startActivity(intent);
    }

    public void setUserEvents(List<EventItem> events) {
        mlist = events;
        updateList();
    }

    public void setUserJoinedEvents(List<EventItem> joined) {
        mlist = joined;
        updateListUser();
    }

    public void setUserLikedEvents(List<EventItem> liked) {
        mlist = liked;
        updateListUser();
    }

    public void updateListUser() {
        LikedEventListAdapter adapter = new LikedEventListAdapter(getContext(), mlist, this);
        recyclerView.setAdapter(adapter);
    }
}