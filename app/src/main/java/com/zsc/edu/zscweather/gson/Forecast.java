package com.zsc.edu.zscweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 落忆枫音 on 2018/6/4.
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {
        public String max;
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }

}
