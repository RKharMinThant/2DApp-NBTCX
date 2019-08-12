package com.nobletecx.a2dapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    TextView showTwoD,showDate;
    Button showSet;
    Button showSetValue;
    Button showToday2DValue,showYesterday2DValue;
    Button refresh;
    ProgressBar pg;
    SwipeRefreshLayout mySwipeRefreshLayout;
    AdView myAdView;

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
         mySwipeRefreshLayout = findViewById(R.id.swipeRefresh);
         myAdView = findViewById(R.id.adView);
        //FloatingActionButton fab = findViewById(R.id.fab);

        //hiding Action Bar
        ActionBar ab = getSupportActionBar();
        ab.hide();

        //showing Banner Ads
        MobileAds.initialize(this,"ca-app-pub-9687700655895649~8028588167");

        AdRequest myAdReq = new AdRequest.Builder().build();
        myAdView.loadAd(myAdReq);

        //getting date and format to day names only
        Calendar calendar = Calendar.getInstance();
        String[] days = new String[] { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        String day = days[calendar.get(Calendar.DAY_OF_WEEK)];
        //show day from date
        showDate.setText(day);

        new fetchData().execute(link);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        showTwoD.setText("2D");
                        showSet.setText("SET");
                        showSetValue.setText("SET VALUE");

                        new fetchData().execute(link);

                        showInterstitial();
                    }
                }
        );
    }
    //getting last from fetched value
    public String getLast(String prefix){
        String a = prefix.replaceAll(",","");
        int index = a.indexOf(".");
        int strLength = index -1;
        return a.substring(strLength,index);
    }
    //getting prefix from fetched value
    public String getPrefix(String last){
        String a = last.replaceAll(",","");
        int lastIndex = a.length() - 1;
        int strLength = a.length();
        return a.substring(lastIndex,strLength);
    }

    //AD
    private void showInterstitial(){
        MobileAds.initialize(this,"ca-app-pub-9687700655895649~8028588167");
        final InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-9687700655895649/7262301400");
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);


        if(interstitialAd.isLoading()){
            Toast.makeText(getApplicationContext(),"Loading !",Toast.LENGTH_SHORT).show();
        }
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
                Toast.makeText(getApplicationContext(),"Ad Loaded !",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(),"Can't load ad: "+errorCode,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(),"Thank you !",Toast.LENGTH_SHORT).show();
                super.onAdClosed();
            }
        });
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
            mySwipeRefreshLayout.setRefreshing(false);

            showSet.setText("SET : "+arrayList.get(1));
            showSetValue.setText("Value : "+arrayList.get(7));
            TwoDValue = getPrefix(arrayList.get(1)) + getLast(arrayList.get(7));
            showTwoD.setText(TwoDValue);

            //storing 2D value
            // for showing 2D from yesterday not perfect
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
