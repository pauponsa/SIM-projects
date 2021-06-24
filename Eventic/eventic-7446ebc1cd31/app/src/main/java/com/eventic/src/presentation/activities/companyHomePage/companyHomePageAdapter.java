package com.eventic.src.presentation.activities.companyHomePage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class companyHomePageAdapter extends RecyclerView.Adapter<companyHomePageAdapter.myViewHolder> {

    Context mContext;
    List<EventItem> mData;

    public companyHomePageAdapter(Context mContext, List<EventItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.event_item, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((HomePageActivity)  mContext).moveToEvent();
            }
        });
//        holder.eventImage.setImageResource(mData.get(position).getEventImage());
        //String distance = mData.get(position).getDistance() + mContext.getString(R.string.kilometers);
        holder.eventPrice.setText(mData.get(position).getPrice());
        holder.eventDate.setText(mData.get(position).getDate());
        holder.eventTitle.setText(mData.get(position).getEventTitle());
        String capacity = mData.get(position).getJoined() + "/" + mData.get(position).getEventCapacity();
        holder.eventCapacity.setText(capacity);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        TextView eventTitle, eventDate, eventPrice;
        //TextView tag1, tag2, tag3, tag4;
        Chip tag1, tag2, tag3, tag4;
        TextView eventCapacity;
        Button likeButton;
        ImageView eventImage;


        public myViewHolder (@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventCapacity = itemView.findViewById(R.id.eventCapacity);
            eventImage = itemView.findViewById(R.id.eventImage);

            eventDate = itemView.findViewById(R.id.eventDate);
            eventPrice = itemView.findViewById(R.id.eventPrice);
            tag1 = itemView.findViewById(R.id.fecha);
            tag2 = itemView.findViewById(R.id.distancia);
            tag3 = itemView.findViewById(R.id.asistentes);

        }

    }
}
