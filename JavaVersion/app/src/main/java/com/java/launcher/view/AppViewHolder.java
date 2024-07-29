package com.java.launcher.view;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.java.launcher.R;
import com.java.launcher.model.AppModel;

public class AppViewHolder extends RecyclerView.ViewHolder {
    private ImageView appIcon; // 应用图标
    private TextView appName; // 应用名称
    private DialogFragment currentDialog; // 当前显示的对话框

    public AppViewHolder(@NonNull View itemView) {
        super(itemView);
        // 初始化视图组件
        appIcon = itemView.findViewById(R.id.appIcon);
        appName = itemView.findViewById(R.id.appName);

        // 设置点击事件，启动应用
        itemView.setOnClickListener(v -> {
            AppModel app = (AppModel) itemView.getTag();
            if (app != null) {
                itemView.getContext().startActivity(
                        itemView.getContext().getPackageManager().getLaunchIntentForPackage(app.getPackageName())
                );
            }
        });

        // 设置长按事件，显示应用信息对话框
        itemView.setOnLongClickListener(v -> {
            AppModel app = (AppModel) itemView.getTag();
            if (app != null) {
                showAppInfoDialog(app);
            }
            return true; // 返回true表示事件已处理
        });
    }

    // 绑定应用数据到视图
    public void bind(AppModel app) {
        appIcon.setImageDrawable(app.getIcon()); // 设置应用图标
        appName.setText(app.getAppName()); // 设置应用名称
        itemView.setTag(app); // 将应用对象设置为视图的标签
    }

    // 显示应用信息对话框
    private void showAppInfoDialog(AppModel app) {
        boolean isSystemApp = app.isSystemApp(); // 判断是否是系统应用

        currentDialog = AppInfoDialogFragment.newInstance(app.getAppName(), app.getPackageName(), isSystemApp);
        // 显示对话框
        currentDialog.show(((FragmentActivity) itemView.getContext()).getSupportFragmentManager(), "AppInfoDialog");
    }

    // 取消当前显示的对话框
    public void cancelDialog() {
        if (currentDialog != null) {
            currentDialog.dismiss(); // 关闭对话框
            currentDialog = null; // 清除对话框引用
        }
    }
}
