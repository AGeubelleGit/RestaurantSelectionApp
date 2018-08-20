package edu.illinois.finalproject.ZomatoAPIFiles;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexandregeubelle on 12/1/17.
 */

/**
 * The firebase restaurant container holds a restaurant and also the likes and dislikes for the current restaurant.
 */
public class RestaurantFirebaseContainer {
    private RestaurantL3 restaurant;
    private HashMap<String, String> likes;
    private HashMap<String, String> dislikes;

    public RestaurantFirebaseContainer() {
        restaurant = new RestaurantL3();
        likes = new HashMap<String, String>();
        dislikes = new HashMap<String, String>();
    }

    public RestaurantFirebaseContainer(RestaurantL3 restaurant) {
        this.restaurant = restaurant;
        likes = new HashMap<String, String>();
        dislikes = new HashMap<String, String>();
    }

    public RestaurantL3 getRestaurant() {
        return restaurant;
    }

    public int getNumLikes() {
        if(likes == null) {
            return 0;
        }
        return likes.size();
    }

    public int getNumDislikes() {
        if(dislikes == null) {
            return 0;
        }
        return dislikes.size();
    }

    public HashMap<String, String> getLikes() {
        return likes;
    }

    public HashMap<String, String> getDislikes() {
        return dislikes;
    }

    public void like (String name) {
        likes.put(name, "true");
    }

    public void dislike (String name) {
        dislikes.put(name, "true");
    }
}
