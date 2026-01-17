package com.cloud.test.project.vo;

import lombok.Data;

@Data
public class MethodInfo {
        private String className; // 所属类名
        private String methodName; // 方法名
        private String parameters; // 参数列表（简化）
        private String filePath; // 文件路径

        public MethodInfo(String className, String methodName, String parameters, String filePath) {
            this.className = className;
            this.methodName = methodName;
            this.parameters = parameters;
            this.filePath = filePath;
        }

        @Override
        public String toString() {
            return String.format("类：%s | 方法：%s(%s) | 文件：%s", className, methodName, parameters, filePath);
        }
    }