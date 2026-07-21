package com.cloud.test.utils;

import com.cloud.test.base.utils.HttpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpUtils 测试类
 */
@SpringBootTest
public class HttpUtilsTest {

    // 测试接口地址（免费的 JSON 占位符接口）
    private static final String TEST_BASE_URL = "https://jsonplaceholder.typicode.com";

    /**
     * 测试 GET 请求
     */
    @Test
    public void testDoGet() {
        // 1. 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1); // 查询 id=1 的帖子

        // 2. 发送 GET 请求
        ResponseEntity<String> response = HttpUtils.doGet(
                TEST_BASE_URL + "/posts",
                null, // 无请求头
                params,
                String.class // 响应类型为字符串
        );

        // 3. 打印结果
        System.out.println("GET 请求结果：");
        System.out.println(response);
        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
    }

    /**
     * 测试 POST 请求
     */
    @Test
    public void testDoPost() {
        // 1. 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "测试标题");
        requestBody.put("body", "测试内容");
        requestBody.put("userId", 1);

        // 2. 发送 POST 请求
        ResponseEntity<String> response = HttpUtils.doPost(
                TEST_BASE_URL + "/posts",
                null, // 无请求头
                null, // 无 URL 参数
                requestBody,
                String.class
        );

        // 3. 打印结果
        System.out.println("POST 请求结果：");
        System.out.println(response);
    }

    /**
     * 测试 PUT 请求
     */
    @Test
    public void testDoPut() {
        // 1. 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", 1);
        requestBody.put("title", "修改后的标题");
        requestBody.put("body", "修改后的内容");
        requestBody.put("userId", 1);

        // 2. 发送 PUT 请求
        ResponseEntity<String> response = HttpUtils.doPut(
                TEST_BASE_URL + "/posts/1",
                null,
                null,
                requestBody,
                String.class
        );

        // 3. 打印结果
        System.out.println("PUT 请求结果：");
        System.out.println(response);
    }

    /**
     * 测试 DELETE 请求
     */
    @Test
    public void testDoDelete() {
        // 发送 DELETE 请求
        ResponseEntity<String> response = HttpUtils.doDelete(
                TEST_BASE_URL + "/posts/1",
                null,
                null,
                String.class
        );

        // 打印结果
        System.out.println("DELETE 请求结果：");
        System.out.println(response);
    }
}