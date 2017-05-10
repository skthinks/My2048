package com.grofers.skthinks.my2048.View;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.grofers.skthinks.my2048.CommonLibs;
import com.grofers.skthinks.my2048.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Skthinks on 27/10/16.
 */
public class AnimationActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;


    @BindView(R.id.img_rocket)
    ImageView imgrocket;

    @BindView(R.id.img_doge)
    ImageView imgdoge;

    @BindView(R.id.container)
    FrameLayout container;

    String animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launchpad);
        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.rocket).diskCacheStrategy(DiskCacheStrategy.ALL).into(imgrocket);
        Glide.with(this).load(R.drawable.doge).diskCacheStrategy(DiskCacheStrategy.ALL).into(imgdoge);
        animation = getIntent().getExtras().get(CommonLibs.AnimationKeys.ANIMATION).toString();

    }

    @OnClick(R.id.container)
    public void done(){
        onStartAnimation();
    }

    private void onStartAnimation() {
        switch (animation){
            case CommonLibs.AnimationKeys.LINEAR_LAUNCH:
                linearLaunch();
                break;
            case CommonLibs.AnimationKeys.SPIN:
                spin();
                break;
            case CommonLibs.AnimationKeys.ACCELERATE:
                accelerate();
                break;
            case CommonLibs.AnimationKeys.LINEAR_ROTATE_LAUNCH:
                linearRotateLaunch();
                break;
            case CommonLibs.AnimationKeys.CHANGE_BACKGROUND_COLOR:
                changeBackgroundColor();
                break;
            case CommonLibs.AnimationKeys.LINEAR_ROTATE_LAUNCH_II:
                linearRotateLaunchAgain();
                break;
            case CommonLibs.AnimationKeys.FLY_WITH_DOGE:
                flyWithDoge();
                break;
            default:
                Toast.makeText(this, "OMG! Ye Mera India", Toast.LENGTH_LONG);
        }
    }

    private void flyWithDoge() {
        ValueAnimator positionAnimator = ValueAnimator.ofFloat(0, -2000);
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                imgrocket.setTranslationY(value);
                imgdoge.setTranslationY(value);
            }
        });

        ValueAnimator rotationAnimator = ValueAnimator.ofFloat(0, 360);
        rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                imgdoge.setRotation(value);
            }
        });

//3
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(positionAnimator).with(rotationAnimator);
        animatorSet.setDuration(2000);
        animatorSet.start();
    }

    private void linearRotateLaunchAgain() {
        imgrocket.animate().translationY(-2000)
                .rotationBy(360f)
                .setDuration(500)
                .start();
    }

    private void changeBackgroundColor() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(container, "backgroundColor",
                new ArgbEvaluator(),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.white));

        objectAnimator.setRepeatCount(1);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setDuration(500);
        objectAnimator.start();

    }

    private void linearRotateLaunch() {
        ValueAnimator positionAnimator = ValueAnimator.ofFloat(0, -2000);

        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                imgrocket.setTranslationY(value);
            }
        });

// 3
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(imgrocket, "rotation", 0, 360f);
// 4
        AnimatorSet animatorSet = new AnimatorSet();
// 5
        animatorSet.play(positionAnimator).with(rotationAnimator);
// 6
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private void linearLaunch() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, -2000);

//2
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //3
                float value = (float) animation.getAnimatedValue();
                //4
                imgrocket.setTranslationY(value);
            }
        });

//5
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(500);
//6
        valueAnimator.start();
    }

    private void spin(){
        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                // 2
                imgrocket.setRotation(value);
            }
        });

        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(500);
        animator.start();
    }

    // 1
    private void accelerate() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, -2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                imgrocket.setTranslationY(value);
            }
        });

        // 2 - Here set your favorite interpolator
        valueAnimator.setInterpolator(new AccelerateInterpolator(1.5f));
        valueAnimator.setDuration(500);

        // 3
        valueAnimator.start();
    }

}
