package com.zsc.edu.zscweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherId = prefs.getString("weather_id", null);
        if (weatherId != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("weather_id", weatherId);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (System.currentTimeMillis() - exitTime > 2000){
                Toast.makeText(getApplicationContext(), R.string.exit, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
