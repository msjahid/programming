package com.example.tz.geo_map_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tz.geo_map_test.R;

/**
 * Created by Ajwad on 02/12/2016.
 */
public class SplashActivity extends AppCompatActivity {
    private TextView tv;
    private ImageView iv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);
        Animation start_anim = AnimationUtils.loadAnimation(this,R.anim.start_anim);
        tv.startAnimation(start_anim);
        iv.startAnimation(start_anim);
        final Intent intent = new Intent(this, MainActivity.class);
        Thread timer = new Thread(){
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
