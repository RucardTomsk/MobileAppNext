package com.example.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnClickListener {
    //Объявляем используемые переменные:
    Button editor;
    Button paint;
    Button cube;
    Animation animAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editor = findViewById(R.id.editorButton);
        editor.setOnClickListener(this);

        paint = findViewById(R.id.paintButton);
        paint.setOnClickListener(this);

        cube = findViewById(R.id.cubeButton);
        cube.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editorButton:
                final Intent editorIntent = new Intent(MainActivity.this, GraphicsEditorActivity.class);
                startActivity(editorIntent);
                break;
            case R.id.paintButton:
                final Intent callPaintIntent = new Intent(MainActivity.this, PaintActivity.class);
                startActivity(callPaintIntent);
                break;
            case R.id.cubeButton:

                break;
            default:
                break;
        }
    }
}