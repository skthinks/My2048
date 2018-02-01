package com.grofers.skthinks.my2048;

import android.app.Activity;
import android.widget.Button;

import com.grofers.skthinks.my2048.View.SplasherActivity;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Skthinks on 24/01/18.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SplashActivityTest {

    @Test
    public void clickingButton_shouldChangeResultsViewText() throws Exception {
        Activity activity = Robolectric.setupActivity(SplasherActivity.class);

        Button button = (Button) activity.findViewById(R.id.btn_recycler_screen);

        Assert.assertThat(button.getText().toString(), Matchers.equalTo("RECYCLER"));
    }

    @Test
    public void TestApiCall() throws Exception {
        SplasherActivity activity = Robolectric.setupActivity(SplasherActivity.class);
        activity.makeApiCall();
    }

    @Test
    public void makeApiCall() throws InterruptedException, IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.alphavantage.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final CountDownLatch latch = new CountDownLatch(1);

        SampleApi request = retrofit.create(SampleApi.class);
        Call<Object> call = request.getMyJSON();
        System.out.print("ABCD");
        Object tasks = call.execute().body();
        System.out.print("TASK : " +  tasks.toString());

        System.out.print("EFGH");
    }
}
