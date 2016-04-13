package com.huxq17.example.bean;

import com.huxq17.example.base.BaseBean;

/**
 * Created by huxq17 on 2016/4/11.
 */
public class MeiziBean extends BaseBean{
    private int count;
    private int width;
    private int height;
    private String imageurl;
    private String url;
    private String title;
    private String type;
    private int groupid;
    private int order;

    @Override
    public int getStatus() {
        return 0;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int getPage() {
        return 0;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getGroupid() {
        return groupid;
    }

    public int getOrder() {
        return order;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
