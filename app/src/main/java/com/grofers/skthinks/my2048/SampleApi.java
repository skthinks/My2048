package com.grofers.skthinks.my2048;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by NitinMehta on 17/01/2018.
 */

public interface SampleApi {

    @GET ("query?function=TIME_SERIES_INTRADAY&symbol=GOOG&interval=15min&outputsize=compact&apikey=GJ203WSCWIQP2XCT")
    Call<Object> getMyJSON();
}