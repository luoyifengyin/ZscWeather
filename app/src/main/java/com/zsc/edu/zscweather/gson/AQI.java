package com.zsc.edu.zscweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 落忆枫音 on 2018/6/4.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;

        @SerializedName("qlty")
        public String quality;
    }
}
