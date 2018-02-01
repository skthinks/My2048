package com.grofers.skthinks.my2048.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.grofers.skthinks.my2048.Interfaces.ITwoButtonDialogListener;
import com.grofers.skthinks.my2048.R;
import com.grofers.skthinks.my2048.SampleApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Skthinks on 21/10/16.
 */

public class SplasherActivity extends AppCompatActivity implements ITwoButtonDialogListener {

    @BindView(R.id.btn_startgame)
    Button startGame;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.btn_startdialog)
    Button openDialog;

    @BindView(R.id.btn_animation_screen)
    Button animationScreen;

    @BindView(R.id.btn_recycler_screen)
    Button recyclerScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.intro);
        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_favorites:
                                int cx = startGame.getWidth() / 2;
                                int cy = startGame.getHeight() / 2;
                                float finalRadius = (float) Math.hypot(cx, cy);
                                //Animator anim = ViewAnimationUtils.createCircularReveal(startGame, cx, cy, 0, finalRadius);
                                startGame.setVisibility(View.VISIBLE);
                                //anim.start();
                                break;
                            case R.id.action_music:
                                break;
                            case R.id.action_schedules:
                                int cX = startGame.getWidth() / 2;
                                int cY = startGame.getHeight() / 2;
                                float initialRadius = (float) Math.hypot(cX, cY);
                                //Animator animator = ViewAnimationUtils.createCircularReveal(startGame, cX, cY, initialRadius, 0);

// make the view invisible when the animation is done
                                /*animator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        startGame.setVisibility(View.INVISIBLE);
                                    }
                                });

// start the animation
                                animator.start();*/
                                startGame.setVisibility(View.INVISIBLE);
                                break;
                        }
                        return false;
                    }
                }
        );
    }


    @OnClick({R.id.btn_animation_screen, R.id.btn_startgame, R.id.btn_startdialog, R.id.btn_recycler_screen})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_animation_screen:
                animationTest();
                break;
            case R.id.btn_startdialog:
                openRandomDialog();
                break;
            case R.id.btn_startgame:
                startGame();
                break;
            case R.id.btn_recycler_screen:
                goToHighscores();
                break;
        }
    }

    private void goToHighscores() {
        Intent i = new Intent(this, HighScoresActivity.class);
        startActivity(i);
    }

    public void startGame(){
        //getWindow().setExitTransition(new Explode());
        Intent i = new Intent(this, MainActivity.class);
        //startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        startActivity(i);
    }

    public void animationTest(){
        Intent i = new Intent(this, AnimationChoiceActivity.class);
        startActivity(i);

    }

    public void openRandomDialog(){
        DialogFragment dialogFragment = DialogFrag.newInstance(
                0,
                null,
                "",
                "",
                "",
                ""
                );

        //dialogFragment.setS(this, 0);
        dialogFragment.show(getSupportFragmentManager(), "StockOutDialog");
    }



    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickYes(Bundle bundle) {
        showToast("Yes");

    }

    @Override
    public void onClickNo() {
        showToast("No");
    }

    public void makeApiCall() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.alphavantage.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SampleApi request = retrofit.create(SampleApi.class);
        Call<Object> call = request.getMyJSON();
        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Object jsonResponse = response.body();
                System.out.print("AFASJDASJDSA");
            }


            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                System.out.print("PAKSFPAKSFAS");
            }
        });
    }
}
