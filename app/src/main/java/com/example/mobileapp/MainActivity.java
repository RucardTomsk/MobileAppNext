package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Bitmap;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.MediaStore;
import android.content.ActivityNotFoundException;
import android.os.Environment;
import androidx.core.content.FileProvider;
import androidx.core.app.AppComponentFactory;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements View.OnClickListener {
    //Объявляем используемые переменные:
    Button editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Menu");
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