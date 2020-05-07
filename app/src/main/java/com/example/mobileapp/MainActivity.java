package com.example.mobileapp;

import java.io.FileNotFoundException;
import java.io.InputStream;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements View.OnClickListener {
    //Объявляем используемые переменные:
    Button editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editor = (Button)findViewById(R.id.editorButton);
        editor.setOnClickListener(this);
   }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.editorButton:
                Intent intent = new Intent(MainActivity.this, GraphicsEditorActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}