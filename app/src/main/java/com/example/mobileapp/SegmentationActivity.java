package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class OpenCvUtil {

    private static CascadeClassifier initOpenCV(Context context) {
        CascadeClassifier cascadeClassifier = new CascadeClassifier();

        try {
            InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);


            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            // Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("SegmentationActivity", "Error loading cascade", e);
        }

        return cascadeClassifier;
    }

    public static Bitmap detectFace(Bitmap imageBitmap, Context context) {
        CascadeClassifier faceDetector  = initOpenCV(context);

        // get image
        Mat image = new Mat();
        Utils.bitmapToMat(imageBitmap, image);

        // Detecting faces
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        // Creating a rectangular box showing faces detected
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }

        Bitmap b = Bitmap.createBitmap(imageBitmap);
        Utils.matToBitmap(image, b);
        return b;
    }

    public static Bitmap detectShape(Bitmap imageBitmap) {

        //convert to Mat: represents an n-dimensional dense numerical single-channel or multi-channel array
        //single channel means each element in the matrix has only one value
        Mat originalMat = new Mat();
        Utils.bitmapToMat(imageBitmap, originalMat);

        Mat cannyEdges = new Mat(); //find edges in the photo
        Mat hierarchy = new Mat(); //Optional output vector, containing information about the image topology. #contours

        List<MatOfPoint> contourList = new ArrayList<>(); //A list to store all the contours

        // - An image gradient is a directional change in the intensity or color in an image.
        // - A threshold is a value which has two regions on its either side i.e.
        // below the threshold or above the threshold.
        // - Canny does use two thresholds (upper and lower): If a pixel gradient
        // is higher than the upper threshold, the pixel is accepted as an edge.
        // If a pixel gradient value is below the lower threshold, then it is rejected.
        Imgproc.Canny(originalMat, cannyEdges, 10, 100);

        //finding contours: a curve joining all the continuous points (along the boundary), having same color or intensity.
        Imgproc.findContours(cannyEdges, contourList, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0; i < contourList.size(); i++) {

            double epsilon = 0.1 * Imgproc.arcLength(new MatOfPoint2f(contourList.get(i).toArray()), true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contourList.get(i).toArray()), approx, epsilon, true);

            //Moments m = Imgproc.moments(contourList.get(i));//to get usefull info about the contour like area m.m00
            //get big enough contour to draw it, instead of getting very small contours that will make points in the more complex photos
            if (Imgproc.contourArea(contourList.get(i))> 1000.0) {//another way of getting the area {in pixel}
                switch ((int) approx.total()) {//total: Returns the total number of array elements.
                    case 5://pentagon
                    case 3://triangle
                    case 4://square
                    case 9://half-circle
                    case 15://circle
                        Imgproc.drawContours(originalMat, contourList, i, new Scalar(0, 0, 0), 30);
                }
            }
        }

        Bitmap b = Bitmap.createBitmap(imageBitmap);
        //Converting Mat back to Bitmap
        Utils.matToBitmap(originalMat, b);
        return b;
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
        OpenCVLoader.initDebug();

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
                // TODO call segmentation
                break;

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

    private void detectFace() {

    }
}
