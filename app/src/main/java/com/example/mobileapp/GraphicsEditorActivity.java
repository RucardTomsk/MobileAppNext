package com.example.mobileapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphicsEditorActivity extends AppCompatActivity implements View.OnClickListener {

    public ImageView resultImage; // обработанное изображение
    static final int GALLERY_REQUEST = 1;
    static final int CAMERA_REQUEST = 2;
    private Uri resultImageURI; // URI обработанного изображения
    private String mCurrentPhotoPath;

    Button segment;
    Button menu; // выход в меню приложения
    Button filter; // наложение цветового фильтра
    Button turn; // Поворот изображения
    Button scaling; // Маштабирование
    Button save;
    Button unsharpmask;
    Button retouch;

    ImageButton gallery; // выбор фотографии из приложения
    ImageButton camera; // открыть камеру
    Bitmap bitmap;

    /* Инициализировать кнопки редактора.
    Метод будет вызван, когда будет загружена фотография. */
    private void initButtons() {
        filter = findViewById(R.id.filterButton);
        filter.setOnClickListener(this);

        turn = (Button)findViewById(R.id.TurnButton);
        turn.setOnClickListener(this);

        scaling = (Button)findViewById(R.id.ScalingButton);
        scaling.setOnClickListener(this);

        unsharpmask = findViewById(R.id.UnsharpMaskButton);
        unsharpmask.setOnClickListener(this);

        retouch = findViewById(R.id.RetouchButton);
        retouch.setOnClickListener(this);

        segment = findViewById(R.id.segmentButton);
        segment.setOnClickListener(this);

        save = findViewById(R.id.saveButton);
        save.setOnClickListener(this);
    }

    private Bitmap SET(Bitmap bitmap){
        float aspectRetio = (float)bitmap.getHeight()/(float)bitmap.getWidth();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int MImageWidth = displayMetrics.widthPixels;
        int MImageHeight = (int)(MImageWidth*aspectRetio);
        Bitmap MBitMap = Bitmap.createScaledBitmap(bitmap,MImageWidth,MImageHeight,false);
        return MBitMap;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_graphics_editor);
        else
            setContentView(R.layout.activity_graphics_editor_n);

        if (getIntent().getParcelableExtra("result") != null) {
            resultImageURI = getIntent().getParcelableExtra("result");
            initButtons();
        }

        resultImage = findViewById(R.id.image);
        resultImage.setImageURI(resultImageURI);

        menu = findViewById(R.id.menuButton);
        menu.setOnClickListener(this);

        gallery = findViewById(R.id.galleryButton);
        gallery.setOnClickListener(this);

        camera = findViewById(R.id.cameraButton);
        camera.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.menuButton:
                final Intent callEditorIntent = new Intent(GraphicsEditorActivity.this, MainActivity.class);
                startActivity(callEditorIntent);
                break;

            case R.id.galleryButton:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                initButtons();
                break;

            case R.id.cameraButton:
                dispatchTakePictureIntent();
                initButtons();
                break;

            case R.id.filterButton:
                final Intent callFilterIntent = new Intent(GraphicsEditorActivity.this, FilterActivity.class);
                callFilterIntent.putExtra("original", resultImageURI);
                startActivity(callFilterIntent);
                break;

            case R.id.TurnButton:
                final Intent callTurnIntent = new Intent(GraphicsEditorActivity.this, TurnActivity.class);
                callTurnIntent.putExtra("original",resultImageURI);
                startActivity(callTurnIntent);
                break;

            case R.id.ScalingButton:
                final Intent callScalingIntent = new Intent(GraphicsEditorActivity.this, ScalingActivity.class);
                callScalingIntent.putExtra("original",resultImageURI);
                startActivity(callScalingIntent);
                break;

            case R.id.UnsharpMaskButton:
                final Intent callUnsharpIntent = new Intent(GraphicsEditorActivity.this, UnsharpMaskActivity.class);
                callUnsharpIntent.putExtra("original", resultImageURI);
                startActivity(callUnsharpIntent);
                break;

            case R.id.RetouchButton:
                final Intent callRetouchIntent = new Intent(GraphicsEditorActivity.this, RetouchActivity.class);
                callRetouchIntent.putExtra("original", resultImageURI);
                startActivity(callRetouchIntent);
                break;

            case R.id.segmentButton:
                final Intent callSegmentationIntent = new Intent(GraphicsEditorActivity.this, SegmentationActivity.class);
                callSegmentationIntent.putExtra("original", resultImageURI);
                startActivity(callSegmentationIntent);
                break;

            case R.id.saveButton:
                BitmapDrawable draw = (BitmapDrawable) resultImage.getDrawable();
                Bitmap bitmap = draw.getBitmap();

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/YourFolderName");
                dir.mkdirs();
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outFile));
                sendBroadcast(intent);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        resultImageURI = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(resultImageURI);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        resultImage.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK ) {
                    resultImage.setImageURI(resultImageURI);
                }
                    break;

            default:
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                resultImageURI = FileProvider.getUriForFile(this,
                        "com.example.android.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, resultImageURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
