package com.cloud.test.base.utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * HTTP 请求工具类（基于 Spring RestTemplate）
 * 适配 Java 17 + Spring Boot 3.5
 * @author Asta
 */
public class HttpUtils {

    // 全局 RestTemplate 实例（线程安全）
    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送 GET 请求
     * @param url 请求地址
     * @param headers 请求头（可为 null）
     * @param params URL 参数（可为 null）
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> ResponseEntity<T> doGet(String url, Map<String, String> headers, Map<String, Object> params, Class<T> responseType) {
        // 构建请求头
        HttpHeaders httpHeaders = buildHeaders(headers);
        // 构建带参数的 URL
        String finalUrl = buildUrlWithParams(url, params);
        // 构建请求实体
        HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);
        
        try {
            return restTemplate.exchange(
                    finalUrl,
                    HttpMethod.GET,
                    requestEntity,
                    responseType
            );
        } catch (Exception e) {
            throw new RuntimeException("GET 请求失败：" + url, e);
        }
    }

    /**
     * 发送 POST 请求（JSON 格式请求体）
     * @param url 请求地址
     * @param headers 请求头（可为 null）
     * @param params URL 参数（可为 null）
     * @param requestBody 请求体（可为 null）
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> ResponseEntity<T> doPost(String url, Map<String, String> headers, Map<String, Object> params, Object requestBody, Class<T> responseType) {
        return doRequest(HttpMethod.POST, url, headers, params, requestBody, responseType);
    }

    /**
     * 发送 PUT 请求（JSON 格式请求体）
     * @param url 请求地址
     * @param headers 请求头（可为 null）
     * @param params URL 参数（可为 null）
     * @param requestBody 请求体（可为 null）
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> ResponseEntity<T> doPut(String url, Map<String, String> headers, Map<String, Object> params, Object requestBody, Class<T> responseType) {
        return doRequest(HttpMethod.PUT, url, headers, params, requestBody, responseType);
    }

    /**
     * 发送 DELETE 请求
     * @param url 请求地址
     * @param headers 请求头（可为 null）
     * @param params URL 参数（可为 null）
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> ResponseEntity<T> doDelete(String url, Map<String, String> headers, Map<String, Object> params, Class<T> responseType) {
        return doRequest(HttpMethod.DELETE, url, headers, params, null, responseType);
    }

    /**
     * 通用请求方法（核心逻辑）
     */
    private static <T> ResponseEntity<T> doRequest(HttpMethod method, String url, Map<String, String> headers,
                                               Map<String, Object> params, Object requestBody, Class<T> responseType) {
        // 构建请求头（默认 JSON 格式）
        HttpHeaders httpHeaders = buildHeaders(headers);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        // 构建带参数的 URL
        String finalUrl = buildUrlWithParams(url, params);
        
        // 构建请求实体
        HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        
        try {
            return restTemplate.exchange(
                    finalUrl,
                    method,
                    requestEntity,
                    responseType
            );
        } catch (Exception e) {
            throw new RuntimeException(method.name() + " 请求失败：" + url, e);
        }
    }

    /**
     * 构建请求头
     */
    private static HttpHeaders buildHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpHeaders::set);
        }
        return httpHeaders;
    }

    /**
     * 构建带参数的 URL
     */
    private static String buildUrlWithParams(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        params.forEach(builder::queryParam);
        return builder.build().toUriString();
    }
}