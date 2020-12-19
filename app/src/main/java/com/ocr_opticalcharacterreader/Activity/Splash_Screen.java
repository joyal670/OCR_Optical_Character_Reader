package com.ocr_opticalcharacterreader.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ocr_opticalcharacterreader.R;

public class Splash_Screen extends AppCompatActivity
{
    Handler handler;
    ImageView splash_image;
    TextView splash_text;
    Animation bottom, above;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash__screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlue));
        }

        splash_image = findViewById(R.id.splash_image);
        splash_text = findViewById(R.id.splash_text);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);
        splash_image.setAnimation(bottom);

        above = AnimationUtils.loadAnimation(this, R.anim.above);
        splash_text.setAnimation(above);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}