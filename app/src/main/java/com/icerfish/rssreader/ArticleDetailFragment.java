package com.icerfish.rssreader;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.icerfish.rssreader.data.ArticleDatabase;
import com.icerfish.rssreader.data.ArticleProvider;

import java.text.ParseException;


public class ArticleDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String TAG = ArticleDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    protected Handler handler = new Handler();

    public String aId;

    static final String[] ARTICLE_PROJECTION = new String[] {
            ArticleDatabase.ID,
            ArticleDatabase.COL_TITLE,
            ArticleDatabase.COL_PUB_DATE,
            ArticleDatabase.COL_DESCRIPTION,
            ArticleDatabase.COL_THUMBNAIL_LARGE,
            ArticleDatabase.COL_LINK
    };

    private TextView aTitle;
    private TextView aDescription;
    private TextView aDate;
    private NetworkImageView aImage;

    private String title;
    private String date;
    private String description;
    private String thumbnailUrl;
    private String link;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            aId = getArguments().getString(ARG_ITEM_ID);

            Log.i(TAG, "aID: " + aId);
        }

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);



        aTitle = (TextView) rootView.findViewById(R.id.title);
        aDate = (TextView) rootView.findViewById(R.id.pub_date);
        aDescription = (TextView) rootView.findViewById(R.id.description);
        aImage = (NetworkImageView) rootView.findViewById(R.id.largeThumbnail);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), ArticleProvider.CONTENT_URI,
                ARTICLE_PROJECTION, ArticleDatabase.ID + "='" + aId + "'", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            title = data.getString(data.getColumnIndex(ArticleDatabase.COL_TITLE));
            date = data.getString(data.getColumnIndex(ArticleDatabase.COL_PUB_DATE));
            description = data.getString(data.getColumnIndex(ArticleDatabase.COL_DESCRIPTION));
            thumbnailUrl = data.getString(data.getColumnIndex(ArticleDatabase.COL_THUMBNAIL_LARGE));
            link = data.getString(data.getColumnIndex(ArticleDatabase.COL_LINK));


            handler.post(new Runnable() {
                public void run() {
                    aTitle.setText(title);
                    try {
                        aDate.setText(AppController.formatDate(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    aDescription.setText(description);
                    aImage.setDefaultImageResId(R.drawable.ic_launcher);
                    aImage.setImageUrl(thumbnailUrl, AppController.getInstance().getImageLoader());

                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).switchActionBarItemVisibility(true);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        ((MainActivity) getActivity()).switchActionBarItemVisibility(false);

    }

    public String getLink(){
        return link;
    }

    public String getTitle(){
        return title;
    }
}
