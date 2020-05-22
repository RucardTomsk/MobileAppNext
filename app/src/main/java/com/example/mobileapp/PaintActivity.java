package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.IOException;
import java.util.Vector;

import static java.lang.StrictMath.abs;

public class PaintActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    ImageView rezultImage;
    Bitmap newBitMap;
    Button build;
    Button flatten;
    Button cancel;
    GraphView graphView;


    public static class V{
        float X;
        float Y;
    }

    Vector mas = new Vector<V>(0,1);
    float X,Y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        build = findViewById(R.id.BuildButton);
        build.setOnClickListener(this);

        flatten = findViewById(R.id.FlattenButton);
        flatten.setOnClickListener(this);

        cancel = findViewById(R.id.CancelButton);
        cancel.setOnClickListener(this);

        graphView = (GraphView) findViewById(R.id.graph);
        graphView.setOnTouchListener(this);

        // set manual X bounds
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(10);

        // set manual Y bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(10);

    }

    public float f(float a, float b, float x){
        return a*x+b;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.BuildButton:
                V mas_3[] = new V[mas.capacity()];


                for(int i = 0; i < mas.capacity(); i++)
                    mas_3[i] = (V)mas.get(i);

                for(int i = 0; i < mas_3.length; i++){
                    for(int g = 0; g < mas_3.length-1; g++){
                        if(mas_3[g].X > mas_3[g+1].X){
                            V tmp = mas_3[g];
                            mas_3[g] = mas_3[g+1];
                            mas_3[g+1] = tmp;
                        }
                    }
                }

                for (int i = 0; i < mas_3.length - 1; i++) {
                    V N1 = (V) mas_3[i];
                    V N2 = (V) mas_3[i + 1];

                    float a = (N2.Y - N1.Y) / (N2.X - N1.X);
                    float b = N1.Y - a * N1.X;
                    for (float g = (float) (N1.X + 0.01); g < (float) (N2.X - 0.01); g = (float) (g + 0.01)) {
                        DataPoint k = new DataPoint(g, f(a, b, g));

                        PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<>(new DataPoint[]{
                                k
                        });

                        graphView.addSeries(series2);
                        series2.setShape(PointsGraphSeries.Shape.POINT);
                        series2.setColor(Color.WHITE);
                        series2.setSize(4);
                    }
                }
                break;
            case R.id.FlattenButton:
                float minY = 0;
                float maxY = 10;
               V mas_2[] = new V[mas.capacity()];


               for(int i = 0; i < mas.capacity(); i++)
                   mas_2[i] = (V)mas.get(i);

               for(int i = 0; i < mas_2.length; i++){
                   for(int g = 0; g < mas_2.length-1; g++){
                       if(mas_2[g].X > mas_2[g+1].X){
                           V tmp = mas_2[g];
                           mas_2[g] = mas_2[g+1];
                           mas_2[g+1] = tmp;
                       }
                   }
               }

               for(float i = mas_2[0].X + 0.005f; i < mas_2[mas.capacity()-1].X - 0.005f; i = i + 0.005f){
                   float y = lagrange(mas_2, mas.capacity(), i);
                   DataPoint k = new DataPoint(i, y);
                    if(y > maxY){
                        maxY = y;
                    }
                    if(y < minY){
                        minY = y;
                    }

                   PointsGraphSeries<DataPoint> series3 = new PointsGraphSeries<>(new DataPoint[]{
                        k
                   });

                   graphView.addSeries(series3);
                   series3.setShape(PointsGraphSeries.Shape.POINT);
                   series3.setColor(Color.RED);
                   series3.setSize(4);
               }
                graphView.getViewport().setMinY(minY);
                graphView.getViewport().setMaxY(maxY);

                break;

            case R.id.CancelButton:
                final Intent callPaintIntent = new Intent(PaintActivity.this,MainActivity.class);
                startActivity(callPaintIntent);
            break;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
            X = event.getX();
            Y = event.getY();
        switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:

                    V id = new V();
                    id.X = (float)((X/100)-0.25);
                    id.Y = (float)((10-(Y/100)-0.25));
                    mas.add(id);
                    DataPoint a = new DataPoint((X/100)-0.25,(10-(Y/100)-0.25));

                    PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(new DataPoint[] {
                            a
                    });

                    graphView.addSeries(series);
                    series.setShape(PointsGraphSeries.Shape.POINT);
                    series.setColor(Color.BLACK);
                    break;
            }
        return true;
    }

    public float lagrange(V mas[], int n, double _x){

        float result = 0.0f;

        for(int i = 0; i < n; i++){
            double P = 1.0f;
            for(int j = 0; j < n; j++)
                if(i!= j)
                    P *= (_x- mas[j].X)/(mas[i].X-mas[j].X);

                result += P*mas[i].Y;
        }
        return result;
    }

}
