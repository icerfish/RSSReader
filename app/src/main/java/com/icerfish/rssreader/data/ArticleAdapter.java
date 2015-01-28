package com.icerfish.rssreader.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.icerfish.rssreader.AppController;
import com.icerfish.rssreader.R;

import java.text.ParseException;

/**
 * Created by dylanturney on 26/01/15.
 */
public class ArticleAdapter extends SimpleCursorAdapter {

    public static String TAG = ArticleAdapter.class.getSimpleName();

    Context mContext;
    private int layout;
    private Cursor cursor;
    private final LayoutInflater inflater;


    public ArticleAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.mContext = context;
        this.layout = layout;
        this.cursor = c;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView title = (TextView)view.findViewById(R.id.title);
        TextView pubDate = (TextView)view.findViewById(R.id.pub_date);
        TextView description = (TextView)view.findViewById(R.id.description);
        NetworkImageView smallThumbnail = (NetworkImageView) view.findViewById(R.id.smallThumbnail);


        int title_index = cursor.getColumnIndexOrThrow(ArticleDatabase.COL_TITLE);
        int pub_date_index = cursor.getColumnIndexOrThrow(ArticleDatabase.COL_PUB_DATE);
        int description_index = cursor.getColumnIndexOrThrow(ArticleDatabase.COL_DESCRIPTION);
        int small_thumnail_index = cursor.getColumnIndexOrThrow(ArticleDatabase.COL_THUMBNAIL_SMALL);


        smallThumbnail.setDefaultImageResId(R.drawable.ic_launcher);
        smallThumbnail.setImageUrl(cursor.getString(small_thumnail_index), AppController.getInstance().getImageLoader());

        title.setText(cursor.getString(title_index));


        try {
            pubDate.setText(AppController.formatDate(cursor.getString(pub_date_index)));
        } catch (ParseException e) {
            e.printStackTrace();
            pubDate.setText("Format Error");
        }
        description.setText(cursor.getString(description_index));

    }
}
