package com.example.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

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

        mTextView = (TextView) findViewById(R.id.textView);
        // Получить изображение
        sourceImageURI = getIntent().getParcelableExtra("source");
        originalImageURI = getIntent().getParcelableExtra("original");

        resultImageURI = originalImageURI;

        resultImage = (ImageView) findViewById(R.id.resultImage);
        resultImage.setImageURI(originalImageURI);
        // resultImage.animate().scaleX(resultImage.getDrawable().getBounds().width()*1.0f/resultImage.getDrawable().getBounds().height()).scaleY(resultImage.getDrawable().getBounds().width()*1.0f/resultImage.getDrawable().getBounds().height()).start();

        scaling = (Button)findViewById(R.id.ScalingButton);
        scaling.setOnClickListener(this);

        cancel = (Button)findViewById(R.id.cancelButton);
        cancel.setOnClickListener(this);

        apply = (Button)findViewById(R.id.applyButton);
        apply.setOnClickListener(this);

        X0_5 = (Button)findViewById(R.id.button3);
        X0_5.setOnClickListener(this);

        X0_8 = (Button)findViewById(R.id.button4);
        X0_8.setOnClickListener(this);

        X1_5 = (Button)findViewById(R.id.button5);
        X1_5.setOnClickListener(this);

        X2 = (Button)findViewById(R.id.button6);
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
                Bitmap resultBitMap = resizeBilinearGray(imageBitmap, resultImage.getWidth(), resultImage.getHeight(), newW, newH);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap resizeBilinearGray(Bitmap pixels_E, int w, int h, int w2, int h2) {
        int[] pixels = new int[w*h];
        int counter = 0;

        for(int i = 0; i < pixels_E.getHeight(); i++){
            for(int g = 0; g < pixels_E.getWidth(); g++){
                pixels[counter] = pixels_E.getPixel(g,i);
                counter++;
            }
        }

        int[] temp = new int[w2*h2] ;
        int A, B, C, D, x, y, index, gray ;
        float x_ratio = ((float)(w-1))/w2 ;
        float y_ratio = ((float)(h-1))/h2 ;
        float x_diff, y_diff, ya, yb ;
        int offset = 0 ;
        for (int i=0;i<h2;i++) {
            for (int j=0;j<w2;j++) {
                x = (int)(x_ratio * j) ;
                y = (int)(y_ratio * i) ;
                x_diff = (x_ratio * j) - x ;
                y_diff = (y_ratio * i) - y ;
                index = y*w+x ;

                A = pixels[index];
                B = pixels[index+1];
                C = pixels[index+w];
                D = pixels[index+w+1];

                // Y = A(1-w)(1-h) + B(w)(1-h) + C(h)(1-w) + Dwh
                float xx = x*x;
                gray = (int)(
                        A*(1-x_diff)*(1-y_diff) +  B*(x_diff)*(1-y_diff) +
                                C*(y_diff)*(1-x_diff)   +  D*(x_diff*y_diff) );

                temp[offset] = gray ;
                offset++;
            }
        }

        Bitmap newBitMap = Bitmap.createBitmap(w2,h2,pixels_E.getConfig());
        counter = 0;

        for(int i = 0; i < newBitMap.getHeight(); i++){
            for(int g = 0; g < newBitMap.getWidth(); g++){
                newBitMap.setPixel(g,i,temp[counter]);
                counter++;
            }
        }
        return newBitMap;
    }

    public Uri bitmapToUriConverter(Bitmap mBitmap) throws IOException {
        Uri uri = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // Calculate inSampleSize
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 400,
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
