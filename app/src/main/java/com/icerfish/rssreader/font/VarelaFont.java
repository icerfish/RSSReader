package com.icerfish.rssreader.font;

import android.content.Context;
import android.graphics.Typeface;

public class VarelaFont {
    private static VarelaFont instance;
    private static Typeface typeface;

    public static VarelaFont getInstance(Context context) {
        synchronized (VarelaFont.class) {
            if (instance == null) {
                instance = new VarelaFont();
                typeface = Typeface.createFromAsset(context.getResources().getAssets(), "Varela-Regular.ttf");
            }
            return instance;
        }
    }

    public Typeface getTypeFace() {
        return typeface;
    }
}
