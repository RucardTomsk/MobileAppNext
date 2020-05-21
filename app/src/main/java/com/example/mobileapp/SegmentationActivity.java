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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class WaterShed {

    static class Point {
        int x;
        int y;
    }

    private static int[] Watershed(int[] OriginalImage, int[] SeedImage,
                                   int[] LabelImage, int width, int height) {
        int Num = 0;

        ArrayList<ArrayList<ArrayList<Point>>> qu = new ArrayList<>();
        Point temp = new Point();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                LabelImage[y * width + x] = 0;
            }
        }

        int up, down, right, left, upleft, upright, downleft, downright;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (SeedImage[y * width + x] == 1 || SeedImage[y * width + x] == 255)
                {
                    Num++;
                    ArrayList<ArrayList<Point>> uu = new ArrayList<>(
                            256);

                    ArrayList<Point> que = new ArrayList<>();
                    for (int l = 0; l < 256; l++) {
                        uu.add(new ArrayList<Point>());
                    }
                    qu.add(uu);
                    temp.x = x;
                    temp.y = y;
                    que.add(temp);
                    LabelImage[y * width + x] = Num;
                    SeedImage[y * width + x] = 127;

                    while (!que.isEmpty()) {
                        up = down = right = left = 0;
                        upleft = upright = downleft = downright = 0;
                        temp = que.remove(0);
                        int m = temp.x;
                        int n = temp.y;

                        if (m > 0) {
                            if (SeedImage[n * width + m - 1] == 1) {
                                Point point = new Point();
                                point.x = m - 1;
                                point.y = n;
                                que.add(point);
                                LabelImage[n * width + m - 1] = Num;
                                SeedImage[n * width + m - 1] = 127;
                            } else {
                                up = 1;
                            }
                        }
                        if (m > 0 && n > 0) {
                            if (SeedImage[(n - 1) * width + m - 1] == 1) {
                                Point point = new Point();
                                point.x = m - 1;
                                point.y = n - 1;
                                que.add(point);
                                LabelImage[(n - 1) * width + m - 1] = Num;
                                SeedImage[(n - 1) * width + m - 1] = 127;
                            } else {
                                upleft = 1;
                            }
                        }

                        if (m < width - 1) {
                            if (SeedImage[n * width + m + 1] == 1) {
                                Point point = new Point();
                                point.x = m + 1;
                                point.y = n;
                                que.add(point);
                                LabelImage[n * width + m + 1] = Num;
                                SeedImage[n * width + m + 1] = 127;
                            } else {
                                down = 1;
                            }
                        }
                        if (m < (width - 1) && n < (height - 1)) {
                            if (SeedImage[(n + 1) * width + m + 1] == 1) {
                                Point point = new Point();
                                point.x = m + 1;
                                point.y = n + 1;
                                que.add(point);
                                LabelImage[(n + 1) * width + m + 1] = Num;
                                SeedImage[(n + 1) * width + m + 1] = 127;
                            } else {
                                downright = 1;
                            }
                        }

                        if (n < height - 1) {
                            if (SeedImage[(n + 1) * width + m] == 1) {
                                Point point = new Point();
                                point.x = m;
                                point.y = n + 1;
                                que.add(point);
                                LabelImage[(n + 1) * width + m] = Num;
                                SeedImage[(n + 1) * width + m] = 127;
                            } else {
                                right = 1;
                            }
                        }
                        if (m > 0 && n < (height - 1)) {
                            if (SeedImage[(n + 1) * width + m - 1] == 1) {
                                Point point = new Point();
                                point.x = m - 1;
                                point.y = n + 1;
                                que.add(point);
                                LabelImage[(n + 1) * width + m - 1] = Num;
                                SeedImage[(n + 1) * width + m - 1] = 127;
                            } else {
                                upright = 1;
                            }
                        }

                        if (n > 0) {
                            if (SeedImage[(n - 1) * width + m] == 1) {
                                Point point = new Point();
                                point.x = m;
                                point.y = n - 1;
                                que.add(point);
                                LabelImage[(n - 1) * width + m] = Num;
                                SeedImage[(n - 1) * width + m] = 127;
                            } else {
                                left = 1;
                            }
                        }
                        if (m < (width - 1) && n > 0) {
                            if (SeedImage[(n - 1) * width + m + 1] == 1) {
                                Point point = new Point();
                                point.x = m + 1;
                                point.y = n - 1;
                                que.add(point);
                                LabelImage[(n - 1) * width + m + 1] = Num;
                                SeedImage[(n - 1) * width + m + 1] = 127;
                            } else {
                                downleft = 1;
                            }
                        }

                        if (up != 0 || down != 0 || right != 0 || left != 0
                                || upleft != 0 || downleft != 0 || upright != 0
                                || downright != 0) {
                            Point point = new Point();
                            point.x = m;
                            point.y = n;
                            qu.get(Num - 1).get(OriginalImage[n * width + m]).add(point);
                        }
                    }
                }
            }
        }
        boolean actives;
        for (int WaterLevel = 1; WaterLevel < 255; WaterLevel++) {
            actives = true;
            while (actives) {
                actives = false;
                for (int x = 0; x < Num; x++) {
                    if (!qu.get(x).get(WaterLevel).isEmpty()) {
                        actives = true;
                        while (qu.get(x).get(WaterLevel).size() > 0) {
                            temp = qu.get(x).get(WaterLevel).remove(0);
                            int m = temp.x;
                            int n = temp.y;
                            if (m > 0) {
                                if (LabelImage[n * width + m - 1] == 0) {
                                    Point point = new Point();
                                    point.x = m - 1;
                                    point.y = n;
                                    LabelImage[n * width + m - 1] = x + 1;

                                    if (OriginalImage[n * width + m - 1] <= WaterLevel) {
                                        qu.get(x).get(WaterLevel).add(point);
                                    } else {
                                        qu.get(x).get(OriginalImage[n * width + m - 1])
                                                .add(point);
                                    }
                                }
                            }

                            if (m < width - 1) {
                                if (LabelImage[n * width + m + 1] == 0) {
                                    Point point = new Point();
                                    point.x = m + 1;
                                    point.y = n;
                                    LabelImage[n * width + m + 1] = x + 1;

                                    if (OriginalImage[n * width + m + 1] <= WaterLevel) {
                                        qu.get(x).get(WaterLevel).add(point);
                                    } else {
                                        qu.get(x).get(OriginalImage[n * width + m + 1])
                                                .add(point);
                                    }
                                }
                            }

                            if (n < height - 1) {
                                if (LabelImage[(n + 1) * width + m] == 0) {
                                    Point point = new Point();
                                    point.x = m;
                                    point.y = n + 1;
                                    LabelImage[(n + 1) * width + m] = x + 1;

                                    if (OriginalImage[(n + 1) * width + m] <= WaterLevel) {
                                        qu.get(x).get(WaterLevel).add(point);
                                    } else {
                                        qu.get(x).get(OriginalImage[(n + 1) * width + m])
                                                .add(point);
                                    }
                                }
                            }

                            if (n > 0) {
                                if (LabelImage[(n - 1) * width + m] == 0) {
                                    Point point = new Point();
                                    point.x = m;
                                    point.y = n - 1;
                                    LabelImage[(n - 1) * width + m] = x + 1;

                                    if (OriginalImage[(n - 1) * width + m] <= WaterLevel) {
                                        qu.get(x).get(WaterLevel).add(point);
                                    } else {
                                        qu.get(x).get(OriginalImage[(n - 1) * width + m])
                                                .add(point);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return LabelImage;
    }

    public static Bitmap getImageData(ImageView image) {
        Bitmap imageBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        Bitmap resultBitmap = Bitmap.createBitmap(imageBitmap.getWidth(),
                imageBitmap.getHeight(), imageBitmap.getConfig());

        int[] originalImage = new int[width * height];
        imageBitmap.getPixels(originalImage, 0, width, 0, 0, width, height);
        int[] resultImage = new int[width * height];

        int[] SeedImage = new int[width * height];
        int[] LabelImage = new int[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                int pixel = originalImage[y * width + x];
                int pixelRed = Color.red(pixel);
                int pixelGreen = Color.green(pixel);
                int pixelBlue = Color.blue(pixel);

                originalImage[y * width + x] = (pixelRed + pixelGreen + pixelBlue) / 3;

                if ((pixelRed == 0) && (pixelGreen == 0) && (pixelBlue == 255)) {
                    SeedImage[y * width + height] = 1;
                    resultImage[y * width + height] = Color.argb(Color.alpha(pixel),
                            255, 255, 255);
                } else {
                    resultImage[y * width + x] = Color.argb(Color.alpha(pixel),
                            0, 0, 0);
                }
            }
        }

        int[] result = WaterShed.Watershed(originalImage, SeedImage, LabelImage, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                resultImage[y * width + x] = 255 << (LabelImage[y * width + x] * 4);
            }
        }

        resultBitmap.setPixels(resultImage, 0, width, 0, 0, width, height);
        return resultBitmap;
    }
}

public class SegmentationActivity extends AppCompatActivity implements View.OnClickListener {

    // Изображение до наложения ретуши
    private Uri originalImageURI;
    // Изображение после наложения ретуши
    private Uri resultImageURI;

    // Кнопки для отмены или принятия изменений
    Button cancel;
    Button apply;
    Button segment;

    // Изображение
    ImageView resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segmentation);

        // Получить изображение
        originalImageURI = getIntent().getParcelableExtra("original");
        resultImageURI = originalImageURI;

        resultImage = findViewById(R.id.image);
        resultImage.setImageURI(resultImageURI);

        cancel = findViewById(R.id.cancelButton);
        cancel.setOnClickListener(this);

        apply = findViewById(R.id.applyButton);
        apply.setOnClickListener(this);

        segment = findViewById(R.id.segmentButton);
        segment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Применить изменения и выйти в редактор
            case R.id.applyButton:
                Intent callEditorIntent = new Intent(SegmentationActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", resultImageURI);
                startActivity(callEditorIntent);
                break;

            // Отменить изменения и выйти в редактор
            case R.id.cancelButton:
                callEditorIntent = new Intent(SegmentationActivity.this,
                        GraphicsEditorActivity.class);

                callEditorIntent.putExtra("result", originalImageURI);
                startActivity(callEditorIntent);
                break;

            case R.id.segmentButton:
                applyWatershed();

            default:
                break;
        }
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

    private void applyWatershed() {
        Bitmap resultBitmap = WaterShed.getImageData(resultImage);
        resultImage.setImageBitmap(resultBitmap);
        try {
            resultImageURI = bitmapToUriConverter(resultBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
