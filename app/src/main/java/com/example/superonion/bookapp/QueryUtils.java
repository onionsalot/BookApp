package com.example.superonion.bookapp;

import android.text.TextUtils;
import android.util.Log;

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



/**
 * Helper methods related to requesting and receiving earthquake data from the API.
 * Used to basically relieve the main activity from being flooded by code.
 * The main activity will still contain the Async task but instead delagate info to here
 * for parsing.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static ArrayList<Books> fetchBookData(String requestUrl) {
        Log.d("QueryUtils.Java", "fetchBookData: STARTING FETCH");
        URL url = createUrl(requestUrl);
        Log.d("QueryUtils.Java", "doInBackground: Current URL " + url);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO handle IO exception
        }

        ArrayList<Books> books = extractFeatureFromJson(jsonResponse);

        return books;
    }

    public static URL createUrl(String googleBooksApi) {
        URL url = null;
        try {
            url = new URL(googleBooksApi);
        } catch (MalformedURLException e) {
            Log.e("Error", "createUrl: Error with creating URL" + e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            // Makes the connection. Everything before this line is to set up the connection.
            urlConnection.connect();

            // Once the connection has been established, we check if the response is valid.
            // Response will be an int and we check if it is a 200 int (connection success).

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            // TODO handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        // Create a StringBuilder. Strings cannot be changed once its created, it can only be completely
        // replaced IE via concatnation. A StringBuilder can be changed after it is created using
        // methods created for the builder. IE .append,.delete. The StringBuilder can then be frozen
        // into a String using the builder.toString() method.

        // Setup Builder
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            // Setup Reader
            BufferedReader reader = new BufferedReader(inputStreamReader);
            // Ask the reader for a line of Text
            String line = reader.readLine();
            // Append result to the end of the String line if the results isn't null. Then move onto the next
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        // Once the reader has run out of lines, it will finalize the string and pass it back to the makeHttpRequest
        return output.toString();
    }


    private static ArrayList<Books> extractFeatureFromJson(String jsonResponse) {
        // If the JSON string is empty or null from the makeHttp method we can use TextUtils to check.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            ArrayList<Books> titleArray = new ArrayList<Books>();
            if (baseJsonResponse.getInt("totalItems") == 0) {
                Log.d("QueryUtils.Java", "extractFeatureFromJson: STOP EVERYTHING THERE IS 0 RETURNS");
                // TODO: Create case for returning 0 items
            } else {
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
                // TODO: show results returned
                // If there are results in the "items" array
                if (itemsArray.length() > 0) {
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject firstItem = itemsArray.getJSONObject(i);
                        JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");

                        // Extract out the title and description values
                        String title;
                        String subtitle;
                        String description;
                        String publishedDate;
                        String authors;
                        String picture;

                        /*
                         * Checks if there is a "name" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (volumeInfo.has("title")) {
                            title = volumeInfo.getString("title");
                        } else {
                            title = "";
                        }
                        /*
                         * Checks if there is a "subtitle" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (volumeInfo.has("subtitle")) {
                            subtitle = volumeInfo.getString("subtitle");
                        } else {
                            subtitle = "";
                        }
                        /*
                         * Checks if there is a "description" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (volumeInfo.has("description")) {
                            description = volumeInfo.getString("description");
                        } else {
                            description = "No description found";
                        }
                        /*
                         * Checks if there is a "publishedDate" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (volumeInfo.has("publishedDate")) {
                            publishedDate = volumeInfo.getString("publishedDate");
                        } else {
                            publishedDate = "Unknown Date";
                        }
                        /*
                         * Checks if there is a "authors" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (volumeInfo.has("authors")) {
                            authors = volumeInfo.getString("authors")
                                    .replace("[", "")
                                    .replace("]", "")
                                    .replace(",","\n");
                        } else {
                            authors = "Unknown Author";
                        }
                        /*
                         * Checks if there is a "imageLinks" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (volumeInfo.has("imageLinks")) {
                            JSONObject images = volumeInfo.getJSONObject("imageLinks");
                            if (images.has("smallThumbnail")) {
                                picture = images.getString("smallThumbnail");
                            } else {
                                picture = "";
                            }
                        } else {
                            picture = "";
                        }


                        titleArray.add(new Books(title, subtitle ,description,publishedDate,authors,picture));
                    }

                    // Create the object and return it
                    return titleArray;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
