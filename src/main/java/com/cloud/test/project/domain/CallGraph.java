package com.cloud.test.project.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.*;

@Data
@Schema(description = "调用图模型 (内部使用)")
public class CallGraph {
    @Schema(description = "反向调用映射: 被调用者 -> 调用者集合")
    private Map<String, Set<String>> reverseCallMap = new HashMap<>();
    
    @Schema(description = "方法签名到类名的映射")
    private Map<String, String> methodToClassMap = new HashMap<>();
    
    @Schema(description = "类名到文件路径的映射")
    private Map<String, String> classToFileMap = new HashMap<>();
    
    @Schema(description = "Controller类集合")
    private Set<String> controllerClasses = new HashSet<>();

    public void addCall(String caller, String callee) {
        reverseCallMap.computeIfAbsent(callee, k -> new HashSet<>()).add(caller);
    }

    public Set<String> getCallers(String callee) {
        return reverseCallMap.getOrDefault(callee, Collections.emptySet());
    }
}
