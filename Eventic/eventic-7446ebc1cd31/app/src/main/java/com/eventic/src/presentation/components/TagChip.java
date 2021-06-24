package com.eventic.src.presentation.components;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.eventic.src.domain.Tag;
import com.example.eventic.R;
import com.google.android.material.chip.Chip;

public class TagChip extends Chip {
    Tag mTag;

    public TagChip(Context context) {
        super(context);
    }

    public TagChip(Context context, AttributeSet attrs)
    {
        super(context, attrs);

    }

    public void setTag(Tag tag)
    {
        mTag = tag;


        int textId = getResources().getIdentifier("tag_" + mTag.getTag_name(), "string", getContext().getPackageName());
        if (textId != 0)
        {
            super.setText(getResources().getText(textId));
        }

        else
        {
            //textId = R.string.tag_not_found;
            super.setText(mTag.getTag_name());
        }

        invalidate();
        requestLayout();
    }

    public Tag getTag() {
        return mTag;
    }
}
