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

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<AppModel>> appsLiveData; // 存储应用列表的LiveData

    public MainViewModel(@NonNull Application application) {
        super(application);
        appsLiveData = new MutableLiveData<>();
        loadInstalledApps(); // 初始化时加载已安装的应用
    }

    // 获取应用列表的LiveData
    public LiveData<List<AppModel>> getAppsLiveData() {
        return appsLiveData;
    }

    // 加载已安装的应用
    private void loadInstalledApps() {
        PackageManager pm = getApplication().getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA); // 获取已安装的应用列表
        List<AppModel> appModels = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            // 仅处理可启动的应用
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                String appName = (String) pm.getApplicationLabel(app); // 获取应用名称
                Drawable icon = pm.getApplicationIcon(app); // 获取应用图标
                boolean isSystemApp = (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0; // 判断是否是系统应用
                appModels.add(new AppModel(appName, app.packageName, icon, isSystemApp)); // 创建AppModel对象并添加到列表
            }
        }
        appsLiveData.setValue(appModels); // 更新LiveData的值
    }

    // 从列表中移除指定包名的应用
    public void removeAppFromList(String packageName) {
        Log.d("JOKER", "removeAppFromList: ");
        List<AppModel> currentList = appsLiveData.getValue(); // 获取当前应用列表
        if (currentList != null) {
            List<AppModel> updatedList = new ArrayList<>(currentList);
            updatedList.removeIf(app -> app.getPackageName().equals(packageName)); // 从列表中移除匹配的应用
            appsLiveData.setValue(updatedList); // 更新LiveData的值
        }
    }
}
