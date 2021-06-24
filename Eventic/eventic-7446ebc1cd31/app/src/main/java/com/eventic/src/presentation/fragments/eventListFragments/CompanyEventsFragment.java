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

import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CompanyEventsFragment extends EventContainer {
    FloatingActionButton createEventButton;
    ProgressBar progressBar;

    public CompanyEventsFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_company_events, container, false);
        createEventButton = view.findViewById(R.id.createEventConfirmButton);

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ((companyHomePageActivity)getActivity()).changeToCreateEventActivity();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.companyEventsProgressBar);

        eventList = new EventListFragment();
        eventList.setContainer(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.companyEventsEventList, eventList).commit();

        ((companyHomePageActivity)getActivity()).eventListFragmentReady();
    }

    @Override
    public void clickEvent(EventItem item) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("id", item.getId());
        getActivity().startActivity(intent);
    }

    public void setLoading(boolean loading) {
        if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        else progressBar.setVisibility(ProgressBar.INVISIBLE);

    }
}