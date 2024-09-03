package com.java.launcher.adapter;

import android.content.ClipData;
import android.content.ClipDescription;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
    private final ViewPager2 viewPager;
    private final AppViewPagerAdapter appViewPagerAdapter;

    public AppAdapter(ViewPager2 viewPager, AppViewPagerAdapter appViewPagerAdapter) {
        this.viewPager = viewPager;
        this.appViewPagerAdapter = appViewPagerAdapter;
    }

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
        Log.d("JOKER", "onBindViewHolder: ");
        holder.bind(apps.get(position));
        holder.itemView.setOnLongClickListener(v -> {

            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);

            // 启动拖拽
            v.startDragAndDrop(null, myShadow, v, 0);
            v.setVisibility(View.INVISIBLE); // 隐藏原始视图
            return true;
        });
        holder.itemView.setOnDragListener((v, event) -> {
            Log.d("JOKER", "setOnDragListener: ");
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    // 拖拽进入目标区域
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    // 检查拖拽位置，接近边缘时切换页面
                    int width = v.getWidth();
                    float x = event.getX();

                    if (x < width * 0.1) { // 接近左边缘
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                    } else if (x > width * 0.9) { // 接近右边缘
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    // 当图标被放下时的操作
                    View draggedView = (View) event.getLocalState(); // 获取拖动的View
                    AppModel app = (AppModel) draggedView.getTag(); // 获取图标的名字（标识符）
                    int fromPage = findPageContainingApp(app);

                    // 如果目标页面和当前位置不一致，则移动图标到新位置
                    if (fromPage != position) {
                        appViewPagerAdapter.getPage(fromPage).remove(app); // 从原位置移除
                        appViewPagerAdapter.getPage(position).add(app); // 添加到新位置
                        notifyItemChanged(fromPage); // 刷新原页面
                        notifyItemChanged(position); // 刷新目标页面
                    }

                    draggedView.setVisibility(View.VISIBLE); // 重新显示拖动的View
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    // 拖拽结束时恢复图标的可见性
                    View droppedView = (View) event.getLocalState(); // 获取拖动的View
                    droppedView.setVisibility(View.VISIBLE); // 重新显示
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private int findPageContainingApp(AppModel app) {
        for (int i = 0; i < appViewPagerAdapter.getItemCount(); i++) {
            if (appViewPagerAdapter.getPage(i).contains(app)) {
                return i;
            }
        }
        return -1;
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
