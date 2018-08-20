package edu.illinois.finalproject.ZomatoAPIFiles;

/**
 * Created by alexandregeubelle on 10/30/17.
 */

public class RestaurantContainer {
    private RestaurantL3 restaurant;

    public RestaurantContainer() {
        restaurant = new RestaurantL3();
    }

    public RestaurantContainer(RestaurantL3 restaurant) {
        this.restaurant = restaurant;
    }

    public RestaurantL3 getRestaurant() {
        return restaurant;
    }
}
