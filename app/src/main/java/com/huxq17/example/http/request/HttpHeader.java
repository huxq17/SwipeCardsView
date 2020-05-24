package com.huxq17.example.http.request;

import java.util.HashMap;
import java.util.LinkedHashMap;

public final class HttpHeader {
    private HashMap<String, String> mHeader;

    public HttpHeader() {
        mHeader = new HashMap<>();
        mHeader.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
    }
    /**
     * Adds a header with {@code name} and {@code value}. Prefer this method for
     * multiply-valued headers like "Cookie".
     */
    public void addHeader(String name,String value){
       mHeader.put(name,value);
    }
    public void setHeader(LinkedHashMap<String,String> header){
        this.mHeader = header;
    }

    public void removeHeader(String name){
        mHeader.remove(name);
    }
    public HashMap<String,String> getHeaders(){
        return mHeader;
    }
    public void clear(){
        mHeader.clear();
    }
}