package com.huxq17.example.http.body;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * Created by huxq17 on 2015/11/26.
 */
public class FileBody {
    /* 上传文件的数据 */
    private byte[] data;
    private File file;
    /* 文件路径 */
    private String filePath;
    /* 请求参数名称*/
    private String parameterName;
    /* 内容类型 */
    private String contentType = "application/octet-stream";

    public FileBody(String parameterName, byte[] data, String filePath, String contentType) {
        this.data = data;
        this.filePath = filePath;
        this.parameterName = parameterName;
        if (contentType != null) {
            this.contentType = contentType;
        } else {
            this.contentType = getContentType(filePath);
        }
    }

    private String getContentType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    public FileBody(String parameterName, String filePath, File file) {
        this(parameterName,filePath,file,null);
    }

    public FileBody(String parameterName, String filePath, File file, String contentType) {
        this.filePath = filePath;
        this.parameterName = parameterName;
        this.file = file;
        if (contentType != null) {
            this.contentType = contentType;
        } else {
            this.contentType = getContentType(filePath);
        }
    }

    public File getFile() {
        return file;
    }

    public byte[] getData() {
        return data;
    }

    public String getFilPath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
