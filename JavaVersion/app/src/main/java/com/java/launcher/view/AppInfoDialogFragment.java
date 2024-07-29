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

public class AppInfoDialogFragment extends DialogFragment {

    // 传递应用名称的参数
    private static final String ARG_APP_NAME = "app_name";
    // 传递包名的参数
    private static final String ARG_PACKAGE_NAME = "package_name";
    // 传递是否是系统应用的参数
    private static final String ARG_IS_SYSTEM_APP = "is_system_app";

    // 创建新的实例，并设置参数
    public static AppInfoDialogFragment newInstance(String appName, String packageName, boolean isSystemApp) {
        AppInfoDialogFragment fragment = new AppInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_NAME, appName);
        args.putString(ARG_PACKAGE_NAME, packageName);
        args.putBoolean(ARG_IS_SYSTEM_APP, isSystemApp);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 从参数中获取应用名称、包名和是否是系统应用的标志
        String appName = getArguments().getString(ARG_APP_NAME);
        String packageName = getArguments().getString(ARG_PACKAGE_NAME);
        boolean isSystemApp = getArguments().getBoolean(ARG_IS_SYSTEM_APP);

        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(appName)
                .setMessage("Package: " + packageName)
                .setNegativeButton("Cancel", null);

        // 根据是否是系统应用设置对话框的按钮
        if (isSystemApp) {
            builder.setPositiveButton("Remove", (dialog, which) -> removeAppIcon(packageName));
        } else {
            builder.setPositiveButton("Uninstall", (dialog, which) -> uninstallApp(packageName));
        }

        return builder.create();
    }

    // 卸载应用
    private void uninstallApp(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        startActivity(intent);
    }

    // 从列表中移除应用图标
    private void removeAppIcon(String packageName) {
        Log.d("JOKER", "removeAppIcon: ");
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.removeAppFromList(packageName);
    }
}
