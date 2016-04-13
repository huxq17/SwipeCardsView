package com.huxq17.example.utils;

/**
 * Created by huxq17 on 2016/4/12.
 */
public class Utils {
    public static int url2groupid(String url) {
        return Integer.parseInt(url.split("/")[3]);
    }
}
