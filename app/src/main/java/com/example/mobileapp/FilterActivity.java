package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.net.URI;

public class FilterActivity extends AppCompatActivity implements OnClickListener {

    // Первоначальное изображений, без изменений
    private Uri sourceImageURI;
    // Изображение до наложения фильтра
    private Uri originalImageURI;
    // Изображение после наложения фильтра
    private Uri resultImageURI;

    // Кнопки для наложения красного, зелёного и синего фильтра соответственно
    Button redColor;
    Button greenColor;
    Button blueColor;

    // Изображение
    ImageView resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // Получить изображение
        sourceImageURI = getIntent().getParcelableExtra("source");
        originalImageURI = getIntent().getParcelableExtra("original");
        resultImageURI = originalImageURI;

        resultImage = (ImageView)findViewById(R.id.resultImage);
        resultImage.setImageURI(originalImageURI);

        // Инициализировать кнопки
        redColor = (Button)findViewById(R.id.redButton);
        redColor.setOnClickListener(this);

        greenColor = (Button)findViewById(R.id.greenButton);
        greenColor.setOnClickListener(this);

        blueColor = (Button)findViewById(R.id.blueButton);
        blueColor.setOnClickListener(this);
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
                        pixelRed += 20;
                        if (pixelRed > 255)
                            pixelRed = 255;
                        break;

                    case "green":
                        pixelGreen += 20;
                        if (pixelGreen > 255)
                            pixelGreen = 255;
                        break;

                    case "blue":
                        pixelBlue += 20;
                        if (pixelBlue > 255)
                            pixelBlue = 255;
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
        Drawable resultDrawable = new BitmapDrawable(resultBitmap);
        resultImage.setImageBitmap(resultBitmap);
        resultImageURI = Uri.parse(resultImage.toString());
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            // Применить изменения и выйти в редактор
            case R.id.applyButton:
                Intent editorIntent = new Intent(FilterActivity.this,
                        GraphicsEditorActivity.class);

                editorIntent.putExtra("source", sourceImageURI);
                editorIntent.putExtra("result", resultImageURI);
                startActivity(editorIntent);
                break;

            // Отменить изменения и выйти в редактор
            case R.id.cancelButton:
                editorIntent = new Intent(FilterActivity.this,
                        GraphicsEditorActivity.class);

                editorIntent.putExtra("source", sourceImageURI);
                editorIntent.putExtra("original", originalImageURI);
                startActivity(editorIntent);
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

            default:
                break;
        }
    }
}
