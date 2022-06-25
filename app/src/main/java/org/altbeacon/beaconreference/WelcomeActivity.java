package org.altbeacon.beaconreference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class WelcomeActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 5000; //開啟畫面時間(2秒)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        GifImageView gifImageView = findViewById(R.id.imageView2);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.loading);
            gifImageView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }



        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class); //MainActivity為主要檔案名稱
                WelcomeActivity.this.startActivity(intent);

                // close this activity
                WelcomeActivity.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }

}