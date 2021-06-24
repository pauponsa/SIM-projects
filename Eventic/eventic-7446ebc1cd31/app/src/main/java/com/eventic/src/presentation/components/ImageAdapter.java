package com.eventic.src.presentation.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.eventic.R;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private String[] imageUrls;

    public ImageAdapter(Context context, String[] urls){
        mContext = context;
        imageUrls = urls;
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView((mContext));
        Picasso.get()
                .load(imageUrls[position])
                .placeholder(R.drawable.eventic_e)   //this is optional the image to display while the url image is downloading
                .error(R.drawable.eventic_e)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                .fit()
                .centerCrop()
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
