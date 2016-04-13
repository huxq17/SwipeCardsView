package com.huxq17.example.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

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

    @Override
    public List getData() {
        return datas;
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.card_item;
    }

    @Override
    public void onBindData(int position, View cardview, Object data) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        ImageView imageView = (ImageView) cardview.findViewById(R.id.iv_meizi);
        ContentBean meizi = datas.get(position);
        String url = meizi.getUrl();
        Picasso.with(context).load(url).config(Bitmap.Config.RGB_565).into(imageView);
    }
}
