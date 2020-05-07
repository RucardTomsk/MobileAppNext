// Непосредствено графический редактор
package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.FileNotFoundException;
import java.io.InputStream;

public class GraphicsEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView resultImage; // обработанное изображение
    private ImageView originalImage; // оригинальное изображение
    static final int GALLERY_REQUEST = 1;
    static final int CAMERA_REQUEST = 2;
    private Uri picUri;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

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

        originalImage = (ImageView)findViewById(R.id.image);
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
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                break;

            case R.id.cameraButton:
                // TODO Call camera with permission
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;

            case R.id.filterButton:
                // TODO Call color filter algorithm
                break;

            default:
                break;
        }
    }

    //Обрабочик выбора действия из галлереи
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        //Получаем URI изображения, преобразуем его в Bitmap
                        //объект и отображаем в элементе ImageView нашего интерфейса:
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        originalImage.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK && imageReturnedIntent.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    if (bitmap != null) {
                        originalImage.setImageBitmap(bitmap);
                    }
                    break;
                }
        }
    }
}
