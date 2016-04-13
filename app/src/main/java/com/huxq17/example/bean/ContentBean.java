package com.huxq17.example.bean;

import com.huxq17.example.base.BaseBean;

import java.io.Serializable;

/**
 * Created by huxq17 on 2016/4/13.
 */
public class ContentBean extends BaseBean implements Serializable{
    private int imagewidth;
    private int imageheight;
    private String url;
    private int order;
    private int groupid;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImagewidth() {
        return imagewidth;
    }

    public void setImagewidth(int imagewidth) {
        this.imagewidth = imagewidth;
    }

    public int getImageheight() {
        return imageheight;
    }

    public void setImageheight(int imageheight) {
        this.imageheight = imageheight;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public int getPage() {
        return 0;
    }
}
