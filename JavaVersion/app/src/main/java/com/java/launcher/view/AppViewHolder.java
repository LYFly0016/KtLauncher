package com.java.launcher.view;

import android.content.Intent;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.java.launcher.R;
import com.java.launcher.model.AppModel;

/**
 * AppViewHolder 是 RecyclerView 的 ViewHolder，负责显示应用程序的图标和名称。
 * 它处理应用图标和名称的绑定，以及响应点击和长按事件。
 */
public class AppViewHolder extends RecyclerView.ViewHolder {
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

        // 设置点击事件监听器，启动应用程序
        itemView.setOnClickListener(v -> {
            AppModel app = (AppModel) itemView.getTag(); // 获取与视图关联的应用程序对象
            if (app != null) {
                // 启动应用程序的主活动
                itemView.getContext().startActivity(
                        itemView.getContext().getPackageManager().getLaunchIntentForPackage(app.getPackageName())
                );
            }
        });


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

    /**
     * 显示应用程序信息对话框。
     *
     * @param app 应用程序数据模型
     */
    private void showAppInfoDialog(AppModel app) {
        boolean isSystemApp = app.isSystemApp(); // 判断应用程序是否是系统应用
        // 创建对话框实例
        currentDialog = AppInfoDialogFragment.newInstance(app.getAppName(), app.getPackageName(), isSystemApp);
        // 显示对话框
        currentDialog.show(((FragmentActivity) itemView.getContext()).getSupportFragmentManager(), "AppInfoDialog");
    }

    /**
     * 取消当前显示的对话框。
     */
    public void cancelDialog() {
        if (currentDialog != null) {
            currentDialog.dismiss(); // 关闭对话框
            currentDialog = null; // 清除对话框引用
        }
    }
}
