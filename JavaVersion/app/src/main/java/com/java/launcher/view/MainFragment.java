package com.java.launcher.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.java.launcher.R;
import com.java.launcher.adapter.AppViewPagerAdapter;
import com.java.launcher.model.AppModel;

import java.util.ArrayList;

/**
 * MainFragment 是应用程序的一个 Fragment，用于显示应用程序列表。
 * 它使用 RecyclerView 来展示应用程序的图标，并支持拖动和移动操作。
 */
public class MainFragment extends BaseFragment {

    private static final String ARG_APPS = "apps"; // 存储应用程序列表的参数名
    private ArrayList<AppModel> apps; // 应用程序列表
    private AnalogClockView clockView;


    /**
     * 创建新的 MainFragment 实例，并设置参数。
     *
     * @param apps 应用程序列表
     * @param currentPosition 当前页面的位置
     * @param viewPagerAdapter ViewPager 的适配器
     * @return 返回初始化后的 MainFragment 实例
     */
    public static MainFragment newInstance(ArrayList<AppModel> apps, int currentPosition, AppViewPagerAdapter viewPagerAdapter) {
        MainFragment fragment = new MainFragment(); // 创建 Fragment 实例
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_APPS, apps); // 将应用程序列表放入参数中
        fragment.setArguments(args); // 设置参数
        fragment.currentPosition = currentPosition; // 设置当前页面的位置
        fragment.viewPagerAdapter = viewPagerAdapter; // 设置 ViewPager 的适配器
        return fragment; // 返回初始化后的 Fragment 实例
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 从参数中获取应用程序列表
        if (getArguments() != null) {
            apps = getArguments().getParcelableArrayList(ARG_APPS); // 获取应用程序列表
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Additional setup for MainFragment
        clockView = view.findViewById(R.id.analogClockView);
        return view;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_main; // 返回 MainFragment 的布局资源 ID
    }




}
