package com.londonappbrewery.clima_completed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;


//The weather controller class is where most of the work is doe in the application. It is attached to the weather_controller_layout view.
public class WeatherController extends AppCompatActivity {

    // Request Codes:
    final int REQUEST_CODE = 123; // Request Code for permission request callback
    final int NEW_CITY_CODE = 456; // Request code for starting new activity for result callback

    // Base URL for the OpenWeatherMap API.
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    //Base URL for the GeoNames API (used for getting timezone)
    final String TIME_ZONE_URL = "http://api.geonames.org/timezoneJSON";

    // App ID to use OpenWeather data
    final String APP_ID = "7c6c1b3ac8b52a614754cbb357f2b650";
    //username to use geoname api
    final String username = "tylemaster";

    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;

    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // Don't want to type 'Clima' in all the logs, so putting this in a constant here.
    final String LOGCAT_TAG = "Clima";

    // Set LOCATION_PROVIDER here. Using GPS_Provider for Fine Location (good for emulator):
    // Recommend using LocationManager.NETWORK_PROVIDER on physical devices (reliable & fast!)
    final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Member Variables.
    boolean mUseLocation = true;
    //the which view is a variable that determines which of the four views in the layout will be changed the next time update ui is called. If 0, all 4 views are changed
    int whichView = 0;
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    TextView mCityLabel2;
    ImageView mWeatherImage2;
    TextView mTemperatureLabel2;
    TextView mCityLabel3;
    ImageView mWeatherImage3;
    TextView mTemperatureLabel3;
    TextView mCityLabel4;
    ImageView mWeatherImage4;
    TextView mTemperatureLabel4;

    ImageView mDayNight;
    ImageView mDayNight2;
    ImageView mDayNight3;
    ImageView mDayNight4;

    TextClock mTextClock;
    TextClock mTextClock2;
    TextClock mTextClock3;
    TextClock mTextClock4;
    //the homeLat and homeLon variables will be set when the gps location is acquired, used for calling current gps data even if location has not changed
    String homeLat;
    String homeLon;


