package com.eventic.src.presentation.activities.companyPublicPage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.domain.Tag;
import com.eventic.src.presentation.activities.event.EventActivity;
import com.eventic.src.presentation.activities.event.EventContract;
import com.eventic.src.presentation.components.TagChip;

import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PublicCompanyAdapter extends RecyclerView.Adapter<PublicCompanyAdapter.ViewHolder> {
    Context mContext;
    List<EventItem> mData;
    private List<EventItem> mDataFull;

    public PublicCompanyAdapter(Context mContext, List<EventItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataFull = new ArrayList<>();
        mDataFull.addAll(mData);
    }

    @NonNull
    @Override
    public PublicCompanyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.event_item, parent, false);
        return new PublicCompanyAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicCompanyAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EventActivity.class);
                intent.putExtra("id", mData.get(position).getId());
                mContext.startActivity(intent);

            }
        });
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


        showTags(holder, mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void showTags(PublicCompanyAdapter.ViewHolder holder, EventItem event) {
        Integer event_id = event.getId();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eventic-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventContract.JsonHttpApi jsonHttpApi = retrofit.create(EventContract.JsonHttpApi.class);

        Call<List<Tag>> call = jsonHttpApi.getTagsById(event_id);

        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                List<Tag> tagsResponse = response.body();
                if (!response.isSuccessful()) {
                    return;
                }
                ((ChipGroup)holder.tagsContainer).removeAllViews();
                for (int i = 0; i < tagsResponse.size(); ++i) {
                    event.addTag(tagsResponse.get(i).getTag_name());
                    TagChip newTag = new TagChip(mContext);
                    newTag.setTag(tagsResponse.get(i));

                    newTag.setChipIcon(mContext.getResources().getDrawable(R.drawable.ic_tag));
                    newTag.setChipIconTintResource(R.color.mainText);

                    ((ChipGroup)holder.tagsContainer).addView(newTag);
                }

            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                return;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle, eventDate, eventPrice;
        Chip tag1, tag2, tag3;
        ChipGroup tagsContainer;
        TextView eventCapacity;
        ImageView eventImage;



        public ViewHolder(@NonNull View itemView) {
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

