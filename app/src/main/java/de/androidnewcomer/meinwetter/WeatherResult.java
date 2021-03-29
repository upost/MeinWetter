package de.androidnewcomer.meinwetter;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

class WeatherResult {
    @SerializedName("consolidated_weather")
    List<ConsolidatedWeather> consolidatedWeatherList;
    Date time,sun_rise,sun_set;
    String timezone_name;
    LocationResult parent;
    //List<WeatherSource> sources;
    LocationResult location;
}
