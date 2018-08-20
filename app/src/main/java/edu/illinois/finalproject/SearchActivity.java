package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by alexandregeubelle on 11/29/17.
 */

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        //Initialize variables of objects in the activity.
        final Spinner cuisinesDropdown = (Spinner) findViewById(R.id.search_cuisine_dropdown);
        final Spinner numberResultsDropdown = (Spinner) findViewById(R.id.search_number_of_results_dropdown);
        final Button searchButton = (Button) findViewById(R.id.search_search_button);
        final EditText nameEditText = (EditText) findViewById(R.id.search_name_edit_text);
        final EditText streetEditText = (EditText) findViewById(R.id.search_street_edit_text);

        //Add in the different types of cuisine to the dropdown.
        //Documentation for drop downs: https://developer.android.com/guide/topics/ui/controls/spinner.html
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> cuisineDropdownAdapter = ArrayAdapter.createFromResource(this,
                R.array.cuisinesTypes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        cuisineDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        cuisinesDropdown.setAdapter(cuisineDropdownAdapter);

        //Add values to show possible number of results that user wants returned (20-100)
        //Documentation for drop downs: https://developer.android.com/guide/topics/ui/controls/spinner.html
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> numberResultsDropdownAdapter = ArrayAdapter.createFromResource(this,
                R.array.resultsIntegers, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        numberResultsDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        numberResultsDropdown.setAdapter(numberResultsDropdownAdapter);

        //When the user presses the search button.
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get values from user inputs.
                String cuisineString = cuisinesDropdown.getSelectedItem().toString();
                int cuisineIndex = cuisinesDropdown.getSelectedItemPosition();
                String name = nameEditText.getText().toString();
                String numberResultsString = numberResultsDropdown.getSelectedItem().toString();
                int numberResultsInt = Integer.parseInt(numberResultsString);
                String streetName = streetEditText.getText().toString();

                //Get instance of GPS tracker class to access user's data.
                GPSTracker gps = new GPSTracker(view.getContext());

                //Get the latitude and longitude.
                double latitude = 0.0;
                double longitude = 0.0;
                if(gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                }else{
                    gps.showSettingsAlert();
                    return;
                }

                //Create intent to pass data to the search results frame.
                Intent searchResultsIntent = new Intent(getApplicationContext(),
                        SearchResultsActivity.class);
                searchResultsIntent.putExtra("CuisineType", cuisineString);
                searchResultsIntent.putExtra("CuisineIndex", cuisineIndex);
                searchResultsIntent.putExtra("Name", name);
                searchResultsIntent.putExtra("Latitude", latitude);
                searchResultsIntent.putExtra("Longitude", longitude);
                searchResultsIntent.putExtra("NumResults", numberResultsInt);
                searchResultsIntent.putExtra("StreetName", streetName);

                //Start search results activity.
                final Context context = view.getContext();
                context.startActivity(searchResultsIntent);

            }
        });

    }
}
