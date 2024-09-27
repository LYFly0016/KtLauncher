package com.java.launcher.adapter;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.java.launcher.R;
import com.java.launcher.model.AppModel;


import java.util.ArrayList;
import java.util.List;

/**
 * AppAdapter 是 RecyclerView 的适配器类，用于管理和显示应用列表数据。
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {
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
        holder.bind(apps.get(position));
        // 设置点击事件监听器，启动应用程序
        holder.itemView.setOnClickListener(v -> {
            AppModel app = (AppModel) holder.itemView.getTag(); // 获取与视图关联的应用程序对象
            if (app != null) {
                // 启动应用程序的主活动
                holder.itemView.getContext().startActivity(
                        holder.itemView.getContext().getPackageManager().getLaunchIntentForPackage(app.getPackageName())
                );
            }
        });
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
                    Log.d("JOKER", "onBindViewHolder: DragEvent.ACTION_DRAG_LOCATION");
                    int width = v.getWidth();
                    float x = event.getX();

                    if (x < width * 0.1) { // 接近左边缘
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                    } else if (x > width * 0.9) { // 接近右边缘
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                case DragEvent.ACTION_DRAG_ENDED:
                    // 拖拽结束时恢复图标的可见性
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


    static class AppViewHolder extends RecyclerView.ViewHolder {
        private ImageView appIcon; // 应用图标的 ImageView
        private TextView appName; // 应用名称的 TextView
        private DialogFragment currentDialog; // 当前显示的对话框

        /**
         * 构造函数，初始化视图组件，并设置点击和长按事件的监听器。
         *
         * @param itemView ViewHolder 的视图项
         */
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化视图组件
            appIcon = itemView.findViewById(R.id.appIcon); // 获取应用图标的 ImageView
            appName = itemView.findViewById(R.id.appName); // 获取应用名称的 TextView
        }
        /**
         * 绑定应用程序数据到视图。
         *
         * @param app 应用程序数据模型
         */
        public void bind(AppModel app) {
            appIcon.setImageDrawable(app.getIcon()); // 设置应用图标
            appName.setText(app.getAppName()); // 设置应用名称
            itemView.setTag(app); // 将应用程序对象设置为视图的标签，用于点击和长按事件
        }
    }

}
