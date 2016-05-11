package com.huxq17.swipecardsview;

import android.view.View;

/**
 * Created by huxq17 on 2016/4/7.
 */
public abstract class BaseCardAdapter<T> {
    /**
     * 获取卡片的数量
     *
     * @return
    */
    public abstract int getCount();

    /**
     * 获取卡片view的layout id
     *
     * @return
     */
    public abstract int getCardLayoutId();

    /**
     * 将卡片和数据绑定在一起
     *
     * @param position 数据在数据集中的位置
     * @param cardview 要绑定数据的卡片
     */
    public abstract void onBindData(int position, View cardview);

    /**
     * 获取可见的cardview的数目，默认是3
     * @return
     */
    public int getVisibleCardCount() {
        return 3;
    }
}
