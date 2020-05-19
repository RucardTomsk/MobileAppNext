package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;


class Pixel {
    // координаты пикселя
    public int imageX;
    public int imageY;
    // значение пикселя
    int pixel;

    Pixel(int x, int y, int newPixel) {
        imageX = x;
        imageY = y;
        pixel = newPixel;
    }
}

public class RetouchActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    // Изображение до наложения ретуши
    private Uri originalImageURI;
    // Изображение после наложения ретуши
    private Uri resultImageURI;

    // Кнопки для отмены или принятия изменений
    Button cancel;
    Button apply;

    // Изображение
    ImageView resultImage;
    // Текущее изображение в формате bitmap
    Bitmap imageBitmap;

    // координаты области касания
    HashSet<Pixel> pixels = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retouch);

        // Получить изображение
        originalImageURI = getIntent().getParcelableExtra("original");
        resultImageURI = originalImageURI;

        resultImage = findViewById(R.id.image);
        resultImage.setImageURI(resultImageURI);
        resultImage.setOnTouchListener(this);

        cancel = findViewById(R.id.cancelButton);
        cancel.setOnClickListener(this);

        apply = findViewById(R.id.applyButton);
        apply.setOnClickListener(this);

        imageBitmap = ((BitmapDrawable)resultImage.getDrawable()).getBitmap();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Применить изменения и выйти в редактор
            case R.id.applyButton:
                Intent callEditorIntent = new Intent(RetouchActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", resultImageURI);
                startActivity(callEditorIntent);
                break;

            // Отменить изменения и выйти в редактор
            case R.id.cancelButton:
                callEditorIntent = new Intent(RetouchActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", originalImageURI);
                startActivity(callEditorIntent);
                break;

            default:
                break;
        }
    }

    private void addPixel(int x, int y) {
        if (0 <= x && x < imageBitmap.getWidth() &&
                0 <= y && y < imageBitmap.getHeight()) {

            Pixel currentPixel = new Pixel(x, y,
                    imageBitmap.getPixel(x, y));
            pixels.add(currentPixel);
        }
    }

    private void addArea(int x, int y) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 21; j++) {
                addPixel(x - 10 + i, y - 10 + j);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // координаты текущего положения пальца на изображении
        float x = event.getX();
        float y = event.getY();
        x *= (float)imageBitmap.getWidth() / (float)resultImage.getWidth();
        y *= (float)imageBitmap.getHeight() / (float)resultImage.getHeight();
        addArea((int)x, (int)y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                retouchImage();
                break;

            default:
                break;
        }
        return true;
    }

    private void retouchImage() {
        Bitmap resultBitmap = Bitmap.createBitmap(imageBitmap.getWidth(),
                imageBitmap.getHeight(), imageBitmap.getConfig());

        final int width = resultBitmap.getWidth();
        final int height = resultBitmap.getHeight();
        int[] pixelArray = new int[width * height];
        imageBitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);

        int averagePixelRed = 0;
        int averagePixelGreen = 0;
        int averagePixelBlue = 0;
        int size = pixels.size();

        for (Pixel i : pixels) {
            averagePixelRed += Color.red(i.pixel);
            averagePixelGreen += Color.green(i.pixel);
            averagePixelBlue += Color.blue(i.pixel);
        }

        averagePixelRed /= size;
        averagePixelGreen /= size;
        averagePixelBlue /= size;

        for (Pixel i : pixels) {
            int pixelAlpha = Color.alpha(i.pixel);
            int pixelRed = Color.red(i.pixel);
            int pixelGreen = Color.green(i.pixel);
            int pixelBlue = Color.blue(i.pixel);

            pixelRed += (averagePixelRed - pixelRed) * 0.3206;
            pixelGreen += (averagePixelGreen - pixelGreen) * 0.3206;
            pixelBlue += (averagePixelBlue - pixelBlue) * 0.3206;

            pixelRed = (max(0, min(255, pixelRed)));
            pixelGreen = (max(0, min(255, pixelGreen)));
            pixelBlue = (max(0, min(255, pixelBlue)));

            int newPixel = Color.argb(pixelAlpha, pixelRed, pixelGreen, pixelBlue);
            pixelArray[width * i.imageY + i.imageX] = newPixel;
        }

        resultBitmap.setPixels(pixelArray, 0, width, 0, 0, width, height);
        // Отобразим изменения
        resultImage.setImageBitmap(resultBitmap);
        imageBitmap = resultBitmap;
        try {
            resultImageURI = bitmapToUriConverter(resultBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pixels.clear();
    }

    // Получить Uri из Bitmap
    public Uri bitmapToUriConverter(Bitmap mBitmap) throws IOException {
        Uri uri = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // Calculate inSampleSize
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth(), mBitmap.getHeight(),
                true);
        File file = new File(getFilesDir(), "Image" + new Random().nextInt() + "jpeg");
        FileOutputStream out = openFileOutput(file.getName(),
                Context.MODE_APPEND);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        //get absolute path
        String realPath = file.getAbsolutePath();
        File f = new File(realPath);
        uri = Uri.fromFile(f);

        return uri;
    }
}