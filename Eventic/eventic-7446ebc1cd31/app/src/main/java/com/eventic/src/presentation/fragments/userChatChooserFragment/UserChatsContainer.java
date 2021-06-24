package com.eventic.src.presentation.fragments.userChatChooserFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eventic.src.domain.Chat;

import java.util.List;


public abstract class UserChatsContainer extends Fragment {
    UserChatChooserFragment chatList;

    abstract public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void addChat(Chat chat) {
        chatList.addChat(chat);
    }

    public void setChats(List<Chat> chats) {
        chatList.setChats(chats);
    }

    abstract public void clickChat(Chat chat);
}
