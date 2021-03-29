package de.androidnewcomer.meinwetter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MetaWeatherService {

    @GET("api/location/search/")
    Call<List<LocationResult>> findLocation(@Query("lattlong") String lattlong);

    @GET("api/location/{woeid}/")
    Call<WeatherResult> findWeather(@Path("woeid") int woeid);
}
