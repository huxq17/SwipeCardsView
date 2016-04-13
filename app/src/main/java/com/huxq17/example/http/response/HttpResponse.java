package com.huxq17.example.http.response;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by huxq17 on 2015/11/28.
 */
public class HttpResponse implements Serializable {
    private String string;
    private long contentLength;
    private InputStream inputStream;
    private ResponseType type;

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void setInputStream(InputStream inputStream) {
        check(ResponseType.InputStream);
        this.inputStream = inputStream;
    }

    private void check(ResponseType requstType) {
        if (requstType != type) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("response type mismatch,you need type is ").append(requstType)
                    .append(" ，but your configuration is ").append(type).append(" ,so you should config responseType like this:");
            switch (requstType) {
                case String:
                    stringBuffer.append(" builder.setResponseType(ResponseType.String);");
                    break;
                case InputStream:
                    stringBuffer.append(" builder.setResponseType(ResponseType.InputStream);");
                    break;
            }
            throw new RuntimeException(stringBuffer.toString());
        }
    }

    public long getContentLength() {
        return contentLength;
    }

    public InputStream getInputStream() {
        check(ResponseType.InputStream);
        return inputStream;
    }

    public void setResponseType(ResponseType type) {
        this.type = type;
    }

    public void setString(String string) {
        check(ResponseType.String);
        this.string = string;
    }

    public String string() {
        check(ResponseType.String);
        return string;
    }
}
