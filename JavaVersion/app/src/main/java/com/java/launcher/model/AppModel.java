package com.java.launcher.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * AppModel 是一个数据模型类，用于存储应用程序的相关信息。
 * 实现 Parcelable 接口，使其能够在组件之间传递。
 */
public class AppModel implements Parcelable {
    private String appName;       // 应用程序名称
    private String packageName;   // 应用程序包名
    private Drawable icon;        // 应用程序图标
    private boolean isSystemApp;  // 是否为系统应用

    /**
     * 构造函数，用于初始化应用程序的信息。
     *
     * @param appName     应用程序名称
     * @param packageName 应用程序包名
     * @param icon        应用程序图标
     * @param isSystemApp 是否为系统应用
     */
    public AppModel(String appName, String packageName, Drawable icon, boolean isSystemApp) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.isSystemApp = isSystemApp;
    }

    /**
     * 从 Parcel 中创建 AppModel 实例的构造函数。
     *
     * @param in Parcel 对象
     */
    protected AppModel(Parcel in) {
        appName = in.readString();
        packageName = in.readString();
        isSystemApp = in.readByte() != 0;
    }

    /**
     * Parcelable 接口的 CREATOR 静态字段，用于反序列化 AppModel 对象。
     */
    public static final Creator<AppModel> CREATOR = new Creator<AppModel>() {
        @Override
        public AppModel createFromParcel(Parcel in) {
            return new AppModel(in);
        }

        @Override
        public AppModel[] newArray(int size) {
            return new AppModel[size];
        }
    };

    /**
     * 获取应用程序名称。
     *
     * @return 应用程序名称
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 获取应用程序包名。
     *
     * @return 应用程序包名
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 获取应用程序图标。
     *
     * @return 应用程序图标
     */
    public Drawable getIcon() {
        return icon;
    }

    /**
     * 判断是否为系统应用。
     *
     * @return 如果是系统应用返回 true，否则返回 false
     */
    public boolean isSystemApp() {
        return isSystemApp;
    }

    /**
     * 描述内容接口方法，通常返回 0。
     *
     * @return 内容描述的标志，通常返回 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将 AppModel 对象写入 Parcel，以便序列化。
     *
     * @param dest  Parcel 对象
     * @param flags 标志
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeByte((byte) (isSystemApp ? 1 : 0));
    }
}
