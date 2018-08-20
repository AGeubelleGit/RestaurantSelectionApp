package edu.illinois.finalproject.ZomatoAPIFiles;

/**
 * Created by alexandregeubelle on 12/7/17.
 */
//Application of functional interface from: http://www.informit.com/articles/article.aspx?p=2191423&seqNum=2
public interface RestaurantFilter {
    public abstract boolean filter(RestaurantContainer restaurantContainer);
}
