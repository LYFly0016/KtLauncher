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

public class MainFragment extends Fragment {

    private static final String ARG_APPS = "apps"; // 存储应用列表的参数名
    private ArrayList<AppModel> apps; // 应用列表
    private int currentPosition; // 当前页面位置
    private AppViewPagerAdapter viewPagerAdapter; // ViewPager的适配器

    // 创建新的实例，并设置参数
    public static MainFragment newInstance(ArrayList<AppModel> apps, int currentPosition, AppViewPagerAdapter viewPagerAdapter) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_APPS, apps); // 将应用列表放入参数中
        fragment.setArguments(args);
        fragment.currentPosition = currentPosition; // 设置当前页面位置
        fragment.viewPagerAdapter = viewPagerAdapter; // 设置ViewPager的适配器
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 从参数中获取应用列表
        if (getArguments() != null) {
            apps = getArguments().getParcelableArrayList(ARG_APPS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 加载Fragment的布局
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // 初始化RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4)); // 使用4列的GridLayoutManager
        AppAdapter appAdapter = new AppAdapter();
        appAdapter.setApps(apps); // 设置应用列表
        recyclerView.setAdapter(appAdapter);

        // 打印调试信息
        Log.d("JOKER", "onCreateView: " + currentPosition + "--" + viewPagerAdapter.getItemCount());

        // 创建拖动和滑动的回调
        ItemTouchHelper.Callback callback = new DragItemTouchHelperCallback(appAdapter, viewPagerAdapter, recyclerView, currentPosition, viewPagerAdapter.getItemCount());
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView); // 将回调附加到RecyclerView

        return view;
    }
}
