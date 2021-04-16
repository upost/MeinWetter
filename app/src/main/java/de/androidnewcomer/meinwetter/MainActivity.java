package de.androidnewcomer.meinwetter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MeinWetter";
    private TextView locationTextView;
    private TextView stationsTextView;
    private TextView weatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationTextView = findViewById(R.id.location);
        stationsTextView = findViewById(R.id.nearestLocation);
        weatherTextView = findViewById(R.id.weather);

        findViewById(R.id.frog).setOnClickListener(v->{
            v.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bounce));
            getWeatherInfos();});
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWeatherInfos();
    }

    private void getWeatherInfos() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://ipinfo.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IpInfoService ipInfoService = retrofit.create(IpInfoService.class);
        locationTextView.setText("Abfrage läuft...");
        Call<IpLocation> ipLocationCall = ipInfoService.findMe();

        ipLocationCall.enqueue(new Callback<IpLocation>() {
            @Override
            public void onResponse(Call<IpLocation> call, Response<IpLocation> response) {
                locationTextView.setText( response.body().city + ", " + response.body().loc);
                findStationsNear(response.body().loc);
            }

            @Override
            public void onFailure(Call<IpLocation> call, Throwable t) {
                Log.e(LOG_TAG,"findMe failed",t);
                locationTextView.setText("Fehler: " + t.getLocalizedMessage());
            }
        });
    }

    private void findStationsNear(String lattlong) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.metaweather.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MetaWeatherService metaWeatherService = retrofit.create(MetaWeatherService.class);
        stationsTextView.setText("Abfrage läuft...");
        Call<List<LocationResult>> locationResultCall = metaWeatherService.findLocation(lattlong);
        locationResultCall.enqueue(new Callback<List<LocationResult>>() {
            @Override
            public void onResponse(Call<List<LocationResult>> call, Response<List<LocationResult>> response) {
                String stationen ="";
                int count=0;
                for(LocationResult lr : response.body()) {
                    stationen+= lr.title + " (where on earth id: " + lr.woeid + ")\r\n";
                    if(count++>=4) break;
                }
                stationsTextView.setText(stationen);

                if(response.body().size()==0) {
                    stationsTextView.setText("Keine Wetterstationen in der Nähe gefunden");
                } else {
                    findWeatherAt(response.body().get(0).woeid);
                }

            }

            @Override
            public void onFailure(Call<List<LocationResult>> call, Throwable t) {
                Log.e(LOG_TAG,"findLocation failed",t);
                stationsTextView.setText("Fehler: " + t.getLocalizedMessage());
            }
        });
    }

    private void findWeatherAt(int woeid) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.metaweather.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MetaWeatherService metaWeatherService = retrofit.create(MetaWeatherService.class);
        weatherTextView.setText("Abfrage läuft...");
        Call<WeatherResult> weatherResultCall = metaWeatherService.findWeather(woeid);
        weatherResultCall.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                String result ="";
                DateFormat sdf = SimpleDateFormat.getDateInstance();
                for(ConsolidatedWeather cw : response.body().consolidatedWeatherList) {
                    result +=  sdf.format(cw.applicable_date) + ": " + cw.weather_state_name + ", " + cw.the_temp + "°C\r\n";
                }
                weatherTextView.setText(result);
            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                Log.e(LOG_TAG,"find weather failed",t);
                weatherTextView.setText("Fehler: " + t.getLocalizedMessage());
            }
        });
    }
}