    // Declaring a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    Button mHelpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        //setting member variables to objects in layout
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);
        mTextClock = findViewById((R.id.timeTV));
        mDayNight = findViewById(R.id.dayNightView);

        mCityLabel2 = findViewById(R.id.locationTV2);
        mWeatherImage2 = findViewById(R.id.weatherSymbolIV2);
        mTemperatureLabel2 = findViewById(R.id.tempTV2);
        ImageButton changeCityButton2 = findViewById(R.id.changeCityButton2);
        mTextClock2 = findViewById((R.id.timeTV2));
        mDayNight2 = findViewById(R.id.dayNightView2);

        mCityLabel3 = findViewById(R.id.locationTV3);
        mWeatherImage3 = findViewById(R.id.weatherSymbolIV3);
        mTemperatureLabel3 = findViewById(R.id.tempTV3);
        ImageButton changeCityButton3 = findViewById(R.id.changeCityButton3);
        mTextClock3 = findViewById((R.id.timeTV3));
        mDayNight3 = findViewById(R.id.dayNightView3);

        mCityLabel4 = findViewById(R.id.locationTV4);
        mWeatherImage4 = findViewById(R.id.weatherSymbolIV4);
        mTemperatureLabel4 = findViewById(R.id.tempTV4);
        ImageButton changeCityButton4 = findViewById(R.id.changeCityButton4);
        mTextClock4 = findViewById((R.id.timeTV4));
        mDayNight4 = findViewById(R.id.dayNightView4);
        //declaring the button that will open the help page, my UX improvemnt
        mHelpButton = findViewById(R.id.button3);

        //when the help button is pressed, a new intent is made for the help layout.
        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController.this, Help.class);
                startActivityForResult(myIntent, NEW_CITY_CODE);
            }
        });

        //there are 4 click listeners for the 4 change city buttons. Each one opens a weather controller intent, and calls startActivity method for that intent.
        //each one also changes the value of the whichView int to determine which view is eventually changed when update ui is called.

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichView = 1;
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);

                // Using startActivityForResult since we just get back the city name.
                // Providing an arbitrary request code to check against later.
                startActivityForResult(myIntent, NEW_CITY_CODE);
            }
        });

        changeCityButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                whichView = 2;
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivityForResult(myIntent, NEW_CITY_CODE);
            }
        });

        changeCityButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                whichView = 3;
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivityForResult(myIntent, NEW_CITY_CODE);
            }
        });

        changeCityButton4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                whichView = 4;
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivityForResult(myIntent, NEW_CITY_CODE);
            }
        });
    }


    // onResume() life cyle callback:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOGCAT_TAG, "onResume() called");
        //if the location hasn't been gotten from the gps on startup yet, do that now
        if(mUseLocation) getWeatherForCurrentLocation();
    }

    // Callback received when a new city name is entered on the second screen.
    // Checking request code and if result is OK before making the API call.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOGCAT_TAG, "onActivityResult() called");

        if (requestCode == NEW_CITY_CODE) {
            if (resultCode == RESULT_OK) {
                String city = data.getStringExtra("City");
                String lat = data.getStringExtra("lat");
                String lon = data.getStringExtra("lon");
                Log.d(LOGCAT_TAG, "New city is " + city);

                //checking the extras that were passed into the intent data. If there is a city extra, then we call the method to get api data with a city name
                //if a latitude was passed in, we call a method to get api data from th lat and long. If nether of those were passed in, then the api call
                //is done with the homelat and homelong coordinates
                mUseLocation = false;
                if(city != null){
                    getWeatherForNewCity(city);
                }
                else if(lat != null){
                    getWeatherForNewCoord(lat, lon);
                }
                else{
                    getWeatherForCurrentLocation();
                    RequestParams params = new RequestParams();
                    params.put("lat", homeLat);
                    params.put("lon", homeLon);
                    params.put("appid", APP_ID);
                    letsDoSomeNetworking(params);
                }
            }
        }
    }

    // Configuring the parameters when a new city has been entered:
    private void getWeatherForNewCity(String city) {
        Log.d(LOGCAT_TAG, "Getting weather for new city");
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);

        letsDoSomeNetworking(params);
    }
    //Configuring parameters when a latitude and longitude has been entered
    private void getWeatherForNewCoord(String lat, String lon){
        Log.d(LOGCAT_TAG, "Getting weather for new city");
        RequestParams params = new RequestParams();
        params.put("lat", lat);
        params.put("lon", lon);
        params.put("appid", APP_ID);
        letsDoSomeNetworking(params);
    }


    // Location Listener callbacks here, when the location has changed.
    private void getWeatherForCurrentLocation() {

        Log.d(LOGCAT_TAG, "Getting weather for current location");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d(LOGCAT_TAG, "onLocationChanged() callback received");
                //setting homelat and homelong when location changes. These values will be used when the get weather from gps button is pressed
                homeLon = String.valueOf(location.getLongitude());
                homeLat = String.valueOf(location.getLatitude());

                Log.d(LOGCAT_TAG, "longitude is: " + homeLon);
                Log.d(LOGCAT_TAG, "latitude is: " + homeLat);

                RequestParams params = new RequestParams();
                params.put("lat", homeLat);
                params.put("lon", homeLon);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Log statements to help you debug your app.
                Log.d(LOGCAT_TAG, "onStatusChanged() callback received. Status: " + status);
                Log.d(LOGCAT_TAG, "2 means AVAILABLE, 1: TEMPORARILY_UNAVAILABLE, 0: OUT_OF_SERVICE");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(LOGCAT_TAG, "onProviderEnabled() callback received. Provider: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(LOGCAT_TAG, "onProviderDisabled() callback received. Provider: " + provider);
            }
        };

        // This is the permission check to access (fine) location.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        // Some additional log statements to help you debug
        Log.d(LOGCAT_TAG, "Location Provider used: "
                + mLocationManager.getProvider(LOCATION_PROVIDER).getName());
        Log.d(LOGCAT_TAG, "Location Provider is enabled: "
                + mLocationManager.isProviderEnabled(LOCATION_PROVIDER));
        Log.d(LOGCAT_TAG, "Last known location (if any): "
                + mLocationManager.getLastKnownLocation(LOCATION_PROVIDER));
        Log.d(LOGCAT_TAG, "Requesting location updates");




        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    // This is the callback that's received when the permission is granted (or denied)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checking against the request code we specified earlier.
        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGCAT_TAG, "onRequestPermissionsResult(): Permission granted!");

                // Getting weather only if we were granted permission.
                getWeatherForCurrentLocation();
            } else {
                Log.d(LOGCAT_TAG, "Permission denied =( ");
            }
        }

    }

    //The timezone method will take in parameters that include latitude and longitude, send the dat to the geoName api, and if successful, parse the JSON object
    //received for the timezone at those coordinates (as a string), and passes this string to a method to update the UI clock object
    private void letsGetTimezoned(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(TIME_ZONE_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(LOGCAT_TAG, " Time Success! JSON: " + response.toString());
                try {
                    String timezone = response.getString("timezoneId");
                    updateTimeUi(timezone);
                    Log.d(LOGCAT_TAG, " Time zone inner: " + timezone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //on fail a toast message is printed saying the local time is shown instead

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {

                Log.e(LOGCAT_TAG, "Time Fail " + e.toString());
                Toast.makeText(WeatherController.this, "Time Zone Failed, Showing Local Time", Toast.LENGTH_SHORT).show();

                Log.d(LOGCAT_TAG, "Status code " + statusCode);
                Log.d(LOGCAT_TAG, "Here's what we got instead " + response.toString());
            }

        });

    }


    // This is the actual networking code. Parameters are already configured.
    private void letsDoSomeNetworking(RequestParams params) {

        // AsyncHttpClient belongs to the loopj dependency.
        AsyncHttpClient client = new AsyncHttpClient();

        // Making an HTTP GET request by providing a URL and the parameters.
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(LOGCAT_TAG, "Success! JSON: " + response.toString());
                WeatherDataModel weatherData;
                weatherData = WeatherDataModel.fromJson(response);
                RequestParams params = new RequestParams();
                //the lat and long gotten from the weather api are passed to a method to use another api to find the timezone
                params.put("lat", weatherData.getLatitude());
                params.put("lng", weatherData.getLongitude());
                params.put("username", username);
                letsGetTimezoned(params);
                //ui is updated with the weather model
                updateUI(weatherData);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {

                Log.e(LOGCAT_TAG, "Fail " + e.toString());
                Toast.makeText(WeatherController.this, "Request Failed", Toast.LENGTH_SHORT).show();

                Log.d(LOGCAT_TAG, "Status code " + statusCode);
                Log.d(LOGCAT_TAG, "Here's what we got instead " + response.toString());
            }

        });
    }



    // Updates the information shown on screen. depending on the value of the whichView int, one of the four views is updated
    //at the start of program (whichview = 0) all views are updated
    private void updateUI(WeatherDataModel weather){
        if(whichView == 4){
            updateUI4(weather);
        }
        else if(whichView == 3){
            updateUI3(weather);
        }
        else if(whichView == 2){
            updateUI2(weather);
        }
        else if(whichView == 1){
            updateUI1(weather);
        }
        else{
            updateUI1(weather);
            updateUI2(weather);
            updateUI3(weather);
            updateUI4(weather);
        }
    }

    //method that gets a weather model, and compares the current time vs the sunrise and sunset times from the api (all in unix utc) to determine whether it is day or night
    // returns true for day, false for night
    private boolean dayOrNight(WeatherDataModel weather){
        boolean day;
        long sunriseDate = weather.getSunrise() * 1000;
        Log.d(LOGCAT_TAG, "rise " + sunriseDate);
        long sunsetDate = weather.getSunset() * 1000;
        Log.d(LOGCAT_TAG, "set" + sunsetDate);
        long nowsTime = System.currentTimeMillis();
        Log.d(LOGCAT_TAG, "now " + nowsTime);
        if(nowsTime >= sunriseDate && nowsTime < sunsetDate){
            day = true;
        }
        else{
            day = false;
        }
        return day;

    }

    //all 4 updateUI# methods act the same. updates the temp, city, weather icon, and day or night icon to match the weather model passed in, one method for each of the views in the layout
    private void updateUI1(WeatherDataModel weather){
        int dayNightresourceID;

        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());

        // Update the icon based on the resource id of the image in the drawable folder.
        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);

        //calls the dayNight method on the weather model to determine if it is day or night, and changes the dayNight icon accordingly
        boolean dayNight = dayOrNight(weather);
        if(dayNight){
            dayNightresourceID = getResources().getIdentifier("day","drawable", getPackageName());
        }
        else{
            dayNightresourceID = getResources().getIdentifier("night","drawable", getPackageName());
        }

        mDayNight.setImageResource(dayNightresourceID);
    }
    //see updateUI1
    private void updateUI2(WeatherDataModel weather){
        int dayNightresourceID;
        mTemperatureLabel2.setText(weather.getTemperature());
        mCityLabel2.setText(weather.getCity());

        // Update the icon based on the resource id of the image in the drawable folder.
        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        mWeatherImage2.setImageResource(resourceID);

        boolean dayNight = dayOrNight(weather);
        if(dayNight){
            dayNightresourceID = getResources().getIdentifier("day","drawable", getPackageName());
        }
        else{
            dayNightresourceID = getResources().getIdentifier("night","drawable", getPackageName());
        }
        mDayNight2.setImageResource(dayNightresourceID);
    }
    //see updateUI1
    private void updateUI3(WeatherDataModel weather){
        int dayNightresourceID;
        mTemperatureLabel3.setText(weather.getTemperature());
        mCityLabel3.setText(weather.getCity());

        // Update the icon based on the resource id of the image in the drawable folder.
        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        mWeatherImage3.setImageResource(resourceID);

        boolean dayNight = dayOrNight(weather);
        if(dayNight){
            dayNightresourceID = getResources().getIdentifier("day","drawable", getPackageName());
        }
        else{
            dayNightresourceID = getResources().getIdentifier("night","drawable", getPackageName());
        }
        mDayNight3.setImageResource(dayNightresourceID);
    }
    //see updateUI1
    private void updateUI4(WeatherDataModel weather){
        int dayNightresourceID;
        mTemperatureLabel4.setText(weather.getTemperature());
        mCityLabel4.setText(weather.getCity());

        // Update the icon based on the resource id of the image in the drawable folder.
        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        mWeatherImage4.setImageResource(resourceID);

        boolean dayNight = dayOrNight(weather);
        if(dayNight){
            dayNightresourceID = getResources().getIdentifier("day","drawable", getPackageName());
        }
        else{
            dayNightresourceID = getResources().getIdentifier("night","drawable", getPackageName());
        }
        mDayNight4.setImageResource(dayNightresourceID);
    }

    //updateTimeUI functions similarly to the updateUI method, calling on one of the 4 updateTimeUI# methods depending on the value of shichView to update a different view, and updating all
    // 4 if the app is just starting
    private void updateTimeUi(String timeZone){
        if(whichView == 4){
            updateTimeUi4(timeZone);
        }
        else if(whichView == 3){
            updateTimeUi3(timeZone);
        }
        else if(whichView == 2){
            updateTimeUi2(timeZone);
        }
        else if(whichView == 1){
            updateTimeUi1(timeZone);
        }
        else{
            updateTimeUi1(timeZone);
            updateTimeUi2(timeZone);
            updateTimeUi3(timeZone);
            updateTimeUi4(timeZone);
        }
    }
    //sets the timeZone of the textclock object in this view to the one acquired from the geoname api. One method for each of the four views
    private void updateTimeUi1(String timeZone) {
        mTextClock.setTimeZone(timeZone);
    }
    private void updateTimeUi2(String timeZone){
        mTextClock2.setTimeZone(timeZone);
    }
    private void updateTimeUi3(String timeZone){
        mTextClock3.setTimeZone(timeZone);
    }
    private void updateTimeUi4(String timeZone){
        mTextClock4.setTimeZone(timeZone);
    }

    // Freeing up resources when the app enters the paused state.
    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationManager != null) mLocationManager.removeUpdates(mLocationListener);
    }

}











