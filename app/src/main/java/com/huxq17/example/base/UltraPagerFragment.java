package com.huxq17.example.base;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by huxq17 on 2016/1/18.
 */
public abstract class UltraPagerFragment<T extends BaseBean, P extends BasePresenter> extends BaseFragment {
    private P presenter;
    /**
     * 下一页数据的页数，用于加载下一页数据
     */
    private int page;
    /**
     * 数据有没有加载成功，如果成功了就不去联网获取数据
     */
    private boolean hasLoad;

    public P getPresenter() {
        if (presenter == null) {
            presenter = createInstance();
        }
        return presenter;
    }

    private P createInstance() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Class cls = (Class) params[1];
        P present = null;
        try {
            Constructor c1 = cls.getDeclaredConstructor(new Class[]{getClass()});
            c1.setAccessible(true);
            present = (P) c1.newInstance(new Object[]{this});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return present;
    }

    /**
     * 数据加载失败的回调
     *
     * @param msg 错误信息
     */
    public final void onDataFailed(String msg) {
        setLoad(false);
        if (page == 0) {
            dealRefreshDataFail(msg);
        } else {
            dealLoadMoreDataFail(msg);
        }
        stopRefresh();
    }


//    @Override
//    public final void onPageSelected(int position) {
//        if (getActivity() != null && !hasLoad()) {
//            dealPageSelected(position);
//        }
//    }

    public abstract void stopRefresh();

    /**
     * 数据加载成功的回调
     *
     * @param beans    获取数据的实体类集合
     * @param success 获取数据是否成功，true代表成功获取了网络的最新数据，false代表获取的是本地缓存
     */
    public final void onDataResponse(List<T> beans, boolean success) {
        setLoad(success);
        stopRefresh();
        dealDataResponse(beans, success);
    }
    /**
     * 部分数据加载成功的回调
     *
     * @param beans    获取数据的实体类集合
     */
    public final void onDataLoading(List<T> beans) {
        stopRefresh();
        dealDataLoading(beans);
    }

    /**
     * 加载更多的回调
     *
     * @param beans 获取数据的实体类
     */
    public final void addFooterData(List<T> beans) {
        setLoad(true);
        stopRefresh();
        dealAddFooter(beans);
    }

    public int getCurrentPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void getData(String url, final Context context, Object tag) {
        getPresenter().getData(url, context, page, tag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().dettach();
        presenter = null;
        setLoad(false);
    }

    /**
     * 数据有没有加载成功
     *
     * @return
     */
    public boolean hasLoad() {
        return hasLoad;
    }

    public void setLoad(boolean hasLoad) {
        this.hasLoad = hasLoad;
    }

    /**
     * 处理数据刷新失败
     *
     * @param msg 错误信息
     */
    public abstract void dealRefreshDataFail(String msg);

    /**
     * 处理数据加载更多失败
     *
     * @param msg 错误信息
     */
    public abstract void dealLoadMoreDataFail(String msg);

    /**
     * 处理数据加载成功
     *
     * @param bean    获取数据的实体类
     * @param success 获取数据是否成功，true代表成功获取了网络的最新数据，false代表获取的是本地缓存
     */
    public abstract void dealDataResponse(List<T> bean, boolean success);

    /**
     * 处理部分数据加载成功
     *
     * @param bean    获取数据的实体类
     */
    public abstract void dealDataLoading(List<T> bean);

    /**
     * 处理加载更多
     *
     * @param beans 获取数据的实体类
     */
    public abstract void dealAddFooter(List<T> beans);

    /**
     * 处理当前页被选中的事件
     *
     * @param position 当前fragment所在的位置
     */
    public abstract void dealPageSelected(int position);
}