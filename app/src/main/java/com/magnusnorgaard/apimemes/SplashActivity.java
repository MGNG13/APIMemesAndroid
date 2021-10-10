package com.magnusnorgaard.apimemes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class SplashActivity extends AppCompatActivity {

    private final float timeSplashScreen = 1000;
    private CountDownTimer countDownTimer;
    private CircularProgressBar splashscreen_circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        splashscreen_circularProgressBar = findViewById(R.id.splashscreen_circularProgressBar);
        splashscreen_circularProgressBar.setProgressMax(timeSplashScreen);
        countDownTimer = new CountDownTimer((long) timeSplashScreen, 250) {
            @Override
            public void onTick(long millisUntilFinished) {
                splashscreen_circularProgressBar.setProgressWithAnimation((timeSplashScreen - millisUntilFinished));
            }

            @Override
            public void onFinish() {
                SplashActivity.this.finish();
                startActivity(new Intent(SplashActivity.this, HomeScreenActivity.class));
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        splashscreen_circularProgressBar.setProgressMax(timeSplashScreen);
        countDownTimer = new CountDownTimer((long) timeSplashScreen, 250) {
            @Override
            public void onTick(long millisUntilFinished) {
                splashscreen_circularProgressBar.setProgressWithAnimation((timeSplashScreen - millisUntilFinished));
            }

            @Override
            public void onFinish() {
                SplashActivity.this.finish();
                startActivity(new Intent(SplashActivity.this, HomeScreenActivity.class));
            }
        }.start();
    }
}