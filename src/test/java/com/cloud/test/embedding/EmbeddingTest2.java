package com.cloud.test.embedding;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class EmbeddingTest2 {

    @Resource
    private EmbeddingModel model;

    @Test
    public void test2(){
        String content = "你好， 我是一名软件测试工程师";
        Map<String, Object> map = new HashMap<>();
        var vector = model.embed(content);
        map.put("content", content);
        map.put("model", model.getClass().getName());
        map.put("modelName", model.getClass().getSimpleName());
        map.put("modelType", model.getClass().getTypeName());
        map.put("modelPackage", model.getClass().getPackage().getName());
        map.put("modelVersion", model.getClass().getPackage().getImplementationVersion());
        map.put("modelAuthor", model.getClass().getPackage().getImplementationVendor());
        map.put("modelDescription", model.getClass().getPackage().getImplementationTitle());
        map.put("modelUrl", model.getClass().getPackage().getSpecificationVendor());
        map.put("modelLicense", model.getClass().getPackage().getSpecificationTitle());
        map.put("modelCopyright", model.getClass().getPackage().getSpecificationVersion());
        map.put("modelStatus", model.getClass().getPackage().getImplementationVendor());
        map.put("length", vector.length);
        map.put("vector", Arrays.toString(vector));

        map.forEach((k, v) -> System.out.println(k + ":" + v));
    }
}
