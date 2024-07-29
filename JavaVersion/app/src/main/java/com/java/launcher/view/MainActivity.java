package com.java.launcher.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.java.launcher.R;
import com.java.launcher.adapter.AppViewPagerAdapter;
import com.java.launcher.model.AppModel;
import com.java.launcher.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager; // 用于显示应用页面的ViewPager2
    private AppViewPagerAdapter appViewPagerAdapter; // ViewPager的适配器
    private MainViewModel appViewModel; // 视图模型

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图组件
        viewPager = findViewById(R.id.viewPager);
        appViewPagerAdapter = new AppViewPagerAdapter(this, new ArrayList<>());
        viewPager.setAdapter(appViewPagerAdapter);

        // 获取MainViewModel实例，并设置观察者
        appViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        appViewModel.getAppsLiveData().observe(this, this::setupPages);
    }

    // 设置页面内容
    private void setupPages(List<AppModel> appModels) {
        int appsPerPage = 28; // 每页显示的应用数量
        ArrayList<ArrayList<AppModel>> pages = new ArrayList<>();
        for (int i = 0; i < appModels.size(); i += appsPerPage) {
            int end = Math.min(i + appsPerPage, appModels.size());
            ArrayList<AppModel> page = new ArrayList<>(appModels.subList(i, end));
            pages.add(page); // 将每页的应用列表添加到pages中
        }
        appViewPagerAdapter.setPages(pages); // 设置适配器的页面
        appViewPagerAdapter.notifyDataSetChanged(); // 通知适配器数据已改变
    }
}
