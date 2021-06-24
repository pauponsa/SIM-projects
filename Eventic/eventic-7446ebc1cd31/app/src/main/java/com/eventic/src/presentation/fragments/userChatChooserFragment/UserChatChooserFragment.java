package com.eventic.src.presentation.fragments.userChatChooserFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.domain.Chat;
import com.example.eventic.R;

import java.util.ArrayList;
import java.util.List;

public class UserChatChooserFragment extends Fragment {
    List<Chat> mlist;
    RecyclerView recyclerView;
    UserChatsContainer mContainer;

    public UserChatChooserFragment() {
        // Required empty public constructor
    }

    public void setContainer(UserChatsContainer mContainer) {
        this.mContainer = mContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_chat_chooser, container, false);

        recyclerView = view.findViewById(R.id.userchats);
        updateList();

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

        UserChatChooserAdapter adapter = new UserChatChooserAdapter(getContext(), mlist, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void addChat(Chat chat) {
        mlist.add((chat));
        updateList();
    }

    public void setChats(List<Chat> chat) {
        mlist = chat;
        updateList();
    }

    public void updateList() {
        UserChatChooserAdapter adapter = new UserChatChooserAdapter(getContext(), mlist, this);
        recyclerView.setAdapter(adapter);
    }

    public void clickChat(Chat chat) {
        mContainer.clickChat(chat);
    }
}