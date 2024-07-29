package com.java.launcher.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.java.launcher.R;
import com.java.launcher.adapter.AppAdapter;
import com.java.launcher.adapter.AppViewPagerAdapter;
import com.java.launcher.helper.DragItemTouchHelperCallback;
import com.java.launcher.model.AppModel;

import java.util.ArrayList;

/**
 * AppFragment 是一个 Fragment 子类，用于显示一个应用图标的页面。
 * 该页面支持应用图标的拖拽和移动。
 */
public class AppFragment extends Fragment {
    private static final String ARG_APPS = "apps"; // 应用列表的参数键
    private ArrayList<AppModel> apps; // 存储应用数据的列表
    private int currentPosition; // 当前页面在 ViewPager 中的位置
    private AppViewPagerAdapter viewPagerAdapter; // ViewPager 的适配器

    /**
     * 创建一个新的 AppFragment 实例并设置参数。
     *
     * @param apps             包含应用数据的 ArrayList
     * @param currentPosition  当前页面在 ViewPager 中的位置
     * @param viewPagerAdapter ViewPager 的适配器
     * @return 新的 AppFragment 实例
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

    /**
     * 在 Fragment 创建时调用，初始化数据。
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            apps = getArguments().getParcelableArrayList(ARG_APPS); // 获取传递的应用数据列表
        }
    }

    /**
     * 创建并返回该 Fragment 的视图层次结构。
     *
     * @param inflater           用于填充视图的布局填充器
     * @param container          视图的父容器
     * @param savedInstanceState 保存的实例状态
     * @return 创建的视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 填充 Fragment 的布局文件并返回根视图
        View view = inflater.inflate(R.layout.fragment_app, container, false);

        // 设置 RecyclerView 和适配器
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4)); // 使用 GridLayoutManager 显示 4 列
        AppAdapter appAdapter = new AppAdapter();
        appAdapter.setApps(apps); // 设置应用数据
        recyclerView.setAdapter(appAdapter);

        // 设置拖拽和移动功能
        ItemTouchHelper.Callback callback = new DragItemTouchHelperCallback(appAdapter, viewPagerAdapter, recyclerView, currentPosition, viewPagerAdapter.getItemCount());
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView); // 将 ItemTouchHelper 附加到 RecyclerView

        return view;
    }
}
