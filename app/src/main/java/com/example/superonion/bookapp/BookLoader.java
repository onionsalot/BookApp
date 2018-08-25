package com.example.superonion.bookapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BookLoader extends AsyncTaskLoader<List<Books>> {
    String mUrl;
    //This is the contructor class.
    // @param context of the activity
    // @param url to load data from
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading: Start");
        forceLoad();
    }

    // This is on a background thread
    // Pass in URL and get the list information
    // Returns the list
    @Override
    public List<Books> loadInBackground() {
        Log.d(TAG, "loadInBackground: start");
        // TODO: This method will be used to parse out data. Similar to asyncTask doinBackground
        if (mUrl == null) {
            return null;
        }
        List<Books> books = QueryUtils.fetchBookData(mUrl);
        return (books);
    }


}
