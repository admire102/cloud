package com.igniubi.rest.client;


public interface IRestClient {

    <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType);

    <T> T get(String serviceName, String serviceUrl, Object request, Class<T> responseType);

    <T> AsyncFuture<T> asyncCall(String serviceName, String serviceUrl, Object request, Class<T> responseType);
}
