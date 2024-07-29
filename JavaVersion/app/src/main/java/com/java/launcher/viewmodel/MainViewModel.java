package com.java.launcher.viewmodel;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.java.launcher.model.AppModel;

import java.util.ArrayList;
import java.util.List;

/**
 * MainViewModel 是一个用于管理应用程序列表的 ViewModel。
 * 它负责从系统中加载已安装的应用程序，并通过 LiveData 提供这些应用程序的信息。
 */
public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<AppModel>> appsLiveData; // 存储应用列表的 LiveData

    /**
     * 构造函数，初始化 MainViewModel 并加载已安装的应用程序。
     *
     * @param application 应用程序的 Application 对象
     */
    public MainViewModel(@NonNull Application application) {
        super(application);
        appsLiveData = new MutableLiveData<>(); // 初始化 MutableLiveData
        loadInstalledApps(); // 初始化时加载已安装的应用程序
    }

    /**
     * 获取应用列表的 LiveData。
     *
     * @return 返回存储应用程序列表的 LiveData
     */
    public LiveData<List<AppModel>> getAppsLiveData() {
        return appsLiveData;
    }

    /**
     * 加载已安装的应用程序，并将其转换为 AppModel 列表。
     * 仅包括可启动的应用程序。
     */
    private void loadInstalledApps() {
        PackageManager pm = getApplication().getPackageManager(); // 获取 PackageManager 实例
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA); // 获取已安装的应用程序列表
        List<AppModel> appModels = new ArrayList<>(); // 用于存储 AppModel 对象的列表

        // 遍历所有已安装的应用程序
        for (ApplicationInfo app : apps) {
            // 仅处理可启动的应用程序
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                String appName = (String) pm.getApplicationLabel(app); // 获取应用程序的名称
                Drawable icon = pm.getApplicationIcon(app); // 获取应用程序的图标
                // 判断应用程序是否为系统应用
                boolean isSystemApp = (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                // 创建 AppModel 对象并添加到列表中
                appModels.add(new AppModel(appName, app.packageName, icon, isSystemApp));
            }
        }
        appsLiveData.setValue(appModels); // 更新 LiveData 的值
    }

    /**
     * 从应用列表中移除指定包名的应用程序。
     *
     * @param packageName 要移除的应用程序的包名
     */
    public void removeAppFromList(String packageName) {
        Log.d("JOKER", "removeAppFromList: "); // 打印调试信息
        List<AppModel> currentList = appsLiveData.getValue(); // 获取当前的应用程序列表
        if (currentList != null) {
            List<AppModel> updatedList = new ArrayList<>(currentList); // 创建当前列表的副本
            // 从副本列表中移除包名匹配的应用程序
            updatedList.removeIf(app -> app.getPackageName().equals(packageName));
            appsLiveData.setValue(updatedList); // 更新 LiveData 的值
        }
    }
}
