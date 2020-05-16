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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;

public class TurnActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    // Первоначальное изображений, без изменений
    private Uri sourceImageURI;
    // Изображение до наложения фильтра
    private Uri originalImageURI;
    // Изображение после наложения фильтра
    private Uri resultImageURI;

    private ImageView resultImage;

    private TextView mTextView;

    private int StartingDegree = 0;

    Button cancel;
    Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);

        mTextView = (TextView) findViewById(R.id.textView);
        // Получить изображение
        sourceImageURI = getIntent().getParcelableExtra("source");
        originalImageURI = getIntent().getParcelableExtra("original");

        resultImageURI = originalImageURI;

        resultImage = (ImageView) findViewById(R.id.resultImage);
        resultImage.setImageURI(originalImageURI);
       // resultImage.animate().scaleX(resultImage.getDrawable().getBounds().width()*1.0f/resultImage.getDrawable().getBounds().height()).scaleY(resultImage.getDrawable().getBounds().width()*1.0f/resultImage.getDrawable().getBounds().height()).start();


        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText("0");

        cancel = (Button)findViewById(R.id.cancelButton);
        cancel.setOnClickListener(this);

        apply = (Button)findViewById(R.id.applyButton);
        apply.setOnClickListener(this);

      }

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

    public Bitmap BitMapRotate(Bitmap bitmap, int degrees){
        double rad = (degrees*3.14f)/180f;
        double cosf = cos(rad);
        double sinf = sin(rad);

        int newWidth = bitmap.getWidth();
        int newHeight = bitmap.getHeight();

        int x1 = (int)(-newHeight*sinf);
        int y1 = (int)(newHeight*cosf);
        int x2 = (int)(newWidth*cosf - newHeight*sinf);
        int y2 = (int)(newHeight*cosf + newWidth*sinf);
        int x3 = (int)(newWidth*cosf);
        int y3 = (int)(newWidth*sinf);

        int minX = min(0,min(x1,min(x2,x3)));
        int minY = min(0,min(y1,min(y2,y3)));
        int maxX = max(0,max(x1,max(x2,x3)));
        int maxY = max(0,max(y1,max(y2,y3)));

        int Width = maxX - minX;
        int Height = maxY - minY;

        Bitmap newBitMap = Bitmap.createBitmap(Width,Height,bitmap.getConfig());

        for(int y = 0; y < Height; y++){
            for(int x = 0; x < Width; x++){
                int sourceX = (int)((x + minX)*cosf + (y+minY)*sinf);
                int sourceY = (int)((y+minY)*cosf - (x+minX)*sinf);
                if(sourceX >= 0 && sourceX < newWidth && sourceY >= 0 && sourceY < newHeight)
                    newBitMap.setPixel(x,y,bitmap.getPixel(sourceX,sourceY));
                else
                    newBitMap.setPixel(x,y, Color.WHITE);
            }
        }
        return newBitMap;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.applyButton:
                Intent callEditorIntent = new Intent(TurnActivity.this,
                        GraphicsEditorActivity.class);
                Bitmap imageBitmap = ((BitmapDrawable)resultImage.getDrawable()).getBitmap();
               if(StartingDegree >= 0 && StartingDegree <= 90)
                {
                    try {
                        resultImageURI = bitmapToUriConverter(BitMapRotate(imageBitmap, StartingDegree));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                   if(StartingDegree >= 90 && StartingDegree <=180) {
                           imageBitmap = BitMapRotate(imageBitmap, 90);
                           try {
                               resultImageURI = bitmapToUriConverter(BitMapRotate(imageBitmap, StartingDegree - 90));
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                   }

                   if (StartingDegree >= 180 && StartingDegree <= 270) {
                           imageBitmap = BitMapRotate(imageBitmap, 90);
                           imageBitmap = BitMapRotate(imageBitmap, 90);
                           try {
                               resultImageURI = bitmapToUriConverter(BitMapRotate(imageBitmap, StartingDegree - 180));
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                if (StartingDegree >= 270 && StartingDegree <= 360) {
                    imageBitmap = BitMapRotate(imageBitmap, 90);
                    imageBitmap = BitMapRotate(imageBitmap, 90);
                    imageBitmap = BitMapRotate(imageBitmap, 90);
                    try {
                        resultImageURI = bitmapToUriConverter(BitMapRotate(imageBitmap, StartingDegree - 270));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //  resultImageURI = getImageUri(getApplicationContext(), resultBitmap);

                callEditorIntent.putExtra("result", resultImageURI);
                startActivity(callEditorIntent);
                break;

            // Отменить изменения и выйти в редактор
            case R.id.cancelButton:
                callEditorIntent = new Intent(TurnActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", originalImageURI);
                startActivity(callEditorIntent);
                break;
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mTextView.setText(String.valueOf(seekBar.getProgress()));
        rotate(seekBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void rotate(int angle) {
        resultImage.animate().rotation(angle).start();
        StartingDegree = angle;
    }
}


