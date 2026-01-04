package com.cloud.test.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestTemplate;


public class WebPageFetcherTool {

    @Tool(description = "获取网页源码")
    public String getPageSource(@ToolParam(description = "这里是url地址") String url) {
        WebPageFetcherTool webPageFetcherTool = new WebPageFetcherTool();
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }
}
