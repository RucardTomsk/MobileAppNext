package com.example.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class UnsharpMaskActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    // Изображение до наложения фильтра
    private Uri originalImageURI;
    // Изображение после наложения фильтра
    private Uri resultImageURI;

    ImageView resultImage;
    Button gaussianButton;
    Button UnsharpMaskButton;
    Button Cancel;
    Button Apply;

    int Radius = 0;
    int Threshold = 0;

    TextView RadiusText;
    TextView ThresholdText;
    TextView LoadinText;

    ProgressBar LoadingProgres;

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
            setContentView(R.layout.activity_unsharp_mask);
        else
            setContentView(R.layout.activity_unsharp_mask_n);

        originalImageURI = getIntent().getParcelableExtra("original");

        resultImageURI = originalImageURI;

        resultImage = findViewById(R.id.resultImage);
        resultImage.setImageURI(originalImageURI);

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

        gaussianButton = findViewById(R.id.GaussianBlurButton);
        gaussianButton.setOnClickListener(this);

        UnsharpMaskButton = findViewById(R.id.UnsharpMaskButton);
        UnsharpMaskButton.setOnClickListener(this);

        SeekBar RadiusSeekBar = findViewById(R.id.RadiusSeekBar);
        RadiusSeekBar.setOnSeekBarChangeListener(this);

        SeekBar ThresholdSeekBar = findViewById(R.id.ThresholdSeekBar);
        ThresholdSeekBar.setOnSeekBarChangeListener(this);

        RadiusText = findViewById(R.id.RadiusText);
        ThresholdText = findViewById(R.id.ThresholdText);

        Cancel = findViewById(R.id.CancelButton);
        Cancel.setOnClickListener(this);

        Apply = findViewById(R.id.ApplyButton);
        Apply.setOnClickListener(this);

        LoadinText = findViewById(R.id.textViewLoading);
        LoadingProgres = findViewById(R.id.progressBar);

    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.GaussianBlurButton:
                Bitmap imageBitmap = ((BitmapDrawable)resultImage.getDrawable()).getBitmap();
                int[] mas_f = GaussianBlur(imageBitmap,Radius);
                Bitmap newBitMap = Bitmap.createBitmap(imageBitmap.getWidth(),imageBitmap.getHeight(), imageBitmap.getConfig());
                int counter = 0;
                newBitMap.setPixels(mas_f,0,imageBitmap.getWidth(),0,0,imageBitmap.getWidth(),imageBitmap.getHeight());
                 // Отобразим изменения
                resultImage.setImageBitmap(newBitMap);
                try {
                    resultImageURI = bitmapToUriConverter(newBitMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.UnsharpMaskButton:
                UnsharpTask UnsharpTask = new UnsharpTask();
                UnsharpTask.execute();
                break;

            case R.id.ApplyButton:
                Intent callEditorIntent = new Intent(UnsharpMaskActivity.this, GraphicsEditorActivity.class);
                callEditorIntent.putExtra("result", resultImageURI);
                startActivity(callEditorIntent);
                break;

            case R.id.CancelButton:
                callEditorIntent = new Intent(UnsharpMaskActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", originalImageURI);
                startActivity(callEditorIntent);
                break;

        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.RadiusSeekBar:
                Radius = seekBar.getProgress();
                RadiusText.setText(String.valueOf(Radius));
                break;

            case R.id.ThresholdSeekBar:
                Threshold = seekBar.getProgress();
                ThresholdText.setText(String.valueOf(Threshold));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
         }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public int[] gaussBlur(int[] scl, int[] tcl, int w, int h, int r){
        float[] bxs;
        bxs = boxesForGauss(r, 3);
        tcl =  boxBlur_2 (scl, tcl, w, h, (int)(bxs[0]-1)/2);
        tcl = boxBlur_2 (tcl, scl, w, h, (int)(bxs[1]-1)/2);
        tcl = boxBlur_2 (scl, tcl, w, h, (int)(bxs[2]-1)/2);
        return tcl;
    }

    public float[] boxesForGauss(float sigma,int n)
    {
        float wIdeal = (float)Math.sqrt((12*sigma*sigma/n)+1);
        float wl = (float)Math.floor(wIdeal);  if(wl%2==0) wl--;
        float wu = wl+2;

        float mIdeal = (12*sigma*sigma - n*wl*wl - 4*n*wl - 3*n)/(-4*wl - 4);
        int m = Math.round(mIdeal);

        float[] sizes = new float[n];
        for(int i=0; i<n; i++) sizes[i] = i<m?wl:wu;
        return sizes;
    }

    public int[] boxBlur_2 (int[] scl, int[] tcl,int w,int h,int r) {
        for (int i = 0; i <h; i ++)
        for (int j = 0; j <w; j ++) {
            int val = 0;
            for (int iy = i-r; iy <i + r + 1; iy ++)
                for (int ix = j-r; ix <j + r + 1; ix ++) {
                int x = Math.min (w-1, Math.max (0, ix));
                int y = Math.min (h-1, Math.max (0, iy));
                val += scl [y * w + x];
            }
            tcl [i * w + j] = val / ((r + r + 1) * (r + r + 1));
        }
        return tcl;
    }

    //Ненужная функция , но пусть она тут будет, поскольку я над ней запарился
    private int[] ChangeContrast(Bitmap imageBitmap, int k){
       int[] mas_o = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
       int[] mas_f = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        imageBitmap.getPixels(mas_o,0,imageBitmap.getWidth(),0,0,imageBitmap.getWidth(),imageBitmap.getHeight());
        int RedVal, GreenVal,BlueVal;
        double Pixel;
        double Contrast = (100.0 + k)/100.0;
        Contrast = Contrast*Contrast;

        for(int i = 0; i < imageBitmap.getWidth()*imageBitmap.getHeight(); i++){
            BlueVal =  mas_o[i]&0xff;
            GreenVal = (mas_o[i]&0xff00)>> 8;
            RedVal = (mas_o[i]&0xff0000)>> 16;

            Pixel = RedVal/255.0;
            Pixel = Pixel -0.5;
            Pixel = Pixel*Contrast;
            Pixel = Pixel + 0.5;
            Pixel = Pixel*255;
            if(Pixel < 0)
                Pixel = 0;
            if(Pixel > 255)
                Pixel = 255;
            RedVal = (byte)Pixel;

            Pixel = GreenVal/255.0;
            Pixel = Pixel -0.5;
            Pixel = Pixel*Contrast;
            Pixel = Pixel + 0.5;
            Pixel = Pixel*255;
            if(Pixel < 0)
                Pixel = 0;
            if(Pixel > 255)
                Pixel = 255;
            GreenVal = (byte)Pixel;

            Pixel = BlueVal/255.0;
            Pixel = Pixel -0.5;
            Pixel = Pixel*Contrast;
            Pixel = Pixel + 0.5;
            Pixel = Pixel*255;
            if(Pixel < 0)
                Pixel = 0;
            if(Pixel > 255)
                Pixel = 255;
            BlueVal = (byte)Pixel;

            mas_f[i] = 0xff000000 | (RedVal << 16) |  (GreenVal << 8) | (BlueVal);
        }
        return mas_f;
    }

    private int[] GaussianBlur(Bitmap imageBitmap, int r){
        int[] mas_o = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        int[] mas_f = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        int[] mas_blue_o = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        int[] mas_green_o = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        int[] mas_red_o = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        int[] mas_blue_f = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        int[] mas_green_f = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        int[] mas_red_f = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];

        imageBitmap.getPixels(mas_o,0,imageBitmap.getWidth(),0,0,imageBitmap.getWidth(),imageBitmap.getHeight());
        for(int i = 0; i < imageBitmap.getHeight()*imageBitmap.getWidth(); i++){
            mas_blue_o[i] = mas_o[i]&0xff;

        }
        for(int i = 0; i < imageBitmap.getHeight()*imageBitmap.getWidth(); i++){
            mas_green_o[i] = (mas_o[i]&0xff00)>>8;
        }
        for(int i = 0; i < imageBitmap.getHeight()*imageBitmap.getWidth(); i++){
            mas_red_o[i] = (mas_o[i]&0xff0000)>>16;
        }

        mas_blue_f = gaussBlur(mas_blue_o,mas_blue_f,imageBitmap.getWidth(),imageBitmap.getHeight(),r);
        mas_green_f = gaussBlur(mas_green_o,mas_green_f,imageBitmap.getWidth(),imageBitmap.getHeight(),r);
        mas_red_f = gaussBlur(mas_red_o,mas_red_f,imageBitmap.getWidth(),imageBitmap.getHeight(),r);

        for(int i = 0; i < imageBitmap.getHeight()*imageBitmap.getWidth(); i++){
            mas_f[i] = 0xff000000 | (mas_red_f[i] << 16) |  (mas_green_f[i] << 8) | (mas_blue_f[i]);
        }
        return mas_f;
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

    class UnsharpTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           LoadinText.setText("Loading...");
           LoadingProgres.setVisibility(ProgressBar.VISIBLE);
           Cancel.setVisibility(View.INVISIBLE);
           Apply.setVisibility(View.INVISIBLE);
           UnsharpMaskButton.setVisibility(View.INVISIBLE);
           gaussianButton.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap imageBitmap = ((BitmapDrawable)resultImage.getDrawable()).getBitmap();

            int[] mas_o = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
            imageBitmap.getPixels(mas_o,0,imageBitmap.getWidth(),0,0,imageBitmap.getWidth(),imageBitmap.getHeight());
            int[]  mas_gause = GaussianBlur(imageBitmap,Radius);
            int[] mas_f = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
            mas_f = mas_o;


            for(int i = 0; i < imageBitmap.getWidth()*imageBitmap.getHeight(); i++){
                if(mas_o[i] - mas_gause[i] > Threshold)
                    mas_f[i] = mas_f[i] + (mas_o[i] - mas_gause[i]);
            }

            Bitmap newBitMap = Bitmap.createBitmap(imageBitmap.getWidth(),imageBitmap.getHeight(), imageBitmap.getConfig());
            newBitMap.setPixels(mas_f,0,imageBitmap.getWidth(),0,0,imageBitmap.getWidth(),imageBitmap.getHeight());
            // Отобразим изменения

            return newBitMap;
        }

        @Override
        protected void onPostExecute(Bitmap newBitmap) {
            super.onPostExecute(newBitmap);
            LoadinText.setText("");
            LoadingProgres.setVisibility(ProgressBar.INVISIBLE);
            Cancel.setVisibility(View.VISIBLE);
            Apply.setVisibility(View.VISIBLE);
            UnsharpMaskButton.setVisibility(View.VISIBLE);
            gaussianButton.setVisibility(View.VISIBLE);
            resultImage.setImageBitmap(newBitmap);
            try {
                    resultImageURI = bitmapToUriConverter(newBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
