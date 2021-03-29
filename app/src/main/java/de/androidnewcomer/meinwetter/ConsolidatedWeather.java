package de.androidnewcomer.meinwetter;

import java.util.Date;

class ConsolidatedWeather {
    long id;
    String weather_state_name,weather_state_abbr,wind_direction_compass;
    Date created,applicable_date;
    float min_temp,max_temp,the_temp,wind_speed,wind_direction,air_pressure,humidity,visibilidy,predictability;
}
