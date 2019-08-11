package com.nobletecx.a2dapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private String link = "https://marketdata.set.or.th/mkt/marketsummary.do?language=en&country=US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showTwoD = findViewById(R.id.showTwoD);
        showSet = findViewById(R.id.showSet);
        showSetValue = findViewById(R.id.showSetValue);
        showDate = findViewById(R.id.showDate);

        Calendar calendar = Calendar.getInstance();
        String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        String day = days[calendar.get(Calendar.DAY_OF_WEEK)];

        showDate.setText(day);

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
            showSet.setText(arrayList.get(1));
            showSetValue.setText(arrayList.get(7));
            String TwoD = getPrefix(arrayList.get(1))+getLast(arrayList.get(7));
            showTwoD.setText(TwoD);
        }
    }
}

