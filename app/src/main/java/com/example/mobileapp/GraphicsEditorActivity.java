// Непосредствено графический редактор
package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GraphicsEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView resultImage; // обработанное изображение
    private ImageView originalImage; // оригинальное изображение
    static final int GALLERY_REQUEST = 1;

    Button menu; // выход в меню приложения
    Button filter; // наложение цветового фильтра
    Button gallery; // выбор фотографии из приложения
    Button camera; // открыть камеру

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics_editor);

        menu = (Button)findViewById(R.id.menuButton);
        menu.setOnClickListener(this);

        /*filter = (Button)findViewById(R.id.filterButton);
        filter.setOnClickListener(this);*/

        gallery = (Button)findViewById(R.id.galleryButton);
        gallery.setOnClickListener(this);

        camera = (Button)findViewById(R.id.cameraButton);
        camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.menuButton:
                Intent intent = new Intent (GraphicsEditorActivity.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.galleryButton:
                // TODO open phone gallery with permission
                break;

            case R.id.cameraButton:
                // TODO Call camera with permission
                break;

            case R.id.filterButton:
                // TODO Call color filter algorithm
                break;

            default:
                break;
        }
    }
}
