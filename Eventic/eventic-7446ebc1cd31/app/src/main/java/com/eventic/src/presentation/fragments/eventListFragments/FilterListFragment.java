package com.eventic.src.presentation.fragments.eventListFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class FilterListFragment extends Fragment {
    RecyclerView recyclerView;
    List<String> mlist;
    EventContainer mContainer;
    List<EventItem> l;
    LinearLayoutManager filtersContainer;


    public FilterListFragment() {
        // Required empty public constructor
    }

    public void setContainer(EventContainer mContainer) {
        this.mContainer = mContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_list, container, false);


        recyclerView = view.findViewById(R.id.myFiltersList);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setup();
    }

    public void setup(){
        // setup recycler view with adapter

        mlist = new ArrayList<>();
        List<String> tags = new ArrayList<String>();
        tags.add("recently");
        tags.add("price");
        tags.add("concerts");
        tags.add("festivals");
        tags.add("culture");
        tags.add("gastronomy");
        tags.add("education");
        tags.add("productivity");
        tags.add("leisure");
        tags.add("music");
        tags.add("museums");
        tags.add("concerts");
        mlist = tags;
        filterListAdapter adapter = new filterListAdapter(getContext(), mlist, this, l);
        recyclerView.setAdapter(adapter);
        filtersContainer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(filtersContainer);
    }

    public void unselectSorts()
    {
        Chip recentlyChip = ((Chip)((ConstraintLayout)filtersContainer.findViewByPosition(0)).findViewById(R.id.filterButton));
        Chip priceChip = ((Chip)((ConstraintLayout)filtersContainer.findViewByPosition(1)).findViewById(R.id.filterButton));

        recentlyChip.setChipIconTint(getContext().getResources().getColorStateList(R.color.mainText));
        priceChip.setChipIconTint(getContext().getResources().getColorStateList(R.color.mainText));

        recentlyChip.setChipBackgroundColorResource(R.color.cardBackground);
        priceChip.setChipBackgroundColorResource(R.color.cardBackground);


    }

    public void setEventsForFilters(List<EventItem> events) {
        l = events;
        updateList();
    }

    public void addFilter(String filter) {
        ((EventDisplayFragment)mContainer).addFilter(filter);
    }
    public void removeFilter(String filter){
        ((EventDisplayFragment)mContainer).removeFilter(filter);
    }
    public void selectSort(String sort) {
        ((EventDisplayFragment)mContainer).sortBy(sort);
    }


    public void setFilters(List<String> filters) {
        mlist = filters;
        updateList();
    }

    public void updateList() {
        filterListAdapter adapter = new filterListAdapter(getContext(), mlist, this, l);
        recyclerView.setAdapter(adapter);
    }

}