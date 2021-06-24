package com.eventic.src.presentation.fragments.eventListFragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.domain.Tag;
import com.eventic.src.presentation.activities.event.EventContract;
import com.eventic.src.presentation.components.TagChip;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinedEventListAdapter extends RecyclerView.Adapter<JoinedEventListAdapter.myViewHolder> {

    Context mContext;
    List<EventItem> mData;
    JoinedEventListFragment joinedFragment;


    public JoinedEventListAdapter(Context mContext, List<EventItem> mData, JoinedEventListFragment mFragment) {
        this.mContext = mContext;
        this.mData = mData;
        joinedFragment = mFragment;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.mini_event_item, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinedFragment.clickEvent(mData.get(position));
            }
        });


//        holder.eventImage.setImageResource(mData.get(position).getEventImage());
        Picasso.get().load(mData.get(position).getEventImage()).placeholder(R.drawable.event01).error(R.drawable.event01).into(holder.eventImage);
        String pr = mData.get(position).getPrice().toString() + "€";
        if (pr.equals("0€")) { holder.eventPrice.setText(mContext.getString(R.string.FREE));}
        else {
            holder.eventPrice.setText(pr);
        }
        holder.eventDate.setText(mData.get(position).getDate());
        holder.eventTitle.setText(mData.get(position).getEventTitle());
        String capacity = mData.get(position).getJoined() + "/" + mData.get(position).getEventCapacity();
        holder.eventCapacity.setText(capacity);


    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle, eventDate, eventPrice;
        //TextView tag1, tag2, tag3, tag4;
        Chip tag1, tag2, tag3, tag4;
        ChipGroup tagsContainer;
        TextView eventCapacity;
        ImageButton likeButton;
        ImageView eventImage;



        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventCapacity = itemView.findViewById(R.id.eventCapacity);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventPrice = itemView.findViewById(R.id.eventPrice);
            tagsContainer = itemView.findViewById(R.id.eventTagsContainer);

            tag1 = itemView.findViewById(R.id.fecha);
            tag2 = itemView.findViewById(R.id.distancia);
            tag3 = itemView.findViewById(R.id.asistentes);
        }


    }


}
