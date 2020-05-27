package com.ryansoft.internet.speed.meter;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

public class MainActivity extends BaseActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    TextView totalSpeed;
    TextView receive;
    TextView send;

    long exRX, exTX;
    long nowRX, nowTX;
    double rxBPS, txBPS;

    public double drx = 10, dtx = 10, dall;

    public  String speed = "0 Kb/s", down = "Download 0 Kb/s",
            up = "Upload 0 Kb/s";

    int flagCount = 0;
    double brx = 0, btx = 0;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);

        wr = (TextView) this.findViewById(R.id.wr);
        ws = (TextView) this.findViewById(R.id.ws);
        mr = (TextView) this.findViewById(R.id.dr);
        ms = (TextView) this.findViewById(R.id.ds);
        tr = (TextView) this.findViewById(R.id.tr);
        ts = (TextView) this.findViewById(R.id.ts);
        totalSpeed = (TextView) findViewById(R.id.total_speed);
        receive = (TextView) findViewById(R.id.receive);
        send = (TextView) findViewById(R.id.send);
        chart = (PieChartView) findViewById(R.id.pieChartView1);
        linechart = (LineChartView) findViewById(R.id.line_chart_total);
//        Toast.makeText(this, android.net.TrafficStats.getMobileRxBytes()+"Bytes", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "main");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        loeadSpeed();
        AdView adView = (AdView) findViewById(R.id.adView);
        runAds(adView);

    }


    @Override
    protected void onDestroy() {
        Data.flag = false;
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        Data.flag = true;
        super.onResume();
    }
    @Override
    protected void onStop() {
        Data.flag = false;
        super.onStop();
    }
    public void loeadSpeed(){
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                if (isNetworkAvailable()) {

                    if (brx != drx || btx != dtx || flagCount >= 2) {
                        brx = drx;
                        btx = dtx;
                        new getData().execute();
                        flagCount = 0;

                    } else {
                        flagCount++;
                    }
                } else {
                    //todo threath exption
//                    totalSpeed.setText("0 KB/s");
//                    receive.setText("Download: 0 B/s");
//                    send.setText("Upload: 0 B/s");
                }

            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 999);
    }

    private class syncData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... arg0) {

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            totalSpeed.setText("0 KB/s");
            receive.setText("Download: 0 B/s");
            send.setText("Upload: 0 B/s");
            //  generateData(true);
            super.onPostExecute(result);
        }

    }

    private String settranc(double a, double b) {
        double c = a - b;

        return calculateData2((int) c);

    }

    private String calculateData2(int a) {
        String res;

        if (a / 1000 >= 1) {
            if ((a / 1000) / 1000000 >= 1) {
                res = Math.abs((int) (a / 1000) / 1000000) + " Gb";
            } else {
                if ((a / 1000) / 1000 >= 1) {
                    res = Math.abs((int) (a / 1000) / 1000) + " Mb";
                } else {
                    res = Math.abs((int) a / 1000) + " Kb";
                }
            }

        } else {
            res = Math.abs((int) a) + " Byte";
        }
        return res;

    }

    public void generatarray() {
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i <= 20; i++) {
                chartArray[j][i] = (float) 0;

            }
        }

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

        }

        else {
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
            axisY.setName(getResources().getString(R.string.axisY));

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
        SliceValue sliceValue2 = new SliceValue((float) b,
                ChartUtils.COLOR_ORANGE);
        values.add(sliceValue2);

        data = new PieChartData(values);
        data.setHasLabels(false);
        data.setHasLabelsOnlyForSelected(false);
        data.setHasLabelsOutside(false);
        data.setHasCenterCircle(false);

        chart.setPieChartData(data);
    }

    private class getData extends AsyncTask<String, Void, String> {

        String a = "ali", b = "ali", c = "ali";

        @Override
        protected void onPreExecute() {
            lineData((float) dall, 0);
            lineData((float) drx, 1);
            lineData((float) dtx, 2);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            showResult();

            wr.setText("R "
                    + settranc(Data.wifirec, TrafficStats.getMobileRxBytes()
                    - TrafficStats.getTotalRxBytes()));
            ws.setText("S "
                    + settranc(Data.wifisend, TrafficStats.getMobileTxBytes()
                    - TrafficStats.getTotalTxBytes()));
            mr.setText("R "
                    + settranc( TrafficStats.getMobileRxBytes(),Data.mobilerec));
            ms.setText("S "
                    + settranc( TrafficStats.getMobileTxBytes(),Data.mobilesend));
            tr.setText("R "
                    + settranc(TrafficStats.getTotalRxBytes(), Data.totalrec));
            ts.setText("S "
                    + settranc(TrafficStats.getTotalTxBytes(), Data.totalsend));

            generatelineData();
            generateData(false);
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... arg0) {
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

            a = Data.speed;
            b = Data.down;
            c = Data.up;

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

    public  void setData(String a, String b, String c, double all,
                         double rx, double tx) {
        speed = a;
        down = b;
        up = c;
        dall = all;
        drx = rx;
        dtx = tx;

    }

    public void showResult(){
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                totalSpeed.setText(speed);
                receive.setText(getResources().getString(R.string.download) + ": "
                        + down);
                send.setText(getResources().getString(R.string.upload) + ": " + up);
            }
        };
        handler.sendEmptyMessage(1);
    }
}
