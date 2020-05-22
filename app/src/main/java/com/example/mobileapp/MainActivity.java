package com.example.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnClickListener {
    //Объявляем используемые переменные:
    ImageButton editor;
    Animation animAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Menu");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        editor = findViewById(R.id.editorButton);
        editor.setOnClickListener(this);
   }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.editorButton:
                editor.startAnimation(animAlpha);
                final Intent editorIntent = new Intent(MainActivity.this, GraphicsEditorActivity.class);
                startActivity(editorIntent);
                break;

            default:
                break;
        }
    }
}