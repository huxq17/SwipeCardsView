package com.huxq17.example.presenter;

import com.huxq17.example.base.BasePresenter;
import com.huxq17.example.bean.MeiziBean;
import com.huxq17.example.fragment.MeiziFragment;

/**
 * Created by huxq17 on 2016/4/11.
 */
public class MeiziPresenter extends BasePresenter<MeiziBean,MeiziFragment> {
    public MeiziPresenter(MeiziFragment fragment) {
        super(fragment);
    }

    @Override
    public int getCacheKey() {
        return 0;
    }
}
