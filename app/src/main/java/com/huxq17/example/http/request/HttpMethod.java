package com.huxq17.example.http.request;

/**
 * Created by huxq17 on 2015/11/26.
 */
public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    MOVE("MOVE"),
    COPY("COPY"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static boolean permitsRetry(HttpMethod method) {
        return method == GET;
    }

    public static boolean permitsCache(HttpMethod method) {
        return method == GET || method == POST;
    }

    public static boolean requiresRequestBody(HttpMethod method) {
        return method == POST
                || method == PUT
                || method == PATCH;
    }

    public static boolean permitsRequestBody(HttpMethod method) {
        return requiresRequestBody(method)
                || method==OPTIONS
                || method == DELETE;
    }

    @Override
    public String toString() {
        return this.value;
    }
}