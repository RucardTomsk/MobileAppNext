package com.example.mobileapp;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.floor;
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

    TextView progressText;

    ProgressBar progressBar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // Получить изображение
        originalImageURI = getIntent().getParcelableExtra("original");
        resultImageURI = originalImageURI;

        resultImage = findViewById(R.id.resultImage);
        resultImage.setImageURI(resultImageURI);

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

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.textView4);

       }

    // Наложить фильтр на изображение
    private void applyFilter(String color) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
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
            progressBar.incrementProgressBy(1);
            progressText.setText(String.valueOf(progressBar.getProgress() + ProgressShift) + "%");
        }
        resultBitmap.setPixels(pixelArray, 0, width, 0, 0, width, height);
        // Отобразим изменения
        resultImage.setImageBitmap(resultBitmap);
        try {
            resultImageURI = bitmapToUriConverter(resultBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressBar.setProgress(0);
        progressText.setText(String.valueOf(0 + "%"));
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
                applyFilter("blue");
                break;

            // Применить зелёный фильтр
            case R.id.greenButton:
                applyFilter("green");
                break;

            // Применить красный фильтр
            case R.id.redButton:
                applyFilter("red");
                break;

            // Применить негатив
            case R.id.negativeButton:
                applyFilter("negative");
                break;
            // Применить серые тона
            case R.id.greyButton:
                applyFilter("grey");
                break;

            default:
                break;
        }
    }
}
