package com.java.launcher.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.launcher.R;
import com.java.launcher.model.AppModel;
import com.java.launcher.view.AppViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * AppAdapter 是 RecyclerView 的适配器类，用于管理和显示应用列表数据。
 */
public class AppAdapter extends RecyclerView.Adapter<AppViewHolder> {
    public List<AppModel> apps = new ArrayList<>(); // 存储应用数据的列表
    private List<AppModel> draggedItems = new ArrayList<>(); // 存储被拖动的应用数据列表

    /**
     * 当需要创建新的 ViewHolder 时调用。
     *
     * @param parent   父视图组
     * @param viewType 视图类型
     * @return 创建的 AppViewHolder 实例
     */
    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 通过 LayoutInflater 加载 item_app 布局文件，并创建一个新的 ViewHolder 实例
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    /**
     * 绑定 ViewHolder 和应用数据。
     *
     * @param holder   要绑定的 ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        // 绑定数据到 ViewHolder
        holder.bind(apps.get(position));
    }

    /**
     * 获取 RecyclerView 项目数量。
     *
     * @return 项目数量
     */
    @Override
    public int getItemCount() {
        return apps.size(); // 返回应用列表的大小
    }

    /**
     * 设置新的应用数据并刷新视图。
     *
     * @param apps 新的应用数据列表
     */
    public void setApps(List<AppModel> apps) {
        this.apps = apps;
        notifyDataSetChanged(); // 通知数据集已更改
    }

    /**
     * 在列表中移动项目。
     *
     * @param fromPosition 原始位置
     * @param toPosition   目标位置
     */
    public void onItemMove(int fromPosition, int toPosition) {
        // 确保位置有效
        if (fromPosition < 0 || toPosition < 0 || fromPosition >= apps.size() || toPosition >= apps.size()) {
            return;
        }
        // 移动应用数据并更新列表
        AppModel movedApp = apps.remove(fromPosition);
        apps.add(toPosition, movedApp);
        notifyItemMoved(fromPosition, toPosition); // 通知项目已移动
    }

    public void onItemRemoved(int position) {
        // 确保位置有效
        apps.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 获取被拖动的应用数据列表。
     *
     * @return 被拖动的应用数据列表
     */
    public List<AppModel> getDraggedItems() {
        return draggedItems;
    }

    /**
     * 设置被拖动的应用数据列表。
     *
     * @param draggedItems 新的被拖动的应用数据列表
     */
    public void setDraggedItems(List<AppModel> draggedItems) {
        this.draggedItems = draggedItems;
    }
}
