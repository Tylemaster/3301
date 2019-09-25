package com.londonappbrewery.clima_completed;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
//This code is attached to the change city controller view.
public class ChangeCityController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);

        //The three forms of input on this page are the EditText for inputting a city, the EditText for inputting the lat/long, and a button for getting the gps lat/long(also a back button)
        final EditText editTextField = findViewById(R.id.queryET);
        final EditText editGPSField = findViewById(R.id.latLongQuery);
        Button GPSCoord = findViewById(R.id.button);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back and destroy the ChangeCityController
                finish();
            }
        });

        //When the GPS button is pressed, a new intent is made with the extra "GPS" value, so that when OnActivityResult is called in WeatherController.java
        //We can check the extra for this value, and if its not null, the api will be called with the phones gps coordinates
        GPSCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newCityIntent = new Intent(ChangeCityController.this, WeatherController.class);
                newCityIntent.putExtra("GPS", "true");
                setResult(Activity.RESULT_OK, newCityIntent);
                finish();
            }
        });



        //When text is entered and enter is pressed on the editTextField, a new intent is made with an extra value, city name.
        // when OnActivityResult is called from WeatherController.java, if the city name value is not null, then the api will be called with this new city
        //name as a parameter
        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String newCity = editTextField.getText().toString();
                Intent newCityIntent = new Intent(ChangeCityController.this, WeatherController.class);

                // Adds what was entered in the EditText as an extra to the intent.
                newCityIntent.putExtra("City", newCity);


                // We started this activity for a result, so now we are setting the result.
                setResult(Activity.RESULT_OK, newCityIntent);

                // This destroys the ChangeCityController.
                finish();
                return true;
            }
        });

        //When text is entered and enter is pressed on the editGPSField, a new intent is made with two extra values, lat and long.
        // when OnActivityResult is called from WeatherController.java, if the lat value is not null, then the api will be called with these coordinates
        //name as a parameter
        editGPSField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String newlatLong = editGPSField.getText().toString();
                String[] latLongArray = newlatLong.split(",");
                //making sure that two values are passed in
                if(latLongArray.length > 1){
                    String lat = latLongArray[0];
                    String longi = latLongArray[1];

                    Intent newCityIntent = new Intent(ChangeCityController.this, WeatherController.class);
                    // Adds what was entered in the EditText as an extra to the intent.
                    newCityIntent.putExtra("lat", lat);
                    newCityIntent.putExtra("lon", longi);

                    // We started this activity for a result, so now we are setting the result.
                    setResult(Activity.RESULT_OK, newCityIntent);
                }
                //if two values are not passed in seperated by a comma, error message is shown
                else{
                    Toast.makeText(ChangeCityController.this, "Invalid Coordinates, try again", Toast.LENGTH_SHORT).show();
                }



                // This destroys the ChangeCityController.
                finish();
                return true;

            }
        });

    }
}
