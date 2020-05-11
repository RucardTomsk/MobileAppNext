package com.example.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.applyButton:
                Intent callEditorIntent = new Intent(TurnActivity.this,
                        GraphicsEditorActivity.class);
                Matrix matrix = new Matrix();
                Bitmap bMap =((BitmapDrawable)resultImage.getDrawable()).getBitmap();
                resultImage.setScaleType(ImageView.ScaleType.MATRIX);   //required
                matrix.postRotate(StartingDegree, resultImage.getDrawable().getBounds().width()/2, resultImage.getDrawable().getBounds().height()/2);

                int newWidth = bMap.getWidth()/2;
                int newHeight = bMap.getHeight()/2;

                Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, newWidth, newHeight, matrix, true);

                try {
                    resultImageURI = bitmapToUriConverter(bMapRotate);
                } catch (IOException e) {
                    e.printStackTrace();
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
        resultImage.animate().scaleX(resultImage.getDrawable().getBounds().width()*1.0f/resultImage.getDrawable().getBounds().height()).scaleY(resultImage.getDrawable().getBounds().width()*1.0f/resultImage.getDrawable().getBounds().height()).start();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void rotate(int angle) {
        resultImage.animate().rotation(angle).start();
        StartingDegree = angle;
    }
}


