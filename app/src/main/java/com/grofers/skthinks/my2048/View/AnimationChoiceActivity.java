package com.grofers.skthinks.my2048.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.grofers.skthinks.my2048.CommonLibs;
import com.grofers.skthinks.my2048.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Skthinks on 27/10/16.
 */
public class AnimationChoiceActivity extends AppCompatActivity {

    @BindView(R.id.SwaggyDog)
    ImageView imgDoge;


    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.bottom_navigation2)
    BottomNavigationView bottomNavigationView2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_choice);
        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.doge).diskCacheStrategy(DiskCacheStrategy.ALL).into(imgDoge);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.linear_launch:
                        launchAnimation(CommonLibs.AnimationKeys.LINEAR_LAUNCH);
                        break;
                    case R.id.accelerate:
                        launchAnimation(CommonLibs.AnimationKeys.ACCELERATE);
                        break;
                    case R.id.spin:
                        launchAnimation(CommonLibs.AnimationKeys.SPIN);
                        break;
                    case R.id.linear_rotate_launch:
                        launchAnimation(CommonLibs.AnimationKeys.LINEAR_ROTATE_LAUNCH);
                        break;
                    case R.id.background_color_change:
                        launchAnimation(CommonLibs.AnimationKeys.CHANGE_BACKGROUND_COLOR);
                }
                return false;
            }
                                                                 }
        );
        bottomNavigationView2.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.rotate_launch_II:
                                launchAnimation(CommonLibs.AnimationKeys.LINEAR_ROTATE_LAUNCH_II);
                                break;
                            case R.id.fly_with_doge:
                                launchAnimation(CommonLibs.AnimationKeys.FLY_WITH_DOGE);
                                break;
                        }
                        return false;
                    }
                }
        );
    }

    private void launchAnimation(String Action){
        Intent intent = new Intent(AnimationChoiceActivity.this, AnimationActivity.class);
        intent.putExtra(CommonLibs.AnimationKeys.ANIMATION, Action);
        startActivity(intent);

    }

}
