package com.zsc.edu.zscweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 落忆枫音 on 2018/6/4.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        public String code;

        @SerializedName("txt")
        public String info;
    }

    public String hum;

    public String pcpn;

    public String pres;

    public String wind_deg;

    public String wind_dir;

    public String wind_sc;

    public String wind_spd;
}
