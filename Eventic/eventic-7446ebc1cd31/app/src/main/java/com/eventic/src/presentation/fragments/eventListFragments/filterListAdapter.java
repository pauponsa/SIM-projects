package com.eventic.src.presentation.fragments.eventListFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.domain.Tag;
import com.eventic.src.presentation.components.TagChip;
import com.eventic.src.presentation.activities.event.EventContract;
import com.eventic.src.presentation.fragments.eventListFragments.eventItem.EventItem;
import com.example.eventic.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class filterListAdapter extends RecyclerView.Adapter<filterListAdapter.myViewHolder2> {

    Context mContext;
    List<String> mData;
    FilterListFragment mFragment;
    List<EventItem> l;



    public filterListAdapter(Context mContext, List<String> mData, FilterListFragment mFragment,  List<EventItem> l) {
        this.mContext = mContext;
        this.mData = mData;
        this.mFragment = mFragment;
        this.l = l;
    }

    @NonNull
    @Override
    public myViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.event_filter, parent, false);
        return new myViewHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder2 holder, int position) {
        String tagText = mData.get(position);
        Tag a = new Tag();
        a.setTag_name(tagText);

        if (tagText == "recently") holder.button.setChipIcon(mContext.getDrawable(R.drawable.ic_clock_solid));
        else if (tagText == "price") holder.button.setChipIcon(mContext.getDrawable(R.drawable.ic_money_bill_wave_solid));
        else if (tagText == "location") holder.button.setChipIcon(mContext.getDrawable(R.drawable.ic_location_solid));
        else holder.button.setChipIcon(mContext.getDrawable(R.drawable.ic_tag_plus));

        holder.button.setTag(a);
        holder.button.setCheckable(true);
        holder.button.setChipIconTint(mContext.getResources().getColorStateList(R.color.mainText));
        holder.button.setChipBackgroundColorResource(R.color.cardBackground);
        holder.button.setOnCheckedChangeListener(new Chip.OnCheckedChangeListener()
         {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked){

                     if (tagText == "recently" || tagText == "price" || tagText == "location")
                     {
                        mFragment.unselectSorts();
                        holder.button.setChipIconTint(mContext.getResources().getColorStateList(R.color.eventic_blue));
                        holder.button.setChipBackgroundColorResource(R.color.eventic_lightest);
                        mFragment.selectSort(tagText);
                     }
                     else
                     {
                         holder.button.setChipIcon(mContext.getResources().getDrawable(R.drawable.ic_tag_remove));
                         holder.button.setChipIconTint(mContext.getResources().getColorStateList(R.color.eventic_blue));
                         holder.button.setChipBackgroundColorResource(R.color.eventic_lightest);
                         mFragment.addFilter(tagText);
                     }


                 }
                 else{

                     if (tagText == "recently" || tagText == "price" || tagText == "location")
                     {
                         mFragment.unselectSorts();
                         holder.button.setChipIconTint(mContext.getResources().getColorStateList(R.color.eventic_blue));
                         holder.button.setChipBackgroundColorResource(R.color.eventic_lightest);
                         mFragment.selectSort(tagText);
                     }
                     else
                     {
                         holder.button.setChipIcon(mContext.getResources().getDrawable(R.drawable.ic_tag_plus));
                         holder.button.setChipIconTint(mContext.getResources().getColorStateList(R.color.mainText));
                         holder.button.setChipBackgroundColorResource(R.color.cardBackground);
                         mFragment.removeFilter(tagText);
                     }

                 }
             }

         }
        );
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class myViewHolder2 extends RecyclerView.ViewHolder {
        TagChip button;

        public myViewHolder2(@NonNull View itemView){
            super(itemView);
            button = itemView.findViewById(R.id.filterButton);

            //itemView.setOnClickListener(this);
        }

        /*public void onClick(View view) {
        }*/
    }

}
