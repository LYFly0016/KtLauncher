package com.java.launcher.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.java.launcher.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherView extends LinearLayout {
    private static final String WEATHER_API_KEY = "d838adf6791f4b0ba80a7cde1b580d8c"; // 替换为你的 和风 API Key
    private static final String WEATHER_API_URL = "http://guolin.tech/api/weather?cityid=CN101280601&key=";


    private TextView weatherTextView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public WeatherView(Context context) {
        super(context);
        init(context);
    }

    public WeatherView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeatherView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_weather, this, true);
        weatherTextView = findViewById(R.id.weatherTextView);
        startWeatherUpdates();
    }

    /**
     * 开始定时更新天气数据
     */
    private void startWeatherUpdates() {
        scheduler.scheduleAtFixedRate(this::fetchWeatherData, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * 获取实时天气数据并更新UI
     */
    private void fetchWeatherData() {
        new Thread(() -> {
            try {
                URL url = new URL(WEATHER_API_URL + WEATHER_API_KEY);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d("JOKER", "fetchWeatherData: " + connection);
                connection.setRequestMethod("GET");

                // 获取响应代码
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("HTTP error code: " + responseCode);
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 解析 JSON 响应
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray heWeatherArray = jsonResponse.getJSONArray("HeWeather");
                JSONObject heWeather = heWeatherArray.getJSONObject(0);
                JSONObject now = heWeather.getJSONObject("now");
                String weatherDescription = now.getString("cond_txt");
                double temperature = now.getDouble("tmp");

                String weatherInfo = String.format("%s, %.1f°C", weatherDescription, temperature);

                // 更新 UI
                handler.post(() -> weatherTextView.setText(weatherInfo));

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> weatherTextView.setText("Failed to load weather"));
            }
        }).start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        scheduler.shutdown(); // 停止定时任务，避免内存泄漏
    }
}
