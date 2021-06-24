package com.eventic.src.presentation.activities.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.domain.Message;
import com.example.eventic.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class chatActivity extends AppCompatActivity implements chatContract.View, View.OnClickListener {

    private chatContract.Presenter mPresenter;
    private ImageButton sendButton;
    private ImageView userProfileImage;
    private RecyclerView recyclerChat;
    private List<Message> mlist;

    private TextView chatName, chatCreator, messageText;
    private chatAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mPresenter = new chatPresenter(this);

        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        databaseReference = database.getReference();

        setupViews();

        setupMessages();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });
        SharedPreferences chatPreferences = getChatPreferences();

        // Read from the database
        String eventID = String.valueOf(chatPreferences.getInt("eventID",0));
        String creatorID = String.valueOf(chatPreferences.getInt("creatorID",0));

        mPresenter.loadInfo(Integer.parseInt(eventID), Integer.parseInt(creatorID));

        databaseReference.child("users").child(creatorID).child(eventID).push().setValue("");

        databaseReference.child("chat").child(eventID).child(creatorID).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                SharedPreferences userPreferences = getUserPreferences();
                Message m = snapshot.getValue(Message.class);

                if(m.getSenderID() != userPreferences.getInt("id", 0)) m.setType(Message.Type.received);
                adapter.addMessage(m);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    void setupViews() {
        messageText = findViewById(R.id.messageText);
        sendButton = findViewById(R.id.sendButton);
        recyclerChat = findViewById(R.id.messages);
        chatName = findViewById(R.id.chatName);
        chatCreator = findViewById(R.id.chatCreator);
        userProfileImage = findViewById(R.id.userProfileImage);
        sendButton.setOnClickListener(this);
    }

    public void setupMessages() {
        mlist = new ArrayList<>();
        // Month must be from 0 to 11
        adapter = new chatAdapter(this, mlist);
        recyclerChat.setAdapter(adapter);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendButton) {
            if(!messageText.getText().toString().equals("")){
                SharedPreferences chatPreferences = getChatPreferences();
                SharedPreferences userPreferences = getUserPreferences();

                String eventID = String.valueOf(chatPreferences.getInt("eventID",0));
                String creatorID = String.valueOf(chatPreferences.getInt("creatorID",0));

                databaseReference.child("chat").child(eventID)
                                 .child(creatorID).push()
                                 .setValue(new Message(messageText.getText().toString(),
                                                       Message.Type.sent,
                                                       userPreferences.getInt("id",0),
                                                        Calendar.getInstance(TimeZone.getDefault()).getTime().getTime()));
            }
            messageText.setText("");
        }
        //else if(v.getId() == R.id.) {}
    }

    public void finishActivity() {
        this.finish();
    }

    private void setScrollbar(){
        recyclerChat.scrollToPosition(adapter.getItemCount()-1);
    }

    public SharedPreferences getUserPreferences() {
        return getSharedPreferences("userPreferences", this.MODE_PRIVATE);
    }

    private SharedPreferences getChatPreferences() {
        return getSharedPreferences("chatPreferences", this.MODE_PRIVATE);

    }

    public void setChatTitle(String title)
    {
        chatName.setText(title);
    }

    public void setChatCreatorName(String name)
    {
        chatCreator.setText(name);
    }

    public void setUserProfileImage(String url) {
        Picasso.get().load(url)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.eventic_e)
                .error(R.drawable.eventic_e)
                .into(userProfileImage);
    }
}
