package com.huxq17.example.http.request;

import android.text.TextUtils;

import com.andbase.tractor.utils.LogUtils;
import com.huxq17.example.http.body.FileBody;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * Created by huxq17 on 2015/11/26.
 */
public class RequestParams {
    private String contentType = "application/x-www-form-urlencoded";
    private String charSet = "utf-8";
    private String stringParams;

    private LinkedHashMap<String, Object> mParams;
    private List<FileBody> mFiles;

    public RequestParams() {
        mParams = new LinkedHashMap<>();
        mFiles = new ArrayList<>();
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(stringParams) &&
                (mParams == null || mParams != null && mParams.size() == 0) && (mFiles == null || mFiles != null && mFiles.size() == 0);
    }

    /**
     * 提交普通参数
     *
     * @param name
     * @param params
     */
    public void addParams(String name, Object params) {
        mParams.put(name, params);
    }

    public void setParams(LinkedHashMap<String, Object> params) {
        mParams = params;
    }

    public String getStringParams() {
        return stringParams;
    }

    public void setStringParams(String stringParams) {
        this.stringParams = stringParams;
    }

    /**
     * 提交文件
     *
     * @param file
     */
    public void addFile(File file) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("file==null||!file.exists()");
        }
        FileBody body = new FileBody(file.getName(), file.getAbsolutePath(), file);
        mFiles.add(body);
    }

    /**
     * 提交文件
     *
     * @param name
     */
    public void addFile(String name, File file) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("file==null||!file.exists()");
        }
        FileBody body = new FileBody(name, file.getAbsolutePath(), file);
        mFiles.add(body);
    }

    /**
     * 提交文件
     *
     * @param name
     */
    public void addFile(String name, File file, String contentType) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("file==null||!file.exists()");
        }
        FileBody body = new FileBody(name, file.getAbsolutePath(), file, contentType);
        mFiles.add(body);
    }

    public void clear() {
        mParams.clear();
        mFiles.clear();
    }

    public LinkedHashMap<String, Object> getmParams() {
        return mParams;
    }

    public List<FileBody> getFiles() {
        return mFiles;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getContentType() {
        return contentType;
    }

    public String getCharSet() {
        return charSet;
    }

    @Override
    public String toString() {
        if (!TextUtils.isEmpty(stringParams)) {
            return stringParams;
        }
        StringBuilder sb = new StringBuilder();
        for (LinkedHashMap.Entry set :mParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(set.getKey()).append("=").append(set.getValue());
        }
        LogUtils.d("http params=" + sb.toString());
        return sb.toString();
    }
}
