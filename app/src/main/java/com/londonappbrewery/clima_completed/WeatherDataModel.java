package com.londonappbrewery.clima_completed;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//The weather data model class will be an object that will hold the weather information needed for the app. Takes in the JSONobject from the api, and parses it to get the right info
public class WeatherDataModel {

    // Member variables that hold our relevant weather inforomation.
    private String mTemperature;
    private String mCity;
    private String mIconName;
    private int mCondition;
    private long mSunrise;
    private long mSunset;
    private double mLatitude;
    private double mLongitude;


    // Create a WeatherDataModel from a JSON.
    // We will call this instead of the standard constructor.
    public static WeatherDataModel fromJson(JSONObject jsonObject) {

        try {
            WeatherDataModel weatherData = new WeatherDataModel();

            weatherData.mCity = jsonObject.getString("name");
            weatherData.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mIconName = updateWeatherIcon(weatherData.mCondition);
            //Getting sunrise and sunset utc time to determine day or night
            weatherData.mSunrise = jsonObject.getJSONObject("sys").getLong("sunrise");
            weatherData.mSunset = jsonObject.getJSONObject("sys").getLong("sunset");
            //Getting latitude and longitude data to determine timezone
            weatherData.mLatitude = jsonObject.getJSONObject("coord").getDouble("lat");
            weatherData.mLongitude = jsonObject.getJSONObject("coord").getDouble("lon");

            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundedValue = (int) Math.rint(tempResult);

            weatherData.mTemperature = Integer.toString(roundedValue);


            return weatherData;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get the weather image name from OpenWeatherMap's condition (marked by a number code)
    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    // Getter methods for temperature, city, icon name, latitude, longitude, sunrise, and sunset:

    public String getTemperature() {
        return mTemperature + "°";
    }

    public String getCity() {
        return mCity;
    }

    public String getIconName() {
        return mIconName;
    }

    public double getLatitude(){ return mLatitude;}
    public double getLongitude(){ return mLongitude;}
    public long getSunrise(){return mSunrise;}
    public long getSunset(){return mSunset;}
}
