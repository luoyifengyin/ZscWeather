package com.zsc.edu.zscweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 落忆枫音 on 2018/6/3.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}
