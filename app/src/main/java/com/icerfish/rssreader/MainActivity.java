package com.icerfish.rssreader;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.icerfish.rssreader.data.Article;
import com.icerfish.rssreader.data.ArticleDatabase;
import com.icerfish.rssreader.data.ArticleProvider;
import com.icerfish.rssreader.data.ArticleXMLParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity
        implements ArticleListFragment.Callbacks {

    public static final String TAG = MainActivity.class
            .getSimpleName();

    private FragmentManager manager = getSupportFragmentManager();
    private ArticleXMLParser parser = new ArticleXMLParser();
    private ArticleDetailFragment fragment;

    private boolean actionBarItemState = false;

    private List<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.fragment_article_list);

        ArticleListFragment fragment = new ArticleListFragment();

        manager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        requestXML();
    }

    /**
     * Callback method from {@link ArticleListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ArticleDetailFragment.ARG_ITEM_ID, id);
            fragment = new ArticleDetailFragment();
            fragment.setArguments(arguments);
            manager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();

    }

    private void requestXML(){
        // Tag used to cancel the request
        String  tag_string_req = "string_req";

        String url = "http://feeds.bbci.co.uk/news/rss.xml";

        setProgressBarIndeterminateVisibility(true);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                ParseXML task = new ParseXML();
                task.execute(new String[] { response.toString() });

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                setProgressBarIndeterminateVisibility(false);
                displayErrorDialog(R.string.message_error_downloading);

            }
        });

        AppController ac = AppController.getInstance();
        ac.addToRequestQueue(strReq, tag_string_req);
    }


    private class ParseXML extends AsyncTask<String, Void, Boolean> {


        protected Boolean doInBackground(String... xml) {
            try {
                articles = parser.parse(xml[0]);

                Log.i(TAG, "Articles size: " + articles.size());

                getContentResolver().delete(ArticleProvider.CONTENT_URI, null, null);

                for(int i = 0; i < articles.size(); i++) {
                    Article article = articles.get(i);

                    ContentValues values = new ContentValues();
                    values.put(ArticleDatabase.COL_TITLE, article.getTitle());
                    values.put(ArticleDatabase.COL_DESCRIPTION, article.getDescription());
                    values.put(ArticleDatabase.COL_LINK, article.getLink());
                    values.put(ArticleDatabase.COL_PUB_DATE, article.getPubDate());
                    values.put(ArticleDatabase.COL_THUMBNAIL_SMALL, article.getThumbnailUrlSmall());
                    values.put(ArticleDatabase.COL_THUMBNAIL_LARGE, article.getThumbnailUrlLarge());


                    Uri rawArticleUri =
                            getContentResolver().insert(ArticleProvider.CONTENT_URI, values);

                }

                return true;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            if(!result) {
                displayErrorDialog(R.string.message_error_parsing);
            }

            setProgressBarIndeterminateVisibility(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        MenuItem refresh = menu.findItem(R.id.action_refresh);
        MenuItem share = menu.findItem(R.id.action_share);

        refresh.setVisible(!actionBarItemState);
        share.setVisible(actionBarItemState);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_share:
                shareItem();
                return true;
            case R.id.action_refresh:
                requestXML();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareItem(){
        if(fragment != null){
            String title = fragment.getTitle();
            String link = fragment.getLink();

            if(link != null || link.equals("")){
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);


                share.putExtra(Intent.EXTRA_SUBJECT, title);
                share.putExtra(Intent.EXTRA_TEXT, link);

                startActivity(Intent.createChooser(share, "Share link!"));
            }
        }
    }

    /*
    Switch visiblity of buttons. True means Share, false means Refresh;
     */
    public void switchActionBarItemVisibility(boolean state){
        actionBarItemState = state;
        this.invalidateOptionsMenu();
    }

    private void displayErrorDialog(int resId){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set title
        alertDialogBuilder.setTitle(R.string.message_error);

        // set dialog message
        alertDialogBuilder
                .setMessage(resId)
                .setCancelable(true)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //Do nothing
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
