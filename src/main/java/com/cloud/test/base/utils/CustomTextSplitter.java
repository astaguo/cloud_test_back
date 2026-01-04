package com.cloud.test.base.utils;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;
import java.util.regex.PatternSyntaxException;

public class CustomTextSplitter extends TextSplitter {
    @Override
    protected List<String> splitText(String text) {
        return List.of(split(text));
    }


    public String[] split(String text) {
        // 这里可以实现你自己的复杂切分逻辑
        // 为简化示例，我们按连续的换行符进行分割
        try {
            return text.split("\\s*\\R\\s*\\R\\S*");
        } catch (PatternSyntaxException e) {
            // 处理正则表达式错误
            throw new IllegalArgumentException("Invalid regex pattern", e);
        }

    }
}
