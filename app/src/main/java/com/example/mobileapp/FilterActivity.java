package com.example.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

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

    // Получить Uri из Bitmap
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
       }

    // Наложить фильтр на изображение
    private void applyFilter(String color) {
        Bitmap imageBitmap = ((BitmapDrawable)resultImage.getDrawable()).getBitmap();
        Bitmap resultBitmap = Bitmap.createBitmap(imageBitmap.getWidth(),
                imageBitmap.getHeight(), imageBitmap.getConfig());

        for(int x = 0; x < imageBitmap.getWidth(); x++) {
            for(int y = 0; y < imageBitmap.getHeight(); y++) {

                // Получим информацию о пикселе
                int pixelColor = imageBitmap.getPixel(x, y);
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
                resultBitmap.setPixel(x, y, newPixel);
            }
        }
        // Отобразим изменения
        resultImage.setImageBitmap(resultBitmap);
        resultImageURI = getImageUri(getApplicationContext(), resultBitmap);
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
