package edu.illinois.finalproject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantFilter;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantL3;
import edu.illinois.finalproject.ZomatoAPIFiles.ZomatoSearchResult;

/**
 * Created by alexandregeubelle on 11/30/17.
 */

public class SearchResultsActivity extends AppCompatActivity {

    private ZomatoAdapter mAdapter;
    private RecyclerView mRestaurantList;
    private final int resultsPerPage = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_activity);

        //Get the intent that started this activity
        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //Get data values from the intent.
        int cuisineIndex = extras.getInt("CuisineIndex");
        String cuisineType = extras.getString("CuisineType");
        String searchName = extras.getString("Name");
        double latitude = extras.getDouble("Latitude");
        double longitude = extras.getDouble("Longitude");
        int numResults = extras.getInt("NumResults");
        String searchStreet = extras.getString("StreetName");

        //Use an array in resources to get the Zomato cuisine codes needed to search by cuisine.
        Resources resources = getResources();
        int[] cuisineCodesArray = resources.getIntArray(R.array.cuisineTypesIntegers);
        int cuisineCode = cuisineCodesArray[cuisineIndex];

        //The recycler view object that will display the results.
        mRestaurantList = (RecyclerView) findViewById(R.id.search_results_rv);

        //The layout manager that controls the layout view.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRestaurantList.setLayoutManager(layoutManager);

        mAdapter = new ZomatoAdapter(null);
        mRestaurantList.setAdapter(mAdapter);

        //Each Zomato 'page' is 20 restaurants.
        //for each page, create a url that will get the data for that page.
        int numPages = numResults / resultsPerPage;
        String[] urls = new String[numPages];
        for (int page = 0; page < numPages; page++) {
            urls[page] = createZomatoAPISearchUrl(latitude, longitude, cuisineCode, searchName, searchStreet, page);
        }

        //Create a filter to only show restaurants on the input street.
        RestaurantFilter filter = createFilter(searchStreet);
        ZomatoAsyncTask zomatoAsyncTask = new ZomatoAsyncTask(this, mAdapter, filter);
        zomatoAsyncTask.execute(urls);
    }

    /**
     * Create a lambda function of the type "restaurantFilter" that will filter zomato restaurant
     * results.
     * It creates a function that takes in a restaurant container and returns true if the street name
     * is a part of the restaurant's address.
     *
     * @param streetName the name of the street input by user
     * @return a new filter function that will
     */
    private RestaurantFilter createFilter(final String streetName) {
        if (streetName == null || streetName.trim().length() <= 0) {
            return null;
        } else {
            //Creates a new function that returns whether the restaurant's address contains the streetName.
            RestaurantFilter newFilter = new RestaurantFilter() {
                @Override
                public boolean filter(RestaurantContainer restaurantContainer) {
                    RestaurantL3 restaurant = restaurantContainer.getRestaurant();
                    String address = restaurant.getLocation().getAddress();
                    String lowerCaseStreetName = streetName.toLowerCase();
                    address = address.toLowerCase();
                    return address.contains(lowerCaseStreetName);
                }
            };
            return newFilter;
        }
    }

    /**
     * Returns the url that retrieves the data from zomato
     */
    //https://developers.zomato.com/api/v2.1/search?q=Panda&lat=40.1062636&lon=-88.2319826
    private String createZomatoAPISearchUrl(double latitude, double longitude,
                                            int cuisineCode, String nameQuery, String streetNameQuery, int pageNumber) {
        final String searchAPIAddition = "search?";
        final String baseAPIUrl = "https://developers.zomato.com/api/v2.1/";

        String ZomatoSearchUrlString = baseAPIUrl + searchAPIAddition;

        if (nameQuery != null && nameQuery.trim().length() > 0) {
            try {
                String encodedQuery = URLEncoder.encode(nameQuery, "UTF-8");
                ZomatoSearchUrlString = ZomatoSearchUrlString
                        + "&q=" + encodedQuery;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            //If there is no restaurant name input, but there is a street name,
            // then have the query be the street name. This makes more of the results be from this street.
            if (streetNameQuery != null && streetNameQuery.trim().length() > 0) {
                try {
                    String encodedQuery = URLEncoder.encode(streetNameQuery, "UTF-8");
                    ZomatoSearchUrlString = ZomatoSearchUrlString
                            + "&q=" + encodedQuery;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        if (pageNumber != -1) {
            ZomatoSearchUrlString = ZomatoSearchUrlString
                    + "&start=" + Integer.toString(pageNumber * resultsPerPage);
        }

        if (latitude != 0.0 && longitude != 0.0) {
            ZomatoSearchUrlString = ZomatoSearchUrlString
                    + "&lat=" + Double.toString(latitude) + "&lon=" + Double.toString(longitude);
        }

        if (cuisineCode != -1) {
            ZomatoSearchUrlString = ZomatoSearchUrlString
                    + "&cuisines=" + cuisineCode;
        }

        return ZomatoSearchUrlString;
    }

}
