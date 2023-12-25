package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    TextView weather, temp, humid, atoms, windDir, windScale, feelLike, vis, wisdom;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    List<String> datalist;
    String cityname;
    EditText city;
    Button refresh;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HeConfig.init("HE2312211653341405", "eacf0b23e7254d799af61644850373ac");
        HeConfig.switchToDevService();

        city = findViewById(R.id.city);
        cityname = city.getText().toString();
        recyclerView = findViewById(R.id.daily);

        linearLayout = findViewById(R.id.background);
        weather = findViewById(R.id.weather);
        temp = findViewById(R.id.temperature);
        humid = findViewById(R.id.humidness);
        atoms = findViewById(R.id.atomsphere);
        windDir = findViewById(R.id.windDir);
        windScale = findViewById(R.id.windScale);
        feelLike = findViewById(R.id.feelLike);
        vis = findViewById(R.id.vis);
        refresh = findViewById(R.id.refrash);
        wisdom = findViewById(R.id.wisdom);

        //初始化界面
        linearLayout.setBackgroundResource(R.drawable.sunny);
        updateAll(cityname); //打开app后默认城市为武汉
        new MyAsyncTask(wisdom).execute();

        //点击更新按钮界面
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAsyncTask(wisdom).execute();
                if (!cityname.equals(city.getText().toString())){
                    cityname = city.getText().toString();
                    updateAll(cityname);
                };
            }
        });

    }

    private void updateAll(String cityname) {
        QWeather.getGeoCityLookup(MainActivity.this, cityname, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d("smile", "onError: " + throwable);
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                /**
                 * 实况天气数据
                 * @param location 所查询的地区，可通过该地区ID、经纬度进行查询经纬度格式：经度,纬度
                 *                 （英文,分隔，十进制格式，北纬东经为正，南纬西经为负)
                 * @param lang     (选填)多语言，可以不使用该参数，默认为简体中文
                 * @param unit     (选填)单位选择，公制（m）或英制（i），默认为公制单位
                 * @param listener 网络访问结果回调
                 */
                String location = geoBean.getLocationBean().get(0).getId();
                Log.d("smile", "onSuccess: " + location);
                QWeather.getWeatherNow(MainActivity.this, location, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i("smile", "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(WeatherNowBean weatherBean) {
                        Log.i("smile", "getWeather onSuccess: " + new Gson().toJson(weatherBean));
                        //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                        if (Code.OK == weatherBean.getCode()) {
                            WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                            updateWeather(now);
                        } else {
                            //在此查看返回数据失败的原因
                            Code code = weatherBean.getCode();
                            Log.i("smile", "failed code: " + code);
                        }
                    }
                });
                QWeather.getWeather3D(MainActivity.this, location, new QWeather.OnResultWeatherDailyListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        Log.i("smile", "getDailyWeather onError: " + throwable);
                    }

                    @Override
                    public void onSuccess(WeatherDailyBean weatherDailyBean) {
                        Log.i("smile", "getDailyWeather onSuccess: " + new Gson().toJson(weatherDailyBean));
                        //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                        if (Code.OK == weatherDailyBean.getCode()) {
                            List<WeatherDailyBean.DailyBean> daily = weatherDailyBean.getDaily();
                            initData(daily);
                            updateDailyWeather(datalist);
                        } else {
                            Code code = weatherDailyBean.getCode();
                            Log.i("smile", "failed code: " + code);
                        }
                    }
                });
            }
        });
    }

    private void updateDailyWeather(List<String> datalist) {
        runOnUiThread(()->{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            myAdapter = new MyAdapter(datalist);
            recyclerView.setAdapter(myAdapter);
        });
    }
    public void updateWeather(WeatherNowBean.NowBaseBean now) {
        runOnUiThread(() -> {
            if (now.getText().equals("晴")) {
                linearLayout.setBackgroundResource(R.drawable.sunny);
            } else if (now.getText().equals("阴")) {
                linearLayout.setBackgroundResource(R.drawable.overvast);
            } else if (now.getText().indexOf("云") != -1){
                linearLayout.setBackgroundResource(R.drawable.cloudy);
            } else if (now.getText().indexOf("雨") != -1) {
                linearLayout.setBackgroundResource(R.drawable.rainy);
            } else if (now.getText().indexOf("雪") != -1){
                linearLayout.setBackgroundResource(R.drawable.snowy);
            }

            weather.setText(now.getText());
            temp.setText(now.getTemp() + "°C");
            humid.setText("相对湿度: " + now.getHumidity() + "%");
            atoms.setText("气压: " + now.getPressure() + "hPa");
            windDir.setText(now.getWindDir());
            windScale.setText(now.getWindScale() + "级");
            feelLike.setText("体感温度: " + now.getFeelsLike() + "°C");
            vis.setText("能见度: " + now.getVis() + "公里");
        });
    }

    public void initData(List<WeatherDailyBean.DailyBean> daily){
        datalist = new ArrayList<>();
        datalist.add(String.format("今天 %s %2s °C / %2s °C", daily.get(0).getTextDay(), daily.get(0).getTempMax(), daily.get(0).getTempMin()));
        datalist.add(String.format("明天 %s %2s °C / %2s °C", daily.get(1).getTextDay(), daily.get(1).getTempMax(), daily.get(1).getTempMin()));
        datalist.add(String.format("后天 %s %2s °C / %2s °C", daily.get(2).getTextDay(), daily.get(2).getTempMax(), daily.get(2).getTempMin()));
    }
}