package com.java.launcher.view;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.java.launcher.viewmodel.MainViewModel;

/**
 * AppInfoDialogFragment 是一个 DialogFragment，用于显示应用程序的信息对话框。
 * 对话框提供了操作选项，例如卸载应用程序或从列表中移除应用图标。
 */
public class AppInfoDialogFragment extends DialogFragment {

    // 传递应用名称的参数键
    private static final String ARG_APP_NAME = "app_name";
    // 传递包名的参数键
    private static final String ARG_PACKAGE_NAME = "package_name";
    // 传递是否是系统应用的参数键
    private static final String ARG_IS_SYSTEM_APP = "is_system_app";

    /**
     * 创建新的 AppInfoDialogFragment 实例，并设置参数。
     *
     * @param appName       应用程序名称
     * @param packageName   应用程序包名
     * @param isSystemApp   是否是系统应用的标志
     * @return 返回一个设置了参数的新实例
     */
    public static AppInfoDialogFragment newInstance(String appName, String packageName, boolean isSystemApp) {
        AppInfoDialogFragment fragment = new AppInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_NAME, appName); // 设置应用名称参数
        args.putString(ARG_PACKAGE_NAME, packageName); // 设置包名参数
        args.putBoolean(ARG_IS_SYSTEM_APP, isSystemApp); // 设置是否是系统应用的参数
        fragment.setArguments(args); // 将参数设置到 Fragment
        return fragment;
    }

    /**
     * 创建对话框时调用的方法。
     * 从参数中获取应用程序信息，并构建相应的对话框。
     *
     * @param savedInstanceState 保存的实例状态
     * @return 返回构建好的对话框
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 从参数中获取应用程序的名称、包名和是否是系统应用的标志
        String appName = getArguments().getString(ARG_APP_NAME);
        String packageName = getArguments().getString(ARG_PACKAGE_NAME);
        boolean isSystemApp = getArguments().getBoolean(ARG_IS_SYSTEM_APP);

        // 创建对话框构建器，并设置标题和消息
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(appName) // 设置对话框标题为应用名称
                .setMessage("Package: " + packageName) // 设置对话框内容显示包名
                .setNegativeButton("Cancel", null); // 设置取消按钮

        // 根据应用程序是否为系统应用设置对话框的正面按钮
        if (isSystemApp) {
            builder.setPositiveButton("Remove", (dialog, which) -> removeAppIcon(packageName)); // 如果是系统应用，提供移除图标的选项
        } else {
            builder.setPositiveButton("Uninstall", (dialog, which) -> uninstallApp(packageName)); // 如果不是系统应用，提供卸载的选项
        }

        return builder.create(); // 创建并返回对话框
    }

    /**
     * 卸载指定包名的应用程序。
     *
     * @param packageName 要卸载的应用程序的包名
     */
    private void uninstallApp(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE); // 创建删除应用程序的 Intent
        intent.setData(Uri.parse("package:" + packageName)); // 设置要删除的应用程序包名
        startActivity(intent); // 启动卸载应用程序的活动
    }

    /**
     * 从列表中移除指定包名的应用图标。
     *
     * @param packageName 要移除的应用程序的包名
     */
    private void removeAppIcon(String packageName) {
        Log.d("JOKER", "removeAppIcon: "); // 打印调试信息
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class); // 获取 ViewModel 实例
        viewModel.removeAppFromList(packageName); // 调用 ViewModel 方法从列表中移除应用图标
    }
}
