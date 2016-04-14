package com.huxq17.example.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huxq17.example.MainActivity;
import com.huxq17.example.R;
import com.huxq17.example.adapter.MeiziAdapter;
import com.huxq17.example.base.UltraPagerFragment;
import com.huxq17.example.bean.ContentBean;
import com.huxq17.example.constants.Constants;
import com.huxq17.example.presenter.MeiziPresenter;
import com.huxq17.swipecardsview.SwipeCardsView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxq17 on 2016/4/11.
 */
public class MeiziFragment extends UltraPagerFragment<ContentBean, MeiziPresenter> {
    private MainActivity activity;
    private SwipeCardsView swipeCardsView;
    private int page = 1;
    private List<ContentBean> mList = new ArrayList<>();
    private MeiziAdapter adapter;

    public MeiziFragment() {
    }

    public static MeiziFragment getInstance() {
        MeiziFragment fragment = new MeiziFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container = (ViewGroup) inflater.inflate(R.layout.fragment_meizi, container, false);
        Toolbar toolbar = (Toolbar) container.findViewById(R.id.toolbar);
        swipeCardsView = (SwipeCardsView) container.findViewById(R.id.swipCardsView);
        activity = (MainActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        getData();
        return container;
    }

    public void getData() {
        getData(Constants.CoverUrl + page, activity, this);
    }

    @Override
    public void dealDataResponse(List<ContentBean> bean, boolean success) {
        mList = bean;
        show();
    }

    @Override
    public void dealDataLoading(List<ContentBean> beans) {
        mList.addAll(beans);
        show();
    }

    private void show() {
        if (adapter == null) {
            adapter = new MeiziAdapter(mList, getActivity());
            swipeCardsView.setAdapter(adapter);
        }else{
            swipeCardsView.notifyDatasetChanged(mList);
        }
    }

    @Override
    public void stopRefresh() {

    }

    @Override
    public void dealRefreshDataFail(String msg) {

    }

    @Override
    public void dealLoadMoreDataFail(String msg) {
    }

    @Override
    public void dealAddFooter(List<ContentBean> beans) {

    }

    @Override
    public void dealPageSelected(int position) {

    }
}
