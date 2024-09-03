    package com.java.launcher.view;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.GridLayoutManager;
    import androidx.recyclerview.widget.ItemTouchHelper;
    import androidx.recyclerview.widget.RecyclerView;

    import com.java.launcher.R;
    import com.java.launcher.adapter.AppAdapter;
    import com.java.launcher.adapter.AppViewPagerAdapter;
    import com.java.launcher.helper.DragItemTouchHelperCallback;
    import com.java.launcher.model.AppModel;

    import java.util.ArrayList;

    /**
     * BaseFragment 是一个抽象类，用于包含 AppFragment 和 MainFragment 的公共逻辑。
     */
    public abstract class BaseFragment extends Fragment {

        protected static final String ARG_APPS = "apps"; // 存储应用程序列表的参数名
        protected ArrayList<AppModel> apps; // 应用程序列表
        protected int currentPosition; // 当前页面的位置
        protected AppViewPagerAdapter viewPagerAdapter; // ViewPager 的适配器
        protected RecyclerView recyclerView;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 从参数中获取应用程序列表
            if (getArguments() != null) {
                apps = getArguments().getParcelableArrayList(ARG_APPS); // 获取应用程序列表
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(getLayoutResourceId(), container, false);

            recyclerView = view.findViewById(R.id.recyclerView); // 获取 RecyclerView 组件

            return view; // 返回 Fragment 的视图
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // 确保 viewPagerAdapter 已经被设置
            if (viewPagerAdapter == null) {
                throw new IllegalStateException("ViewPagerAdapter must be set before calling setupRecyclerView()");
            }

            setupRecyclerView(); // 在 onViewCreated 中调用 setupRecyclerView
        }

        /**
         * 设置 RecyclerView 和拖拽功能
         */
        protected void setupRecyclerView() {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4)); // 设置 GridLayoutManager，每行 4 列
            AppAdapter appAdapter = new AppAdapter(((MainActivity) getActivity()).getViewPager(), viewPagerAdapter); // 创建 AppAdapter 实例
            appAdapter.setApps(apps); // 设置应用程序列表
            recyclerView.setAdapter(appAdapter); // 设置 RecyclerView 的适配器

            ItemTouchHelper.Callback callback = new DragItemTouchHelperCallback(
                    appAdapter, // 当前页面的适配器
                    viewPagerAdapter, // ViewPager 的适配器
                    recyclerView, // 当前的 RecyclerView 实例
                    currentPosition, // 当前页面的位置
                    viewPagerAdapter.getItemCount(), // ViewPager 的页面数量
                    ((MainActivity) getActivity()).getViewPager()
            );
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback); // 创建 ItemTouchHelper 实例
            touchHelper.attachToRecyclerView(recyclerView); // 将回调附加到 RecyclerView
        }


        /**
         * 子类必须实现该方法，以返回相应的布局资源 ID。
         */
        protected abstract int getLayoutResourceId();
    }
