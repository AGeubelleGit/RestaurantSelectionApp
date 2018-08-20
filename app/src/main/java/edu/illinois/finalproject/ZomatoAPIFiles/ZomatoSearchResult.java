package edu.illinois.finalproject.ZomatoAPIFiles;

/**
 * Created by alexandregeubelle on 10/30/17.
 */

public class ZomatoSearchResult {
    private int results_found;
    private int results_start;
    private int results_shown;
    private RestaurantContainer[] restaurants;

    public ZomatoSearchResult() {

    }

    public int getResults_found() {
        return results_found;
    }

    public int getResults_start() {
        return results_start;
    }

    public int getResults_shown() {
        return results_shown;
    }

    public RestaurantContainer[] getRestaurants() {
        return restaurants;
    }

}
