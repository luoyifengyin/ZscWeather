package com.zsc.edu.zscweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zsc.edu.zscweather.db.City;
import com.zsc.edu.zscweather.db.County;
import com.zsc.edu.zscweather.db.Province;
import com.zsc.edu.zscweather.util.HttpUtil;
import com.zsc.edu.zscweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by DZ on 2018-06-03.
 */

public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    //进度对话框
    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //1. 初始化View
        //inflate选择城市的布局
        View view = inflater.inflate(R.layout.choose_area, container);
        // return super.onCreateView(inflater, container, savedInstanceState);

        //2. 找到各个UI控件
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        //3. ListView 适配器初始化
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //4. 设置用户点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) { //省
                    selectedProvince = provinceList.get(position);//当前选中的省
                    queryCities();//请求市
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        //getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });

        //5. 返回按钮的监听
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {//县
                    queryCities();//返回到市，请求市
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });

        //请求省的数据，更新UI
        queryProvinces();

    }

    //请求省
    private void queryProvinces() {
        //设置标题
        titleText.setText("中国");
        //返回按钮不可见
        backButton.setVisibility(View.GONE);
        //litepal读取数据库Province
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) { //数据库有数据
            //adapter的数据链表清空
            dataList.clear();
            for (Province province : provinceList) {
                //把省的名字添加到adapter的数据链表
                dataList.add(province.getProvinceName());
            }
            //通知适配器改变数据
            adapter.notifyDataSetChanged();
            //选中第一个
            listView.setSelection(0);
            //当前显示的省
            currentLevel = LEVEL_PROVINCE;
        } else { //数据库没有数据
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }


    //请求市
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
		//根据当前的省的ID，去数据库查询省所包含的的市的信息
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())) .find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }


    //请求县
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int proviceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + proviceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }


    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(String address, final String type) {
		//显示进度
        showProgressDialog();
		
		//发起请求
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
				//返回结果
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
					//解析省的数据，写入数据库
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
					//解析市
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
					//解析县
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
					//主线程里更新UI
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
							//关闭进度
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}

