package edu.illinois.finalproject;

/**
 * Created by alexandregeubelle on 10/30/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantFilter;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantL3;
import edu.illinois.finalproject.ZomatoAPIFiles.Zomato;
import edu.illinois.finalproject.ZomatoAPIFiles.ZomatoSearchResult;

/**
 * Created by ageubell on 10/30/17.
 * based off of code shown by Proffessor Zilles in CS 126
 */

public class ZomatoAsyncTask extends AsyncTask<String, Integer, ArrayList<RestaurantL3>> {
    public static final String TAG = ZomatoSearchResult.class.getSimpleName();

    private Context context;
    private ZomatoAdapter zomatoRecycleAdapter;
    //The filter is used to only show a subset of restaurants returned by zomato.
    private RestaurantFilter filter;

    public ZomatoAsyncTask(Context context, ZomatoAdapter zomatoRecycleAdapter) {
        this.context = context;
        this.zomatoRecycleAdapter = zomatoRecycleAdapter;
        this.filter = null;
    }

    public ZomatoAsyncTask(Context context, ZomatoAdapter zomatoRecycleAdapter, RestaurantFilter filter) {
        this.context = context;
        this.zomatoRecycleAdapter = zomatoRecycleAdapter;
        this.filter = filter;
    }

    @Override
    protected ArrayList<RestaurantL3> doInBackground(String... params) {
        try {
            //instantiate list of restaurants.
            ArrayList<RestaurantL3> restaurants = new ArrayList<>();

            // For each paramteter url, retrieve the data from zomato and append it to the restaurants list.
            for (String urlString: params) {
                URL url = new URL(urlString);

                // Add api-key to the connection.
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("user-key", Zomato.getAPIKey());

                connection.connect();

                InputStream inStream = connection.getInputStream();
                InputStreamReader inStreamReader = new InputStreamReader(inStream, Charset.forName("UTF-8"));

                // Parse json of restaurant data from Zomato.
                Gson gson = new Gson();
                ZomatoSearchResult restaurantCollection = gson.fromJson(inStreamReader, ZomatoSearchResult.class);
                for (RestaurantContainer restaurant: restaurantCollection.getRestaurants()) {
                    //If there is no filter, or the filter returns true for this restaurant then add it to the list.
                    if (filter == null || filter.filter(restaurant)) {
                        restaurants.add(restaurant.getRestaurant());
                    }
                }
            }

            // return restaurants to post execute.
            return restaurants;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<RestaurantL3> restaurants) {
        if (restaurants == null) {
            return;
        }
        //Add the search results to the data in zomatoAdapter.
        zomatoRecycleAdapter.addZomatoSearchResults(restaurants);
    }
}