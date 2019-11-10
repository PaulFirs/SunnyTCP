package com.example.paulfirs.sunny.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.example.paulfirs.sunny.R;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Graphics extends Fragment {
    private final static String TAG = "myLogs";
    GraphView graph;
    FrameLayout layout;
    String chooseFile;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //add this line to make the options appear in your Toolbar
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.file_data, container, false);

        layout = (FrameLayout) rootView.findViewById(R.id.layoutGraph);

        createGraph();

        //outputDataFile = (TextView) rootView.findViewById(R.id.outputDataFile);
        return rootView;
    }


    public void createGraph() {
        layout.removeAllViews();

        graph = new GraphView(getContext());
        graph.setRotation(90);
        //изменение ориентации графика, не изменяя ориентацию экрана (спасибо корейцу "Think Twice Code Once")
        graph.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {

                        graph.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int offset = (graph.getHeight() - graph.getWidth()) / 2;

                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) graph.getLayoutParams();
                        layoutParams.width = graph.getHeight();
                        layoutParams.height = graph.getWidth();
                        graph.setLayoutParams(layoutParams);

                        graph.setTranslationX(-offset);
                        graph.setTranslationY(offset);

                        //initLineChart();
                    }
                });
        layout.addView(graph, 0);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Graphics onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Graphics onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Graphics onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Graphics onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Graphics onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Graphics onDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Graphics onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Graphics onDetach");
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_file, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final String[] files = getContext().fileList();
        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_file) {
            layout.removeAllViews();//очистка окна
            getContext().deleteFile(chooseFile);
            return true;
        }else if (id == R.id.delete_all_file) {
            layout.removeAllViews();
            for(int i = 0; i < files.length; i++)
                getContext().deleteFile(files[i]);
            return true;
        }else if (id == R.id.choose_file) {
// setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose a date");

// add a list
            builder.setItems(files, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //WorkActivity.showToast(files[which]);
                    chooseFile = files[which];
                    readFile(chooseFile);
                }
            });

// create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



/*
читение содержимого

В массив subStr помещаются данные:
0 элемент - дд.мм.гг
1 элемент - чч.мм.сс
2 элемент - ppm

Добавлять в координату точки тип String нельзя, вылетит ошибка.
Тип Date можно, но он преобразуется в double.
Из-за этого приходится применять DefaultLabelFormatter и там возвращать его в Date.

1 элемент преобразуется в формат Date и помещается в переменную d
добавляется новая точка с координатами (d, 2 элемент)

*/

    public void readFile(String fileName) {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    getContext().openFileInput(fileName)));//открывается файл
            String str;//строка, которая считывается


            List<DataPoint> points = new ArrayList<DataPoint>();//лист для дочек графика
            while ((str = br.readLine()) != null) {//чтение текущей строки
                String[] subStr;//строка для данных разделенных "-" (дд.мм.гг-чч.мм.сс-ppm)
                subStr = str.split("-"); // Разделения строки str с помощью метода split()
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); //объект для форматирования
                Date oX = null;

                try {
                    oX = dateFormat.parse(subStr[0]);//преобразование String to date
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                points.add(new DataPoint(oX, Integer.parseInt(subStr[1])));//запись координат точек в лист (x - время, y - ppm)

            }

            DataPoint[] pointsArr = new DataPoint[points.size()];//массив размера листа. Т.к. функция построения графика принимает только массив

            // ArrayList to Array Conversion
            for (int i =0; i < points.size(); i++)//заполнение массива координатами
                pointsArr[i] = points.get(i);

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(pointsArr);//присвоение графику координат
            createGraph();
            graph.addSeries(series);//построение графика
            //Изменение чисел на время оси ОХ
            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){//изменение лейбла у оси X
                @Override
                public String formatLabel(double value, boolean isValueX){
                    if(isValueX){
                        return dateFormat.format(new Date((long) value));
                    }else{
                        return super.formatLabel(value, isValueX);
                    }
                }
            });
            graph.setTitle(fileName);
            graph.getViewport().setScalable(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
