package com.huxq17.example.base;

import android.content.Context;

import com.andbase.tractor.listener.impl.LoadListenerImpl;
import com.andbase.tractor.task.Task;
import com.andbase.tractor.task.TaskPool;
import com.andbase.tractor.utils.LogUtils;
import com.huxq17.example.bean.ContentBean;
import com.huxq17.example.bean.MeiziBean;
import com.huxq17.example.http.HttpSender;
import com.huxq17.example.http.response.HttpResponse;
import com.huxq17.example.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by huxq17 on 2016/1/26.
 */
public abstract class BasePresenter<T extends BaseBean, F extends UltraPagerFragment> {
    protected F fragment;
    private Class<T> entityClass;
    private boolean isFirst = true;
    private List<ContentBean> firstList = new ArrayList<>();

    public BasePresenter(F fragment) {
        this.fragment = fragment;
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }

    public void dettach() {
        fragment = null;
    }


    public abstract int getCacheKey();

    public void getData(final String url, final Context context, final int page, final Object tag) {
        LoadListenerImpl listener = new LoadListenerImpl(context) {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                List<ContentBean> lists = (List<ContentBean>) result;
                if (fragment != null) {
                    fragment.onDataResponse(lists, true);
                }
            }

            @Override
            public void onFail(Object result) {
                super.onFail(result);
                String msg = (String) result;
                if (fragment != null) {
                    fragment.onDataFailed(msg);
                }
            }

            @Override
            public void onLoading(Object result) {
                super.onLoading(result);
                List<ContentBean> contentBean = (List<ContentBean>) result;
                if (fragment != null) {
                    fragment.onDataLoading(contentBean);
                }
                dimiss();
            }

            @Override
            public void onCancelClick() {
                super.onCancelClick();
//                TaskPool.getInstance().cancelTask(tag);
            }
        };
        listener.setDismissTime(0);
        TaskPool.getInstance().execute(new Task(tag, listener) {
            @Override
            public void onRun() {
                HttpResponse httpResponse = HttpSender.instance().getSync(url, null, null, tag);
                String html = httpResponse.string();
                if (html != null) {
                    List<ContentBean> beans = parserMainBean(this, html, "", tag);
                    if (beans != null && beans.size() > 0) {
                        notifySuccess(beans);
                    } else {
                        notifyFail("数据解析异常");
                    }
                } else {
                    notifyFail("网络异常");
                }
            }

            @Override
            public void cancelTask() {
            }
        });
    }

    private List<ContentBean> parserMainBean(Task task, String html, String type, Object tag) {
        List<ContentBean> contentBeanList = new ArrayList<>();
//        List<MeiziBean> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("li");//.select("a[target]");

        Element aelement;
        Element imgelement;
        for (int i = 7; i < links.size(); i++) {
            imgelement = links.get(i).select("img").first();
            aelement = links.get(i).select("a").first();
            MeiziBean bean = new MeiziBean();
            bean.setOrder(i);

            bean.setTitle(imgelement.attr("alt").toString());
            bean.setType(type);
            bean.setHeight(354);//element.attr("height")
            bean.setWidth(236);
            bean.setImageurl(imgelement.attr("data-original"));
            bean.setUrl(aelement.attr("href"));
            int groupId = Utils.url2groupid(bean.getUrl());
            bean.setGroupid(groupId);//首页的这个是从大到小排序的 可以当做排序依据
//            list.add(bean);
            List<ContentBean> block = getContent(task, bean.getUrl(), groupId, tag);
            if (firstList.size() > 0) {
                block.removeAll(firstList);
            }
//            if(isFirst){
//            }
            task.notifyLoading(block);
            contentBeanList.addAll(block);
        }
        return contentBeanList;
    }

    private List<ContentBean> getContent(Task task, String url, int groupid, Object tag) {
        LogUtils.i("getcontent url="+url);
        List<ContentBean> list = new ArrayList<>();
        HttpResponse httpResponse = HttpSender.instance().getSync(url, null, null, tag);
        String html = httpResponse.string();
        if (html != null) {
            int mcount = getCount(html);
            for (int i = 1; i < mcount + 1; i++) {
                ContentBean content = null;
                content = fetchContent(url + "/" + i, tag);
                if (content != null) {
                    content.setOrder(groupid + i);
                    content.setGroupid(groupid);
                    list.add(content);
                }
                if (list.size() >= 20 && isFirst) {
                    isFirst = false;
                    firstList.addAll(list);
                    task.notifyLoading(list);
                }
            }
        }
        return list;
    }

    private int getCount(String html) {
        Document doc = Jsoup.parse(html);
        Elements pages = doc.select("span");
        Element page = pages.get(10);

        Pattern p = Pattern.compile("[\\d*]");
        Matcher m = p.matcher(page.toString());
        StringBuffer stringBuffer = new StringBuffer();
        while (m.find()) {
            stringBuffer.append(m.group());
        }
        return Integer.parseInt(stringBuffer.toString());
    }

    private ContentBean fetchContent(String url, Object tag) {
        String html;
        HttpResponse httpResponse = HttpSender.instance().getSync(url, null, null, tag);
        if (httpResponse != null) {
            html = httpResponse.string();
            if (html != null) {
                ContentBean content = ParserContent(html);//这里解析获取的HTML文本
                return content;
            }
        }
//
//        //其实这里不用再去解析bitmap，从HTML可以解析到的。。。至于为什么不去解析，我也不知道我当时怎么想的。。
//        Response response = client.newCall(new Request.Builder().url(content.getUrl()).build()).execute();
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(response.body().byteStream(), null, options);
//        content.setImagewidth(options.outWidth);
//        content.setImageheight(options.outHeight);

        return null;
    }

    private ContentBean ParserContent(String html) {
        ContentBean content = new ContentBean();
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("img[src~=(?i)\\.(png|jpe?g)]");
        if (links.size() == 0) {
            return null;
        }
        Element element = links.get(0).getElementsByTag("img").first();
        content.setUrl(element.attr("src"));
        content.setTitle(element.attr("alt"));
        return content;
    }
}
