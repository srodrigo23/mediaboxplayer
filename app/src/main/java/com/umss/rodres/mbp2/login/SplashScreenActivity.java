package com.umss.rodres.mbp2.login;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.umss.rodres.mbp2.R;

public class SplashScreenActivity extends AppCompatActivity {

    private CountDownTimer splashTimer;
    private AppCompatImageView musicLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startSplashTimer();
    }

    private void startSplashTimer() {
        splashTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                startLogoAnimation();
            }
            @Override
            public void onFinish() {
                try{
                    startHomeScreenActivity();
                }catch (Exception ex){
                    splashTimer.cancel();
                }
            }
        }.start();
    }

    private void startLogoAnimation() {
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        zoomIn.reset();
        musicLogo = (AppCompatImageView) findViewById(R.id.app_logo_splash);
        musicLogo.clearAnimation();
        musicLogo.startAnimation(zoomIn);
    }

    private void startHomeScreenActivity() {
        Intent loginScreenActivity = new Intent(SplashScreenActivity.this,
                LogInActivity.class);
        startActivity(loginScreenActivity);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        splashTimer.cancel();
        splashTimer.onFinish();
    }
}