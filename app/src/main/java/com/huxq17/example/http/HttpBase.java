package com.huxq17.example.http;

import com.andbase.tractor.listener.LoadListener;
import com.huxq17.example.http.request.HttpRequest;
import com.huxq17.example.http.response.HttpResponse;


/**
 * http请求的基类
 */
public interface HttpBase {
    /**
     * get请求
     *
     * @param request  http请求
     * @param listener 监听 同步请求的时候这个参数是用不到的，同步请求可以传null
     * @param tag
     * @return 封装了网络请求的响应 同步请求的时候返回值不为null,异步请求的时候总是null
     */
    public HttpResponse get(HttpRequest request, LoadListener listener, Object... tag);

    /**
     * post请求
     *
     * @param request  http请求
     * @param listener 监听 同步请求的时候这个参数是用不到的，同步请求可以传null
     * @param tag
     * @return 封装了网络请求的响应 同步请求的时候返回值不为null,异步请求的时候总是null
     */
    public HttpResponse post(HttpRequest request, LoadListener listener, Object... tag);

    /**
     * get post以外的其他请求
     *
     * @param request  http请求
     * @param listener 监听 同步请求的时候这个参数是用不到的，同步请求可以传null
     * @param tag
     * @return 封装了网络请求的响应 同步请求的时候返回值不为null,异步请求的时候总是null
     */
    public HttpResponse request(HttpRequest request, LoadListener listener, Object... tag);

    /**
     * 取消特定tag的http请求
     * @param tag
     */
    public void cancel(Object... tag);
}