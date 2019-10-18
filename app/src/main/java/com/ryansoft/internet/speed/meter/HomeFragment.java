package com.ryansoft.internet.speed.meter;


import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeFragment extends BaseFragment {
    boolean isStarted = true;
    View view;
    TextView totalSpeed;
    TextView receive;
    TextView send;
    private AdView adView;

    long exRX, exTX;
    long nowRX, nowTX;
    double rxBPS, txBPS;

    public double drx = 10, dtx = 10, dall;

    public String speed = "0 Kb/s", down = "Download 0 Kb/s",
            up = "Upload 0 Kb/s";

    int flagCount = 0;
    double brx = 0, btx = 0;

    @Override
    public void onDestroy() {
        isStarted = true;
        super.onDestroy();
    }

    float[][] chartArray = new float[3][21];

    //

    TextView wr, ws, mr, ms, tr, ts;
    private PieChartView chart;
    private PieChartData data;
    private boolean hasLabelsOutside = false;
    //
    private LineChartView linechart;
    private LineChartData linedata;
    private int maxNumberOfLines = 3;
    private int numberOfPoints = 20;

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStart() {
        if (isStarted) {
            wr = (TextView) view.findViewById(R.id.wr);
            ws = (TextView) view.findViewById(R.id.ws);
            mr = (TextView) view.findViewById(R.id.dr);
            ms = (TextView) view.findViewById(R.id.ds);
            tr = (TextView) view.findViewById(R.id.tr);
            ts = (TextView) view.findViewById(R.id.ts);
            totalSpeed = (TextView) view.findViewById(R.id.total_speed);
            receive = (TextView) view.findViewById(R.id.receive);
            send = (TextView) view.findViewById(R.id.send);
            chart = (PieChartView) view.findViewById(R.id.pieChartView1);
            linechart = (LineChartView) view.findViewById(R.id.line_chart_total);
            adView = (AdView) view.findViewById(R.id.adView);


            loadSpeed();
            isStarted = false;
        }

        if (showAds()) {
            runAds(adView);
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    public void loadSpeed() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        if (isNetworkAvailable()) {


                            brx = drx;
                            btx = dtx;
                            new getData().execute();
                            flagCount = 0;

                        } else {
                            flagCount++;
                        }


                    }
                }, 0, 1, TimeUnit.SECONDS);
    }


    private String setTrance(double a, double b) {
        double c = Math.abs(a - b);

        return calculateData2((int) c);

    }

    private String calculateData2(int a) {
        String res;

        if (a / 1024 >= 1) {
            if ((a / 1000) / 1048576 >= 1) {
                res = Math.abs((int) (a / 1024) / 1048576) + " GB";
            } else {
                if ((a / 1024) / 1024 >= 1) {
                    res = Math.abs((int) (a / 1024) / 1024) + " MB";
                } else {
                    res = Math.abs((int) a / 1024) + " KB";
                }
            }

        } else {
            res = Math.abs((int) a) + " Byte";
        }
        return res;

    }

    public void lineData(float a, int flag) {
        NumberFormat n = NumberFormat.getInstance();
        if (a == 0) {
            if (chartArray[flag][18] == 0) {
                a = (float) 0.1;
            }
        }
        float res;
        if (a / 1000 >= 1) {

            res = (int) a / 1000;

        } else {
            try {
                res = a / 1000;
                n.setMaximumFractionDigits(1);
                res = Float.parseFloat(n.format(res));
            } catch (Exception e) {
                res = a / 1000;
            }

        }
        float[] tempArry = new float[20];
        for (int i = 0; i < 20; i++) {
            if (i == 0) {
                if (res == 0) {
                    if (chartArray[flag][18] == 0) {
                        res = (float) 0.1;
                    }
                }
                tempArry[i] = res;
            } else {
                tempArry[i] = chartArray[flag][i - 1];

            }

        }
        for (int i = 0; i < 20; i++) {
            chartArray[flag][i] = tempArry[i];
        }

    }

    private boolean chartFlag = true;

    private void generatelineData() {

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < 1; ++i) {
            List<PointValue> values = new ArrayList<PointValue>();

            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);

            switch (i) {
                case 0:
                    // line.setStrokeWidth(4);
                    line.setColor(ChartUtils.COLOR_GREEN);
                    break;
                case 2:
                    line.setColor(ChartUtils.COLOR_ORANGE);
                    break;
                case 3:
                    line.setColor(ChartUtils.COLOR_RED);
                    break;

                default:
                    line.setColor(ChartUtils.COLOR_RED);
                    break;
            }

            line.setShape(ValueShape.CIRCLE);
            line.setCubic(false);
            line.setFilled(false);
            line.setHasLabels(false);
            line.setHasLabelsOnlyForSelected(false);
            line.setHasLines(true);
            line.setHasPoints(false);
            lines.add(line);
        }

        linedata = new LineChartData(lines);

        if (hasAxes) {
            Axis axisY = new Axis().setHasLines(true);
            Axis axisX = new Axis().setHasLines(true);
            // axisY.generateAxisFromRange(0,1000,5);

            axisY.setMaxLabelChars(5);
            // axisY.setAutoGenerated(true);
            if (hasAxesNames) {
                // axisX.setName("Axis X");

            }
            linedata.setAxisXBottom(axisX);
            linedata.setAxisYLeft(axisY);
            if (isAdded()) {
                axisY.setName(getResources().getString(R.string.axisY));
            }
        } else {
            // linedata.setAxisXBottom(null);
            linedata.setAxisYLeft(null);
        }

        linedata.setBaseValue(Integer.MAX_VALUE);
        linechart.setLineChartData(linedata);

    }

    private void generateValues() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = chartArray[i][j];
            }
        }
    }

    private void generateData(boolean flag) {
        double a;
        double b;
        a = dtx;
        b = drx;
        if (flag) {
            a = 1;
            b = 1;
        }

        if (a == 0 && b == 0) {
            a = 1;
            b = 1;
        }
        List<SliceValue> values = new ArrayList<SliceValue>();

        SliceValue sliceValue = new SliceValue((float) a, ChartUtils.COLOR_RED);
        values.add(sliceValue);
        SliceValue sliceValue2 = new SliceValue((float) b, ChartUtils.COLOR_ORANGE);
        values.add(sliceValue2);

        data = new PieChartData(values);
        data.setHasLabels(false);
        data.setHasLabelsOnlyForSelected(false);
        data.setHasLabelsOutside(false);
        data.setHasCenterCircle(true);

        chart.setPieChartData(data);
    }

    boolean loopFirst = true;

    private class getData extends AsyncTask<Boolean, Void, String> {


        @Override
        protected void onPreExecute() {


            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            showResult();

            if (loopFirst) {
                lineData((float) 0, 0);
                loopFirst = false;
            } else {
                lineData((float) dall, 0);
            }


            wr.setText("R "
                    + setTrance(Data.wifirec, TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()));
            ws.setText("S "
                    + setTrance(Data.wifisend, TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()));
            mr.setText("R "
                    + setTrance(TrafficStats.getMobileRxBytes(), Data.mobilerec));
            ms.setText("S "
                    + setTrance(TrafficStats.getMobileTxBytes(), Data.mobilesend));
            tr.setText("R "
                    + setTrance(TrafficStats.getTotalRxBytes(), Data.totalrec));
            ts.setText("S "
                    + setTrance(TrafficStats.getTotalTxBytes(), Data.totalsend));

            generatelineData();
            generateData(false);

            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Boolean... booleans) {

            String s, d, u;
            double rxDiff = 0, txDiff = 0;
            nowTX = TrafficStats.getTotalTxBytes();
            nowRX = TrafficStats.getTotalRxBytes();

            rxDiff = nowRX - exRX;
            txDiff = nowTX - exTX;


            rxBPS = (rxDiff / (1000 / 1000));
            txBPS = (txDiff / (1000 / 1000));

            exRX = nowRX;
            exTX = nowTX;
            s = calculateData(txBPS + rxBPS);
            d = calculateData(rxBPS);
            u = calculateData(txBPS);


            setData(s, d, u, txBPS + rxBPS, rxBPS, txBPS);

            generateValues();
            return null;
        }

    }

    private String calculateData(double a) {
        String res;
        try {
            NumberFormat n = NumberFormat.getInstance();
            n.setMaximumFractionDigits(1);

            if (a / 1000 >= 1) {
                if ((a / 1000) / 1000 >= 1) {
                    res = n.format((a / 1000) / 1000) + " "
                            + getResources().getString(R.string.mb);
                } else {
                    res = (int) a / 1000 + " "
                            + getResources().getString(R.string.kb);
                }

            } else {
                res = (int) a + " " + getResources().getString(R.string.b);
            }
            return res;
        } catch (Exception e) {
            if (a / 1000 >= 1) {
                if ((a / 1000) / 1000 >= 1) {
                    res = (int) ((a / 1000) / 1000) + " "
                            + getResources().getString(R.string.mb);
                } else {
                    res = (int) a / 1000 + " "
                            + getResources().getString(R.string.kb);
                }

            } else {
                res = (int) a + " " + getResources().getString(R.string.b);
            }
            return res;
        }

    }

    public void setData(String a, String b, String c, double all,
                        double rx, double tx) {
        speed = a;
        down = b;
        up = c;
        dall = all;
        drx = rx;
        dtx = tx;

    }

    public void showResult() {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (isAdded()) {
                    totalSpeed.setText(speed);
                    receive.setText(getResources().getString(R.string.download) + ": "
                            + down);
                    send.setText(getResources().getString(R.string.upload) + ": " + up);
                }
            }
        };
        handler.sendEmptyMessage(1);
    }


}
