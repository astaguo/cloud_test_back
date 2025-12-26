package com.cloud.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class APIRequest {

    public String getPageSource(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }

    @Test
    public void sendRequest() {
        APIRequest fetcher = new APIRequest();
        String source = fetcher.getPageSource("https://www.baidu.com");
        System.out.println(source);
    }
}
