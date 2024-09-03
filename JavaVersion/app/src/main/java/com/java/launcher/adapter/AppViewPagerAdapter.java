package com.java.launcher.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.java.launcher.model.AppModel;
import com.java.launcher.view.AppFragment;
import com.java.launcher.view.MainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * AppViewPagerAdapter 是一个自定义的 FragmentStateAdapter，用于管理 ViewPager2 中的 Fragment。
 */
public class AppViewPagerAdapter extends FragmentStateAdapter {
    private ArrayList<ArrayList<AppModel>> pages; // 存储每个页面的应用数据列表

    /**
     * 构造函数，初始化适配器和页面数据。
     *
     * @param fragmentActivity FragmentActivity 实例，用于 FragmentStateAdapter 的初始化
     * @param pages            存储应用数据的页面列表，每个页面包含一个 AppModel 列表
     */
    public AppViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<ArrayList<AppModel>> pages) {
        super(fragmentActivity);
        this.pages = pages;
    }

    /**
     * 根据位置创建相应的 Fragment。
     *
     * @param position 页面的索引位置
     * @return 对应索引位置的 Fragment 实例
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ArrayList<AppModel> apps = pages.get(position);
        if (position == 0) {
            // 如果是第一个页面，返回 MainFragment
            return MainFragment.newInstance(apps, position, this);
        } else {
            // 否则返回 AppFragment
            return AppFragment.newInstance(apps, position, this);
        }
    }

    /**
     * 获取页面数量。
     *
     * @return 页面数量
     */
    @Override
    public int getItemCount() {
        return pages.size();
    }

    /**
     * 设置新的页面数据，并刷新适配器。
     *
     * @param pages 新的页面数据列表
     */
    public void setPages(ArrayList<ArrayList<AppModel>> pages) {
        this.pages = pages;
        notifyDataSetChanged(); // 通知数据集已更改
    }

    public ArrayList<AppModel> getPage(int position) {
        return pages.get(position);
    }

    /**
     * 将拖动的应用数据从一个页面移动到另一个页面。
     *
     * @param draggedApps 要移动的应用数据列表
     * @param fromPage    数据来源页面的索引
     * @param toPage      数据目标页面的索引
     */
    public void moveItemsToPage(List<AppModel> draggedApps, int fromPage, int toPage) {
        // 如果目标页面和来源页面相同，或者没有拖动的应用数据，直接返回
        if (fromPage == toPage || draggedApps == null || draggedApps.isEmpty()) return;

        ArrayList<AppModel> fromPageList = pages.get(fromPage);
        ArrayList<AppModel> toPageList;
        Log.d("JOKER", "moveItemsToPage: " + fromPage + "--" + toPage);
        // 如果目标页面超过现有页面数量，创建一个新的页面
        if (toPage >= pages.size()) {
            toPageList = new ArrayList<>();
            pages.add(toPageList);
        } else {
            toPageList = pages.get(toPage);
        }

        // 从来源页面移除拖动的应用数据，并添加到目标页面
        fromPageList.removeAll(draggedApps);
        toPageList.addAll(draggedApps);

        // 如果来源页面为空且不是首页，则删除该页面
        if (fromPageList.isEmpty() && fromPage != 0) {
            pages.remove(fromPage);
        }
    }
}
