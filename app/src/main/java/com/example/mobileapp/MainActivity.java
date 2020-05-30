package com.example.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {
    //Объявляем используемые переменные:
    Button editor;
    Button paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_main);
        else
            setContentView(R.layout.activity_main_n);

       editor = findViewById(R.id.editorButton);
       editor.setOnClickListener(this);

       paint = findViewById(R.id.paintButton);
       paint.setOnClickListener(this);

   }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.editorButton:
                final Intent editorIntent = new Intent(MainActivity.this, GraphicsEditorActivity.class);
                startActivity(editorIntent);
                break;
            case R.id.paintButton:
                final Intent callPaintIntent = new Intent(MainActivity.this, PaintActivity.class);
                startActivity(callPaintIntent);
                break;

            default:
                break;
        }
    }
}