package com.example.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.min;

public class FilterActivity extends AppCompatActivity implements OnClickListener {

    // Изображение до наложения фильтра
    private Uri originalImageURI;
    // Изображение после наложения фильтра
    private Uri resultImageURI;

    // Кнопки для наложения красного, зелёного и синего фильтра соответственно
    Button redColor;
    Button greenColor;
    Button blueColor;
    Button negative;
    Button grey;
    Button cancel;
    Button apply;

    // Изображение
    ImageView resultImage;
    ImageView ImageBlue,ImageRed,ImageNegetive,ImageGrey,ImageGreen ;

    // Получить Uri из Bitmap
    public Uri bitmapToUriConverter(Bitmap mBitmap) throws IOException {
        Uri uri = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth(), mBitmap.getHeight(),
                true);
        File file = new File(getFilesDir(), "Image" + new Random().nextInt() + "jpeg");
        FileOutputStream out = openFileOutput(file.getName(),
                Context.MODE_APPEND);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        String realPath = file.getAbsolutePath();
        File f = new File(realPath);
        uri = Uri.fromFile(f);

        return uri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_filter);
        else
            setContentView(R.layout.activity_filter_n);

        // Получить изображение
        originalImageURI = getIntent().getParcelableExtra("original");
        resultImageURI = originalImageURI;

        resultImage = findViewById(R.id.resultImage);
        resultImage.setImageURI(resultImageURI);

        if (savedInstanceState != null) {
            Bitmap newBitMap = Bitmap.createBitmap(savedInstanceState.getInt("W"),savedInstanceState.getInt("H"), Bitmap.Config.ARGB_8888);
            newBitMap.setPixels(savedInstanceState.getIntArray("resultImage"),0,newBitMap.getWidth(),0,0,newBitMap.getWidth(),newBitMap.getHeight());
            resultImage.setImageBitmap(newBitMap);
            try {
                resultImageURI = bitmapToUriConverter(newBitMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Инициализировать кнопки
        redColor = findViewById(R.id.redButton);
        redColor.setOnClickListener(this);

        greenColor = findViewById(R.id.greenButton);
        greenColor.setOnClickListener(this);

        blueColor = findViewById(R.id.blueButton);
        blueColor.setOnClickListener(this);

        negative = findViewById(R.id.negativeButton);
        negative.setOnClickListener(this);

        grey = findViewById(R.id.greyButton);
        grey.setOnClickListener(this);

        cancel = findViewById(R.id.cancelButton);
        cancel.setOnClickListener(this);

        apply = findViewById(R.id.applyButton);
        apply.setOnClickListener(this);

        ImageBlue = findViewById(R.id.imageViewBlue);
        applyFilter("blue",ImageBlue,false);
        ImageRed = findViewById(R.id.imageViewRed);
        applyFilter("red",ImageRed,false);
        ImageGreen = findViewById(R.id.imageViewGreen);
        applyFilter("green",ImageGreen,false);
        ImageGrey = findViewById(R.id.imageViewGrey);
        applyFilter("grey",ImageGrey,false);
        ImageNegetive = findViewById(R.id.imageViewNegative);
        applyFilter("negative",ImageNegetive,false);
       }

    // Наложить фильтр на изображение
    private void applyFilter(String color, ImageView image, boolean BOL) {
        Bitmap imageBitmap = ((BitmapDrawable)resultImage.getDrawable()).getBitmap();
        Bitmap resultBitmap = Bitmap.createBitmap(imageBitmap.getWidth(),
                imageBitmap.getHeight(), imageBitmap.getConfig());
        final int width = imageBitmap.getWidth();
        final int height = imageBitmap.getHeight();
        float ProgressShift = 100/width;
        int[] pixelArray = new int[width * height];
        imageBitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {

                // Получим информацию о пикселе
                int pixelColor = pixelArray[y * width + x];
                int pixelAlpha = Color.alpha(pixelColor);
                int pixelRed = Color.red(pixelColor);
                int pixelGreen = Color.green(pixelColor);
                int pixelBlue = Color.blue(pixelColor);


                // Наложим фильтр
                switch (color) {
                    case "red":
                        pixelRed = min(255, pixelRed + 25);
                        break;

                    case "green":
                        pixelGreen = min(255, pixelGreen + 25);
                        break;

                    case "blue":
                        pixelBlue = min(255, pixelBlue + 25);
                        break;

                    case "negative":
                        pixelRed = 255 - pixelRed;
                        pixelGreen = 255 - pixelGreen;
                        pixelBlue = 255 - pixelBlue;
                        break;

                    case "grey":
                        final int pixelGrey = (int)(pixelRed * 0.2126 + pixelGreen * 0.7152 + pixelBlue * 0.0722);
                        pixelRed = pixelGrey;
                        pixelGreen = pixelGrey;
                        pixelBlue = pixelGrey;
                        break;

                    default:
                        break;
                }

                // Создадим новый пиксель и заменим им старый
                int newPixel = Color.argb(pixelAlpha, pixelRed,
                        pixelGreen, pixelBlue);
                pixelArray[y * width + x] = newPixel;
            }
        }
        resultBitmap.setPixels(pixelArray, 0, width, 0, 0, width, height);
        // Отобразим изменения
        image.setImageBitmap(resultBitmap);
        try {
            if(BOL)
            resultImageURI = bitmapToUriConverter(resultBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            // Применить изменения и выйти в редактор
            case R.id.applyButton:
                Intent callEditorIntent = new Intent(FilterActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", resultImageURI);
                startActivity(callEditorIntent);
                break;

            // Отменить изменения и выйти в редактор
            case R.id.cancelButton:
                callEditorIntent = new Intent(FilterActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", originalImageURI);
                startActivity(callEditorIntent);
                break;

            // Применить синий фильтр
            case R.id.blueButton:
                applyFilter("blue",resultImage,true);
                break;

            // Применить зелёный фильтр
            case R.id.greenButton:
                applyFilter("green",resultImage,true);
                break;

            // Применить красный фильтр
            case R.id.redButton:
                applyFilter("red",resultImage,true);
                break;

            // Применить негатив
            case R.id.negativeButton:
                applyFilter("negative",resultImage,true);
                break;
            // Применить серые тона
            case R.id.greyButton:
                applyFilter("grey",resultImage,true);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bitmap imageBitmap = ((BitmapDrawable)resultImage.getDrawable()).getBitmap();
        int[] mas = new int[imageBitmap.getHeight()*imageBitmap.getWidth()];
        imageBitmap.getPixels(mas,0,imageBitmap.getWidth(),0,0,imageBitmap.getWidth(),imageBitmap.getHeight());
        outState.putIntArray("resultImage", mas);
        outState.putInt("W", imageBitmap.getWidth());
        outState.putInt("H", imageBitmap.getHeight());
    }

}
