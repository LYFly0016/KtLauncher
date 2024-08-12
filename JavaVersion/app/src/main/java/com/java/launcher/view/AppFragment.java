package com.java.launcher.view;

import android.os.Bundle;

import com.java.launcher.R;
import com.java.launcher.adapter.AppViewPagerAdapter;
import com.java.launcher.model.AppModel;

import java.util.ArrayList;

/**
 * AppFragment 是一个 Fragment 子类，用于显示一个应用图标的页面。
 * 该页面支持应用图标的拖拽和移动。
 */
public class AppFragment extends BaseFragment {
    /**
     * 创建新的 AppFragment 实例，并设置参数。
     */
    public static AppFragment newInstance(ArrayList<AppModel> apps, int currentPosition, AppViewPagerAdapter viewPagerAdapter) {
        AppFragment fragment = new AppFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_APPS, apps); // 传递应用数据列表
        fragment.setArguments(args);
        fragment.currentPosition = currentPosition;
        fragment.viewPagerAdapter = viewPagerAdapter;
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_app; // 返回 AppFragment 的布局资源 ID
    }
}
