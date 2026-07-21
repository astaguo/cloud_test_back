package com.cloud.test.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "影响分析结果")
public class ImpactResult {
    @Schema(description = "仓库地址")
    private String repoUrl;
    
    @Schema(description = "变更文件列表")
    private List<ChangedFile> changedFiles;
    
    @Schema(description = "受影响的Controller列表")
    private Set<ControllerInfo> affectedControllers;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "受影响的Controller信息")
    public static class ControllerInfo {
        @Schema(description = "类名")
        private String className;
        
        @Schema(description = "文件路径")
        private String filePath;
    }
}
