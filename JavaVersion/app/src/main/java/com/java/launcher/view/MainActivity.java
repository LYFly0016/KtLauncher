package com.java.launcher.view;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

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

/**
 * MainActivity 是应用程序的主活动，用于显示应用程序的页面。
 * 它使用 ViewPager2 来展示应用程序图标，并通过 ViewModel 管理数据。
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager; // 用于显示应用程序页面的 ViewPager2 组件
    private AppViewPagerAdapter appViewPagerAdapter; // ViewPager2 的适配器
    private MainViewModel appViewModel; // 视图模型，用于管理和提供应用程序数据

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 设置活动的布局文件

        // 初始化视图组件
        viewPager = findViewById(R.id.viewPager); // 获取布局文件中的 ViewPager2 组件
        appViewPagerAdapter = new AppViewPagerAdapter(this, new ArrayList<>()); // 创建 AppViewPagerAdapter 实例
        viewPager.setAdapter(appViewPagerAdapter); // 设置 ViewPager2 的适配器

        // 获取 MainViewModel 实例，并设置观察者以更新页面内容
        appViewModel = new ViewModelProvider(this).get(MainViewModel.class); // 获取 MainViewModel 实例
        appViewModel.getAppsLiveData().observe(this, this::setupPages); // 观察 LiveData，当数据发生变化时调用 setupPages 方法
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // 设置状态栏为透明，并使内容延伸到状态栏区域
        Window window = getWindow();
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
        } else {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    /**
     * 根据应用程序数据设置页面内容。
     *
     * @param appModels 包含应用程序数据的列表
     */
    private void setupPages(List<AppModel> appModels) {
        int appsPerPage = 20; // 每页显示的应用程序数量
        ArrayList<ArrayList<AppModel>> pages = new ArrayList<>(); // 用于存储每页的应用程序列表

        // 将应用程序列表划分为若干页，每页包含 appsPerPage 个应用程序
        for (int i = 0; i < appModels.size(); i += appsPerPage) {
            int end = Math.min(i + appsPerPage, appModels.size()); // 确定当前页的结束索引
            ArrayList<AppModel> page = new ArrayList<>(appModels.subList(i, end)); // 获取当前页的应用程序列表
            pages.add(page); // 将当前页的应用程序列表添加到 pages 中
        }

        // 设置适配器的页面数据，并通知适配器数据已改变
        appViewPagerAdapter.setPages(pages); // 设置适配器的页面数据
        appViewPagerAdapter.notifyDataSetChanged(); // 通知适配器数据已改变，刷新页面
    }
}
