package com.huxq17.example.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.huxq17.example.BuildConfig;
import com.huxq17.example.R;
import com.huxq17.example.bean.ContentBean;
import com.huxq17.swipecardsview.BaseCardAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by huxq17 on 2016/4/12.
 */
public class MeiziAdapter extends BaseCardAdapter {
    private List<ContentBean> datas;
    private Context context;

    public MeiziAdapter(List<ContentBean> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    public void setData(List<ContentBean> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.card_item;
    }

    @Override
    public void onBindData(int position, View cardview) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        ImageView imageView = (ImageView) cardview.findViewById(R.id.iv_meizi);
        ContentBean meizi = datas.get(position);
        String url = BuildConfig.isFake ? "file:///android_asset/fake/laoer.png" : meizi.getUrl();
        Bitmap.Config config = BuildConfig.isFake ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Picasso.with(context).load(url).config(config).into(imageView);
    }

    /**
     * 如果可见的卡片数是3，则可以不用实现这个方法
     *
     * @return
     */
    @Override
    public int getVisibleCardCount() {
        return 3;
    }
}
