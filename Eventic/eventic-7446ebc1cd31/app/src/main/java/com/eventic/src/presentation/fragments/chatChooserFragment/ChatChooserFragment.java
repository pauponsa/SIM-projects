package com.eventic.src.presentation.fragments.chatChooserFragment;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventic.src.domain.Chat;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.companyHomePage.companyHomePagePresenter;
import com.example.eventic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ChatChooserFragment extends Fragment {
    List<Chat> mlist;
    RecyclerView recyclerView;
    ChatsContainer mContainer;

    public ChatChooserFragment() {
        // Required empty public constructor
    }

    public void setContainer(ChatsContainer mContainer) {
        this.mContainer = mContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_chooser, container, false);

        recyclerView = view.findViewById(R.id.chats);
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

        ChatChooserAdapter adapter = new ChatChooserAdapter(getContext(), mlist, this);
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
        ChatChooserAdapter adapter = new ChatChooserAdapter(getContext(), mlist, this);
        recyclerView.setAdapter(adapter);
    }

    public void clickChat(Chat chat) {
        mContainer.clickChat(chat);
    }
}