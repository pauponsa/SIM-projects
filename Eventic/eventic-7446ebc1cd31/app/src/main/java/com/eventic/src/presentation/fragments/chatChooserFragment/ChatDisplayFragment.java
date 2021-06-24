package com.eventic.src.presentation.fragments.chatChooserFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.eventic.src.domain.Chat;
import com.eventic.src.presentation.activities.chat.chatActivity;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;
import com.eventic.src.presentation.fragments.eventListFragments.EventContainer;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;

public class ChatDisplayFragment extends ChatsContainer {
    //FragmentTransaction transaction;
    ProgressBar progressBar;

    public ChatDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //System.out.println("[DEBUG]: onCreateView...");
        return inflater.inflate(R.layout.fragment_chat_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatList = new ChatChooserFragment();
        chatList.setContainer(this);
        progressBar = view.findViewById(R.id.chatDisplayProgressBar);
        //setLoading(true);
        //System.out.println("[DEBUG]: onViewCreated...");

        //if (progressBar == null) System.out.println("[DEBUG/onViewCreated]: progressBar is null");

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.chatDisplayChatList, chatList).commit();

    }

    @Override
    public void clickChat(Chat chat) {
        Intent intent = new Intent(getActivity(), chatActivity.class);
        SharedPreferences chatPreferences = ((companyHomePageActivity) getActivity()).getChatPreferences();
        chatPreferences.edit().putInt("eventID",chat.getEventID()).apply();
        chatPreferences.edit().putInt("creatorID",chat.getCreatorID()).apply();
        getActivity().startActivity(intent);
    }

    public void setLoading(boolean loading) {
        //System.out.println("[DEBUG]: setLoading...");


        //if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        //else progressBar.setVisibility(ProgressBar.INVISIBLE);

    }
}