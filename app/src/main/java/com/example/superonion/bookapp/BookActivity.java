package com.example.superonion.bookapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BookActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<List<Books>>{
    /*
    * String to store the URL data
    *
    * https://developers.google.com/books/docs/v1/using
    * Go there for further information
    *
    *
     */
    private static final String GOOGLE_BOOKS_API =
            "https://www.googleapis.com/books/v1/volumes?q=flowers&key=AIzaSyAn2KNpdIoz0SZYh545NfIPKxVCvVK9uBw";
    private static final String GOOGLE_BOOKS_BASE=
            "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String MY_API_KEY=
            "&key=AIzaSyAn2KNpdIoz0SZYh545NfIPKxVCvVK9uBw";
    String searchGeneral ="";
    String searchTitle ="";
    String searchAuthor ="";
    String searchCategory ="";
    String searchType ="";
    String maxResults ="";
    String fullSearch ="";

    private static final String TAG = "Book Activity:";
    BookAdapter mAdapter;
    LinearLayout emptyView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        themeChooser(pref.getString("theme","1"));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_list_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Search Results");
        Bundle extras = getIntent().getExtras();
        formURL(extras);
        // Instantiate the progressBar/searchingView/emptyView at runtime
        // Set its VISIBILITY levels at runtime.
        // Will have to disable them on completion.
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(VISIBLE);
        emptyView = findViewById(R.id.emptyView);

        /**
         *
         * Injection of an artificial delay
         * Using this method, we inject an artifical delay to show cute animations
         * The progress bar will also be running to show the user that something is happening
         * Artificial delay is meant to represent real life situations where
         * a user's internet is unstable or slow.
         *
         */
        //final Handler handler = new Handler();
        //handler.postDelayed(new Runnable() {
        //   @Override
        //    public void run() {
        //        // Do something after 5s = 5000ms
        //    }
        //}, 3000);


        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        // No need to pass in any args cause we only need the URL which is created in the loader already.
        loaderManager.initLoader(1, null, BookActivity.this);


        mAdapter = new BookAdapter(BookActivity.this, 0, new ArrayList<Books>());
        final ListView listView = (ListView) findViewById(R.id.full_list);
        listView.setAdapter(mAdapter);



    }

    private void formURL (Bundle extras) {
        SharedPreferences pref = android.support.v7.preference.PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean prefBooks = pref.getBoolean("showBooks", true);
        boolean prefMags = pref.getBoolean("showMags", true);
        String prefResults = pref.getString("resultsReturned", "1");

        if (extras != null) {
            if (!(TextUtils.isEmpty(extras.getString("general")))) {
                searchGeneral = extras.getString("general").replace(" ", "+");
                // The key/argument here must match that used in the other activity
            }
            if (!(TextUtils.isEmpty(extras.getString("intitle")))) {
                searchTitle = "+intitle:" + extras.getString("intitle").replace(" ", "+");
            }
            if (!(TextUtils.isEmpty(extras.getString("inauthor")))) {
                searchAuthor = "+inauthor:" + extras.getString("inauthor").replace(" ", "+");
            }
            if (!(TextUtils.isEmpty(extras.getString("category")))) {
                searchCategory = "+category:" + extras.getString("category").replace(" ", "+");
            }
            if (prefMags == prefBooks) {
                searchType = "&printType=all";
            } else {
                if (prefBooks) {
                    searchType = "&printType=books";
                } else {
                    searchType = "&printType=magazines";
                }
            }
            switch (prefResults) {
                case "1":
                    maxResults = "&maxResults=10";
                    break;
                case "2":
                    maxResults = "&maxResults=20";
                    break;
                case "3":
                    maxResults = "&maxResults=30";
                    break;
                case "4":
                    maxResults = "&maxResults=40";
                    break;
                default:
                    maxResults = "&maxResults=10";
                    break;
            }
            fullSearch = searchGeneral + searchTitle + searchAuthor + searchCategory + searchType + maxResults;
            fullSearch = GOOGLE_BOOKS_BASE + fullSearch + MY_API_KEY;
            Log.d(TAG, "FULL SEARCH URL W/ PARAMS: " + fullSearch);
        }
    }

    @Override
    public Loader<List<Books>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: Start");
        // TODO: Create a new loader for the given URL
        // Create a new loader with the return method
        // returning the URL passed in..
        return new BookLoader(this,fullSearch);
    }

    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> data) {
        Log.d(TAG, "onLoadFinished: Start");
        // TODO: Update the UI with the result
        // Similar to the onPostExecute. Basically updates UI
        progressBar.setVisibility(View.INVISIBLE);
        if (data == null) {
            emptyView.setVisibility(VISIBLE);
            return;
        }

// If there is a valid list of quakes, we add that to the data set this will trigger the listView to update.
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        Log.d(TAG, "onLoaderReset: Start");
        mAdapter.clear();
        // TODO: Loader reset, so we can clear out our existing data.
    }



    /*

     Method remains but is no longer used due to creation of the loader.
     To use this method, we need to call the BookAsyncTask .execute(URL HERE)

     The method will then call the QueryUtils, a new class we created.
     QueryUtils separates the code to not flood up the main activity.

     It is also how the Loader will get information on return.

     Due to an AsyncTask getting multiple possible URLs, we use urls[0] to get
     the first URL passed in from the execute method.
        BookAsyncTask bookTask = new BookAsyncTask();
        bookTask.execute(fullSearch);

      */
    private class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Books>> {

        @Override
        protected ArrayList<Books> doInBackground(String... urls) {
            Log.d("BookActivity/ AsyncTask", "doInBackground: START");
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            ArrayList<Books> books = QueryUtils.fetchBookData(urls[0]);
            return (books);
        }

        @Override
        protected void onPostExecute(ArrayList<Books> books) {
            Log.d("BookActivity/ AsyncTask", "onpostExecute: START");
            if (books == null) {
                return;
            }
            ArrayList<Books> book = books;
            BookAdapter adapter2 = new BookAdapter(BookActivity.this, 0, book);
            ListView listView = (ListView) findViewById(R.id.full_list);
            listView.setAdapter(adapter2);
        }
    }
    public void themeChooser(String theme) {
        switch (theme) {
            case "1":
                setTheme(R.style.AppTheme);
                break;
            case "2":
                setTheme(R.style.AppTheme_Earth);
                break;
            case "3":
                setTheme(R.style.AppTheme_3);
                break;

        }
    }
}
