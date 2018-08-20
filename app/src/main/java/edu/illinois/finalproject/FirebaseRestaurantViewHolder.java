package edu.illinois.finalproject;

/**
 * Created by alexandregeubelle on 11/29/17.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.HashMap;

import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantFirebaseContainer;
import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantL3;

/*
View Holder that is used to show the different restaurants from the database.
 */
public class FirebaseRestaurantViewHolder extends RecyclerView.ViewHolder {

    //Variables of objects of each cell.
    private View itemView;
    private TextView restaurantNameView;
    private TextView restaurantAddressView;
    private TextView restaurantPriceView;
    private ImageView restaurantImageView;
    private ImageButton likeButton;
    private ImageButton dislikeButton;
    private TextView scoreView;

    public FirebaseRestaurantViewHolder(View itemView) {
        super(itemView);

        //Initialize all the variables.
        this.itemView = itemView;
        restaurantNameView = (TextView) itemView.findViewById(R.id.restaunt_name_text);
        restaurantAddressView = (TextView) itemView.findViewById(R.id.restaurant_address_text);
        restaurantPriceView = (TextView) itemView.findViewById(R.id.restaurant_price_range_text);
        restaurantImageView = (ImageView) itemView.findViewById(R.id.restaurant_image_view);
        likeButton = (ImageButton) itemView.findViewById(R.id.firebase_restaurant_cell_like_button);
        dislikeButton = (ImageButton) itemView.findViewById(R.id.firebase_restaurant_cell_dislike_button);
        scoreView = (TextView) itemView.findViewById(R.id.firebase_restaurant_cell_score_text);
    }

    //Bind values from the restaurant input into the different views.
    void bind(int listIndex, final RestaurantFirebaseContainer restaurantContainer) {
        //Get the current restaurant for this cell and set the name text view and address.
        final RestaurantL3 restaurant = restaurantContainer.getRestaurant();
        restaurantNameView.setText(restaurant.getName());
        restaurantAddressView.setText(restaurant.getLocation().getAddress());

        //Set the price range string.
        int priceRange = restaurant.getPrice_range();
        restaurantPriceView.setText(getPriceString(priceRange));

        //Set the featured image if it exists.
        if(restaurant.getFeatured_image() != null && restaurant.getFeatured_image().length() > 0) {
            if(restaurantImageView != null) {
                Picasso.with(itemView.getContext()).load(restaurant.getFeatured_image()).into(restaurantImageView);
            }
        }

        //Set on click listener for the entire cell
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Start detail activity for the current restaurant to display data.
                final Context context = v.getContext();
                Intent detailedIntent = new Intent(context, DetailActivity.class);
                detailedIntent.putExtra("restaurant", restaurant);
                detailedIntent.putExtra("ActionType", RestaurantActionType.REMOVE);
                context.startActivity(detailedIntent);
            }
        });

        //Set up likes and dislikes.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        //Get hash map of likes/dislikes.
        final HashMap<String, String> likes = restaurantContainer.getLikes();
        final HashMap<String, String> dislikes = restaurantContainer.getDislikes();

        //If the current user is in the likes or dislikes set the color of the respective arrow.
        if(likes.containsKey(uid)) {
            setPressedColor(itemView.getContext(), likeButton);
            setDefaultColor(itemView.getContext(), dislikeButton);
        }else if(dislikes.containsKey(uid)) {
            setPressedColor(itemView.getContext(), dislikeButton);
            setDefaultColor(itemView.getContext(), likeButton);
        }else{
            setDefaultColor(itemView.getContext(), likeButton);
            setDefaultColor(itemView.getContext(), dislikeButton);
        }

        //Set the text view to display the likes - dislikes to the user.
        int numLikes = restaurantContainer.getNumLikes();
        int numDislikes = restaurantContainer.getNumDislikes();
        String scoreString = Integer.toString(numLikes - numDislikes);
        scoreView.setText(scoreString);

        //If like is pressed, toggle the like in the database
        likeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String uid = auth.getCurrentUser().getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference rootReference = database.getReference(MainActivity.currId);

                String restaurantId = Integer.toString(restaurant.getId());
                DatabaseReference restaurantReference = rootReference.child(restaurantId);
                if(likes.containsKey(uid)) {
                    restaurantReference.child("likes").child(uid).setValue(null);
                }else {
                    //If user is liking the restaurant, set dislike to null in firebase.
                    restaurantReference.child("likes").child(uid).setValue("true");
                    restaurantReference.child("dislikes").child(uid).setValue(null);
                }
            }
        });


        //If dislike button is pressed, toggle dislike in database.
        dislikeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String uid = auth.getCurrentUser().getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference rootReference = database.getReference(MainActivity.currId);

                String restaurantId = Integer.toString(restaurant.getId());
                DatabaseReference restaurantReference = rootReference.child(restaurantId);
                if(dislikes.containsKey(uid)) {
                    restaurantReference.child("dislikes").child(uid).setValue(null);
                }else {
                    //If user is liking the restaurant, set dislike to null in firebase.
                    restaurantReference.child("dislikes").child(uid).setValue("true");
                    restaurantReference.child("likes").child(uid).setValue(null);
                }
            }
        });

    }

    /**
     * Return the string representing the price range
     * Price range = 4 -> string = $$$$
     * @param priceRange the integer price range 1-5
     * @return the string version of the price range.
     */
    private String getPriceString(int priceRange) {
        String currencySymbol = itemView.getContext().getResources().getString(R.string.currency_symbol);

        StringBuilder output = new StringBuilder();
        for(int iterator = 0; iterator < priceRange; iterator++) {
            output.append(currencySymbol);
        }
        return output.toString();
    }

    /**
     * Set color of button passed to the color to represent pressed.
     */
    private void setPressedColor(Context context, ImageButton button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setColorFilter(context.getResources().getColor(R.color.liked_color, context.getTheme()));
        }else {
            button.setColorFilter(context.getResources().getColor(R.color.liked_color));
        }
    }

    /**
     * Set color of button passed to the color to represent not being pressed.
     */
    private void setDefaultColor(Context context, ImageButton button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setColorFilter(context.getResources().getColor(R.color.default_non_liked, context.getTheme()));
        }else {
            button.setColorFilter(context.getResources().getColor(R.color.default_non_liked));
        }
    }
}