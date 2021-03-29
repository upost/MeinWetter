package de.androidnewcomer.meinwetter;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IpInfoService {

    @GET("geo")
    Call<IpLocation> findMe();
}
