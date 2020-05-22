package com.example.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.floor;

public class ScalingActivity extends AppCompatActivity implements View.OnClickListener{

    // Первоначальное изображений, без изменений
    private Uri sourceImageURI;
    // Изображение до наложения фильтра
    private Uri originalImageURI;
    // Изображение после наложения фильтра
    private Uri resultImageURI;

    private ImageView resultImage;

    private TextView mTextView;

    Button cancel;
    Button apply;
    Button scaling;

    Button X0_5;
    Button X0_8;
    Button X1_5;
    Button X2;

    float Cord;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaling);

        mTextView = findViewById(R.id.textView);
        // Получить изображение
        sourceImageURI = getIntent().getParcelableExtra("source");
        originalImageURI = getIntent().getParcelableExtra("original");

        resultImageURI = originalImageURI;

        resultImage = findViewById(R.id.resultImage);
        resultImage.setImageURI(originalImageURI);

        scaling = findViewById(R.id.ScalingButton);
        scaling.setOnClickListener(this);

        cancel = findViewById(R.id.cancelButton);
        cancel.setOnClickListener(this);

        apply = findViewById(R.id.applyButton);
        apply.setOnClickListener(this);

        X0_5 = findViewById(R.id.button3);
        X0_5.setOnClickListener(this);

        X0_8 = findViewById(R.id.button4);
        X0_8.setOnClickListener(this);

        X1_5 = findViewById(R.id.button5);
        X1_5.setOnClickListener(this);

        X2 = findViewById(R.id.button6);
        X2.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ScalingButton:
                Bitmap imageBitmap = ((BitmapDrawable)resultImage.getDrawable()).getBitmap();
                int newW = (int)(imageBitmap.getWidth()*Cord);
                int newH = (int)(imageBitmap.getHeight()*Cord);
                Bitmap resultBitMap = resample(imageBitmap.getWidth(),imageBitmap.getHeight(),newW,newH,imageBitmap);
                resultImage.setImageBitmap(resultBitMap);
                try {
                    resultImageURI = bitmapToUriConverter(resultBitMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Отменить изменения и выйти в редактор
            case R.id.cancelButton:
                Intent callEditorIntent = new Intent(ScalingActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", originalImageURI);
                startActivity(callEditorIntent);
                break;

            case R.id.applyButton:
                 callEditorIntent = new Intent(ScalingActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", resultImageURI);
                startActivity(callEditorIntent);
                break;

            case R.id.button3:
                Cord =(float) 0.5;
                mTextView.setText("X" + Cord);
                break;

            case R.id.button4:
                Cord =(float) 0.8;
                mTextView.setText("X" + Cord);
                break;
            case R.id.button5:
                Cord =(float) 1.5;
                mTextView.setText("X" + Cord);
                break;
            case R.id.button6:
                Cord =(float) 2;
                mTextView.setText("X" + Cord);
                break;
        }
    }

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

   public Bitmap resample(int oldw, int oldh, int neww, int newh, Bitmap a_Bit)
    {
        int[] a = new int[oldh*oldw];
        a_Bit.getPixels(a,0,oldw,0,0,oldw,oldh);
        int i, j;
        int h, w;
        float t;
        float u;
        float tmp;
        float d1, d2, d3, d4;
        int p1, p2, p3, p4;

        int red, green, blue;
        int[] b = new int[newh*neww];
        for (j = 0; j < newh; j++) {
            tmp = (float) (j) / (float) (newh - 1) * (oldh - 1);
            h = (int) floor(tmp);
            if (h < 0) {
                h = 0;
            } else {
                if (h >= oldh - 1) {
                    h = oldh - 2;
                }
            }
            u = tmp - h;

            for (i = 0; i < neww; i++) {

                tmp = (float) (i) / (float) (neww - 1) * (oldw - 1);
                w = (int) floor(tmp);
                if (w < 0) {
                    w = 0;
                } else {
                    if (w >= oldw - 1) {
                        w = oldw - 2;
                    }
                }
                t = tmp - w;

                d1 = (1 - t) * (1 - u);
                d2 = t * (1 - u);
                d3 = t * u;
                d4 = (1 - t) * u;

                p1 = a[w + oldw*h];
                p2 = a[w +1 + oldw*h];
                p3 = a[w+1 + oldw*(h+1)];
                p4 = a[w + oldw*(h+1)];

                blue = (int)(((p1 & 0xff)) * d1 + ((p2 & 0xff)) * d2 + (char) ((p3 & 0xff)) * d3 + ((p4 & 0xff)) * d4);
                green = (int)(((p1 & 0xff00) >> 8) * d1 + ((p2 & 0xff00) >> 8) * d2 + (char) ((p3 & 0xff00) >> 8) * d3 + ((p4 & 0xff00)>> 8) * d4);
                red = (int)(((p1 & 0xff0000) >> 16) * d1 + ((p2 & 0xff0000) >> 16) * d2 + (char) ((p3 & 0xff0000) >> 16) * d3 + ((p4 & 0xff0000)>> 16) * d4);

               b[i+neww*j] = 0xff000000 | (red << 16) |  (green << 8) | (blue);
            }
        }
        Bitmap newBitMap = Bitmap.createBitmap(neww,newh, Bitmap.Config.ARGB_8888);

        newBitMap.setPixels(b,0,neww,0,0,neww,newh);
        return newBitMap;
    }
}
