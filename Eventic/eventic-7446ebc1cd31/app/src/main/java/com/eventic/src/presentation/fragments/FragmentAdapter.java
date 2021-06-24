package com.eventic.src.presentation.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.eventic.src.presentation.fragments.eventListFragments.FollowedEventListFragment;
import com.eventic.src.presentation.fragments.eventListFragments.JoinedEventListFragment;
import com.eventic.src.presentation.fragments.eventListFragments.LikedEventListFragment;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;

import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {
    LikedEventListFragment likedFragment;
    JoinedEventListFragment joinedFragment;
    FollowedEventListFragment followedFragment;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position){
            case 1:
                likedFragment = new LikedEventListFragment();
                return likedFragment;
            case 2:
                joinedFragment = new JoinedEventListFragment();
                return joinedFragment;
        }
        followedFragment = new FollowedEventListFragment();
        return followedFragment;
        /*if (position == 1) {

            return likedFragment;
        }
        else if (position == 2) {
            return joinedFragment;
        }

        return followedFragment;*/
    }


    @Override
    public int getItemCount() {
        return 3;
    }


    public void setLikedEvents(List<EventItem> liked) {
        likedFragment.setEvents(liked);
    }

    public void setFollowedEvents(List<EventItem> followed) {
        followedFragment.setEvents(followed);
    }

    public void setJoinedEvents(List<EventItem> joined) {
        joinedFragment.setEvents(joined);
    }
}