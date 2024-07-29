package com.java.launcher.view;

import android.os.Bundle;
import android.util.Log;
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
 * MainFragment 是应用程序的一个 Fragment，用于显示应用程序列表。
 * 它使用 RecyclerView 来展示应用程序的图标，并支持拖动和移动操作。
 */
public class MainFragment extends Fragment {

    private static final String ARG_APPS = "apps"; // 存储应用程序列表的参数名
    private ArrayList<AppModel> apps; // 应用程序列表
    private int currentPosition; // 当前页面的位置
    private AppViewPagerAdapter viewPagerAdapter; // ViewPager 的适配器

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
        // 加载 Fragment 的布局文件
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // 初始化 RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView); // 获取 RecyclerView 组件
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4)); // 设置 GridLayoutManager，每行 4 列
        AppAdapter appAdapter = new AppAdapter(); // 创建 AppAdapter 实例
        appAdapter.setApps(apps); // 设置应用程序列表
        recyclerView.setAdapter(appAdapter); // 设置 RecyclerView 的适配器

        // 打印调试信息，查看当前页面位置和适配器的页面数量
        Log.d("JOKER", "onCreateView: " + currentPosition + "--" + viewPagerAdapter.getItemCount());

        // 创建拖动和滑动的回调
        ItemTouchHelper.Callback callback = new DragItemTouchHelperCallback(
                appAdapter, // 当前页面的适配器
                viewPagerAdapter, // ViewPager 的适配器
                recyclerView, // 当前的 RecyclerView 实例
                currentPosition, // 当前页面的位置
                viewPagerAdapter.getItemCount() // ViewPager 的页面数量
        );
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback); // 创建 ItemTouchHelper 实例
        touchHelper.attachToRecyclerView(recyclerView); // 将回调附加到 RecyclerView

        return view; // 返回 Fragment 的视图
    }
}
