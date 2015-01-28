package com.icerfish.rssreader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.icerfish.rssreader.font.VarelaFont;


public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
        setTypeface(VarelaFont.getInstance(context).getTypeFace());
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(VarelaFont.getInstance(context).getTypeFace());
    }

    public CustomTextView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(VarelaFont.getInstance(context).getTypeFace());
    }
}
