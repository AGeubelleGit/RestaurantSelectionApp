package edu.illinois.finalproject;

/**
 * Created by alexandregeubelle on 10/31/17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.illinois.finalproject.ZomatoAPIFiles.RestaurantL3;

/**
 * Adapter class to control the restaurant recylerview of zomato restaurants.
 */
public class ZomatoAdapter extends RecyclerView.Adapter<ZomatoAdapter.ZomatoViewHolder> {

    private static final String TAG = ZomatoAdapter.class.getSimpleName();

    //Data source of restaurant information.
    private ArrayList<RestaurantL3> restaurants = new ArrayList<>();

    public ZomatoAdapter(ArrayList<RestaurantL3> restaurantResults) {
        restaurants.clear();
        addZomatoSearchResults(restaurantResults);
    }

    @Override
    public ZomatoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(viewType, viewGroup, shouldAttachToParentImmediately);
        ZomatoViewHolder viewHolder = new ZomatoViewHolder(view);

        return viewHolder;
    }

    @Override
    // Return the view type: There are seperate view types for whether or not there is an image.
    public int getItemViewType(int position) {
        RestaurantL3 currRestaurant = restaurants.get(position);

        String currRestaurantFeaturedImageUrl = currRestaurant.getFeatured_image();
        if(currRestaurantFeaturedImageUrl == null || currRestaurantFeaturedImageUrl.length() < 1) {
            return R.layout.zomato_restaurant_cell;
        }else {
            return R.layout.zomato_restaurant_cell_with_image;
        }
    }

    @Override
    // Call bind on the holder at the specfic position.
    public void onBindViewHolder(ZomatoViewHolder holder, int position) {
        RestaurantL3 currRestaurant = restaurants.get(position);
        holder.bind(position, currRestaurant);
    }

    @Override
    public int getItemCount() {
        if(restaurants == null) {
            return 0;
        }
        return restaurants.size();
    }

    public void resetZomatoSearchResults() {
        restaurants.clear();
        this.notifyDataSetChanged();
    }

    //Add new data. Called by asyncTask to add restaurants.
    public void addZomatoSearchResults(ArrayList<RestaurantL3> newResults) {
        if(newResults == null) {
            return;
        }
        restaurants.addAll(newResults);
        this.notifyDataSetChanged();
    }

    //The View Holder class for the Zomato Restaurant recycler view.
    class ZomatoViewHolder extends RecyclerView.ViewHolder {
        //Views that will be changed on binding.
        private View itemView;
        private TextView restaurantNameView;
        private TextView restaurantAddressView;
        private TextView restaurantPriceView;
        private ImageView restaurantImageView;

        public ZomatoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            restaurantNameView = (TextView) itemView.findViewById(R.id.restaunt_name_text);
            restaurantAddressView = (TextView) itemView.findViewById(R.id.restaurant_address_text);
            restaurantPriceView = (TextView) itemView.findViewById(R.id.restaurant_price_range_text);
            restaurantImageView = (ImageView) itemView.findViewById(R.id.restaurant_image_view);
        }

        //Bind values from the restauranat input into the different views.
        void bind(int listIndex, final RestaurantL3 restaurant) {
            //Set name and address texts.
            restaurantNameView.setText(restaurant.getName());
            restaurantAddressView.setText(restaurant.getLocation().getAddress());

            //Set price range string of the cell.
            int priceRange = restaurant.getPrice_range();
            restaurantPriceView.setText(getPriceString(priceRange));

            //Set the image if there is one using Picasso.
            if(restaurant.getFeatured_image() != null && restaurant.getFeatured_image().length() > 0) {
                if(restaurantImageView != null) {
                    Picasso.with(itemView.getContext()).load(restaurant.getFeatured_image()).into(restaurantImageView);
                }
            }

            //Set on click listener for the entire cell.
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final Context context = v.getContext();
                    Intent detailedIntent = new Intent(context, DetailActivity.class);
                    //Start restaurant detail activity with ability to add restaurant to database.
                    detailedIntent.putExtra("restaurant", restaurant);
                    detailedIntent.putExtra("ActionType", RestaurantActionType.ADD);
                    context.startActivity(detailedIntent);
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
    }
}