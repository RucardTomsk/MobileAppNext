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

public class GraphicsEditorActivity extends AppCompatActivity implements View.OnClickListener {

    public ImageView resultImage; // обработанное изображение
    public ImageView originalImage; // оригинальное изображение
    static final int GALLERY_REQUEST = 1;
    static final int CAMERA_REQUEST = 2;
    final int PIC_CROP = 3;
    private Uri photoURI;
    private String mCurrentPhotoPath;

    Button menu; // выход в меню приложения
    Button filter; // наложение цветового фильтра
    Button gallery; // выбор фотографии из приложения
    Button camera; // открыть камеру

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Graphics Editor");

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
                dispatchTakePictureIntent();
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
                if (resultCode == RESULT_OK ) {
                   // Bitmap thumbnailBitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                //    performCrop();
                    originalImage.setImageURI(photoURI);
                }
                    break;

            case PIC_CROP:
                if (resultCode == RESULT_OK ) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    // Получим кадрированное изображение
                    Bitmap thePic = extras.getParcelable("data");
                    // передаём его в ImageView
                    originalImage.setImageBitmap(thePic);
                }
                break;
        }
    }

   /* private void performCrop(){
        try {
            // Намерение для кадрирования. Не все устройства поддерживают его
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException e){
            String errorMessage = "Извините, но ваше устройство не поддерживает кадрирование";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }*/

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        Bitmap mImageBitmap = (Bitmap)extras.get("data");
        originalImage.setImageBitmap(mImageBitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
