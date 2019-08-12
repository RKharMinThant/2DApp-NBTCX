package com.nobletecx.a2dapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView showTwoD,showDate;
    Button showSet;
    Button showSetValue;
    Button showToday2DValue,showYesterday2DValue;
    Button refresh;
    ProgressBar pg;

    private String link = "https://marketdata.set.or.th/mkt/marketsummary.do?language=en&country=US";
    private String TwoDValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showTwoD = findViewById(R.id.showTwoD);
        showSet = findViewById(R.id.showSet);
        showSetValue = findViewById(R.id.showSetValue);
        showDate = findViewById(R.id.showDate);
        showToday2DValue = findViewById(R.id.showToday2DValue);
        showYesterday2DValue = findViewById(R.id.showYesterday2DValue);
        refresh = findViewById(R.id.refresh);
        pg = findViewById(R.id.progressBar);
         //fab = findViewById(R.id.fab);

        ActionBar ab = getSupportActionBar();
        ab.hide();

        Calendar calendar = Calendar.getInstance();
        String[] days = new String[] { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        String day = days[calendar.get(Calendar.DAY_OF_WEEK)];
        //show day from date
        showDate.setText(day);

        new fetchData().execute(link);

    }

    public void refresh(View view){
        showTwoD.setText("2D");
        showSet.setText("SET");
        showSetValue.setText("SET VALUE");

        new fetchData().execute(link);
    }

    public String getLast(String prefix){
        String a = prefix.replaceAll(",","");
        int index = a.indexOf(".");
        int strLength = index -1;
        return a.substring(strLength,index);
    }

    public String getPrefix(String last){
        String a = last.replaceAll(",","");
        int lastIndex = a.length() - 1;
        int strLength = a.length();
        return a.substring(lastIndex,strLength);
    }

    private class fetchData extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            pg.setProgress(50);
            pg.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> getData = new ArrayList<>();
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Elements table = document.getElementsByClass("table-info");
                Elements tr = table.get(0).select("tr");
                Elements td = tr.get(1).select("td");
                for(Element cell : td){
                    getData.add(cell.text());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return getData;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            pg.setVisibility(View.GONE);
            showSet.setText("SET : "+arrayList.get(1));
            showSetValue.setText("Value : "+arrayList.get(7));
            TwoDValue = getPrefix(arrayList.get(1)) + getLast(arrayList.get(7));
            showTwoD.setText(TwoDValue);

            //storing 2D value
            SharedPreferences sp = getSharedPreferences("2D", 0);
            SharedPreferences.Editor spEdit = sp.edit();
            spEdit.putString("Today2D", TwoDValue);
            spEdit.apply();

            String stored2dValue = sp.getString("Today2D", "");
            if (stored2dValue.equals(TwoDValue)) {
                showToday2DValue.setText(TwoDValue);
                showYesterday2DValue.setText("Off Day");
            } else {
                showToday2DValue.setText(TwoDValue);
                showYesterday2DValue.setText(stored2dValue);
            }
        }
    }
}

