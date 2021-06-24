package com.eventic.src.presentation.fragments.chatChooserFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.domain.Chat;
import com.eventic.src.domain.Event;
import com.eventic.src.domain.User;
import com.eventic.src.presentation.activities.chat.chatContract;
import com.example.eventic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatChooserAdapter extends RecyclerView.Adapter<ChatChooserAdapter.myViewHolder> {

    Context mContext;
    List<Chat> mData;
    ChatChooserFragment mFragment;


    public ChatChooserAdapter(Context mContext, List<Chat> mData, ChatChooserFragment mFragment) {
        this.mContext = mContext;
        this.mData = mData;
        this.mFragment = mFragment;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.chat, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragment.clickChat(mData.get(position));
            }
        });

        //holder.eventImage.setImageResource(mData.get(position).getEventImage());
        //holder.eventTitle.setText(mData.get(position).getEventTitle());
        loadChatInfo(holder, position);
    }

    private void loadChatInfo(myViewHolder holder, int position)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        chatContract.JsonHttpApi jsonHttpApi = retrofit.create(chatContract.JsonHttpApi.class);

        Integer event_id = mData.get(position).getEventID();
        Call<Event> call = jsonHttpApi.getEventById(event_id);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Event eventResponse = response.body();
                System.out.println("Event responded");
                if (!response.isSuccessful()) {
                    System.out.println("Event response failed");
                    return;
                }

                String event_name = eventResponse.getTitle();
                holder.eventTitle.setText(event_name);

                int idUser = mData.get(position).getCreatorID();
                loadUserInfo(holder, position, idUser);

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                return;
            }
        });
    }

    private void loadUserInfo(myViewHolder holder, int position, Integer user_id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        chatContract.JsonHttpApi jsonHttpApi = retrofit.create(chatContract.JsonHttpApi.class);

        Call<User> call = jsonHttpApi.getUser(user_id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();

                System.out.println("User responded");

                if (!response.isSuccessful()) {
                    System.out.println("User response failed");
                    return;
                }

                String username = userResponse.getName();
                if (username == null || username.isEmpty()) username = userResponse.getUsername();
                holder.eventUser.setText(username);
                Picasso.get().load(userResponse.getImage())
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.eventic_e)
                        .error(R.drawable.eventic_e)
                        .into(holder.eventImage);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                return;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle;
        TextView eventUser;
        ImageView eventImage;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventUser = itemView.findViewById(R.id.companyNameEvent);
            eventImage = itemView.findViewById(R.id.eventImage);

        }
    }
}
