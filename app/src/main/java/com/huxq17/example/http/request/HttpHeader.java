package com.huxq17.example.http.request;

import java.util.HashMap;
import java.util.LinkedHashMap;

public final class HttpHeader {
    private HashMap<String, String> mHeader;

    public HttpHeader() {
        mHeader = new HashMap<>();
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