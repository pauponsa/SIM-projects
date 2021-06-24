package com.eventic.src.presentation.fragments.eventListFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;

import java.util.List;

public class MyEventsFragment extends EventContainer {
    View v;
    ProgressBar progressBar;

    public MyEventsFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //System.out.println("[DEBUG]: onCreateView...");
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        eventList = new EventListFragment();
        eventList.setContainer(this);


        progressBar = view.findViewById(R.id.myEventsProgressBar);
        //setLoading(true);
        //System.out.println("[DEBUG]: onViewCreated...");

        //if (progressBar == null) System.out.println("[DEBUG/onViewCreated]: progressBar is null");
        ((userHomePageActivity)getActivity()).eventListFragmentReady();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.myEventsEventList, eventList).commit();
    }

    @Override
    public void clickEvent(EventItem item) {

        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("id", item.getId());
        getActivity().startActivity(intent);
    }

    public void setLoading(boolean loading)
    {
        //System.out.println("[DEBUG]: setLoading...");


        if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        else progressBar.setVisibility(ProgressBar.INVISIBLE);

    }
}