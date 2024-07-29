package com.java.launcher.helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.java.launcher.adapter.AppAdapter;
import com.java.launcher.adapter.AppViewPagerAdapter;
import com.java.launcher.model.AppModel;
import com.java.launcher.view.AppViewHolder;

import java.util.List;

/**
 * DragItemTouchHelperCallback 是一个自定义的 ItemTouchHelper.Callback，
 * 用于处理应用程序图标在 RecyclerView 中的拖拽和移动操作，包括跨页面的拖拽。
 */
public class DragItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final AppAdapter appAdapter; // 当前页面的适配器
    private final AppViewPagerAdapter viewPagerAdapter; // ViewPager2 的适配器
    private final RecyclerView recyclerView; // 当前的 RecyclerView 实例
    private final int currentPosition; // 当前页面在 ViewPager 中的位置
    private final int pageCount; // 总页数
    private boolean isDragging = false; // 是否正在拖拽
    private Handler handler; // 处理跨页面拖拽的 Handler
    private Runnable switchPageRunnable; // 用于延迟执行页面切换的 Runnable
    private int switchDirection = 0; // 拖拽方向，-1 表示向左，1 表示向右

    /**
     * 构造函数，初始化必要的参数。
     *
     * @param adapter           当前页面的适配器
     * @param viewPagerAdapter  ViewPager2 的适配器
     * @param recyclerView      当前的 RecyclerView 实例
     * @param currentPosition   当前页面在 ViewPager 中的位置
     * @param pageCount         总页数
     */
    public DragItemTouchHelperCallback(AppAdapter adapter, AppViewPagerAdapter viewPagerAdapter,
                                       RecyclerView recyclerView, int currentPosition, int pageCount) {
        this.appAdapter = adapter;
        this.viewPagerAdapter = viewPagerAdapter;
        this.recyclerView = recyclerView;
        this.currentPosition = currentPosition;
        this.pageCount = pageCount;
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置拖拽和滑动的标志。
     *
     * @param recyclerView 目标 RecyclerView
     * @param viewHolder   目标 ViewHolder
     * @return 返回支持的移动标志
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, 0); // 仅允许拖拽
    }

    /**
     * 当一个项目被拖动时调用。
     *
     * @param recyclerView RecyclerView
     * @param viewHolder   拖动的 ViewHolder
     * @param target       目标位置的 ViewHolder
     * @return 如果成功移动，返回 true
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        appAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        if (viewHolder instanceof AppViewHolder) {
            ((AppViewHolder) viewHolder).cancelDialog(); // 取消任何显示的弹窗
        }
        return true;
    }

    /**
     * 当项目被滑动时调用（不处理滑动操作）。
     *
     * @param viewHolder 滑动的 ViewHolder
     * @param direction  滑动的方向
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // 不处理滑动
    }

    /**
     * 是否启用长按拖动。
     *
     * @return 返回 true 以启用长按拖动
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 是否启用项目视图滑动。
     *
     * @return 返回 false 不启用滑动
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * 当选中状态改变时调用。
     *
     * @param viewHolder 发生状态变化的 ViewHolder
     * @param actionState 新的动作状态
     */
    @Override
    public void onSelectedChanged(@NonNull RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        Log.d("JOKER", "onSelectedChanged: " + actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            isDragging = true;
            if (viewHolder instanceof AppViewHolder) {
                ((AppViewHolder) viewHolder).cancelDialog(); // 取消弹窗
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            isDragging = false;
            handler.removeCallbacks(switchPageRunnable); // 取消页面切换
        }
    }

    /**
     * 自定义绘制拖动的项目视图。
     *
     * @param c              画布
     * @param recyclerView   当前 RecyclerView
     * @param viewHolder     当前拖动的 ViewHolder
     * @param dX             X 轴偏移
     * @param dY             Y 轴偏移
     * @param actionState    动作状态
     * @param isCurrentlyActive 是否当前活跃
     */
    @Override
    public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (!isDragging) return;

        // 计算边界位置
        int edgeWidth = recyclerView.getWidth() / 6; // 边缘灵敏度因子
        int itemRight = viewHolder.itemView.getRight();
        int itemLeft = viewHolder.itemView.getLeft();

        // 检查是否拖动到边缘
        if (itemRight > recyclerView.getWidth() - edgeWidth && currentPosition < viewPagerAdapter.getItemCount() - 1) {
            switchDirection = 1;
        } else if (itemLeft < edgeWidth && currentPosition > 0) {
            switchDirection = -1;
        } else {
            switchDirection = 0;
        }


        if (switchDirection != 0) {
            handler.removeCallbacks(switchPageRunnable);
            switchPageRunnable = () -> {
                // 执行页面切换
                Log.d("JOKER", "onChildDraw: switchDirection = " + switchDirection + "--" + currentPosition);
                Log.d("JOKER", "onChildDraw: viewPagerAdapter.getItemCount() = " + viewPagerAdapter.getItemCount());
                switchPage(currentPosition + 1);
                if (switchDirection == 1 && currentPosition < viewPagerAdapter.getItemCount() - 1) {
                    switchPage(currentPosition + 1);
                } else if (switchDirection == -1 && currentPosition > 0) {
                    switchPage(currentPosition - 1);
                }
                switchDirection = 0;
            };
            handler.postDelayed(switchPageRunnable, 1000); // 延迟 1 秒执行页面切换
        } else {
            handler.removeCallbacks(switchPageRunnable); // 取消页面切换
        }
    }

    /**
     * 切换到新页面并移动拖动的项目。
     *
     * @param newPosition 新页面的位置
     */
    private void switchPage(int newPosition) {
        Log.d("JOKER", "switchPage: " + newPosition + "--" + currentPosition);
        if (newPosition != currentPosition) {
            List<AppModel> draggedApps = appAdapter.getDraggedItems();
            viewPagerAdapter.moveItemsToPage(draggedApps, currentPosition, newPosition);
            appAdapter.notifyDataSetChanged();
            viewPagerAdapter.notifyDataSetChanged();

            if (recyclerView.getParent() instanceof ViewPager2) {
                Log.d("JOKER", "switchPage: enter if");
                ((ViewPager2) recyclerView.getParent()).setCurrentItem(newPosition, true);
            }
        }
    }
}
