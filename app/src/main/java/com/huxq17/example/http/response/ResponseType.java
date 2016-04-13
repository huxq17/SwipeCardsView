package com.huxq17.example.http.response;

/**
 * 网络请求返回的类型
 *
 * <p>HttpRequest.Builder builder = new HttpRequest.Builder();
 * <p>builder.url(url);
 * <p>addHeaders(builder, headers);
 * <p>//网络请求返回的默认类型就是string,如下载文件和加载图片需要用到InputStream，则设置为ResponseType.InputStream
 * <p>builder.setResponseType(ResponseType.InputStream);
 * <p>mHttpBase.get(builder.build(), listener, tag);
 */
public enum ResponseType {
    String("String"), InputStream("InputStream");
    private final String type;

    ResponseType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}