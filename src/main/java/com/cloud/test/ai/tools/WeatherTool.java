package com.cloud.test.ai.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class WeatherTool {

    @Tool(description = "获取传入城市的天气")
    public String getWeatherTool(@ToolParam(description = "这是城市名称") String city) {
        System.out.println("当前城市" + city + ": 多云");
        return "当前城市" + city + ": 多云";
    }
}
