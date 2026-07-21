package com.cloud.test.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "变更文件信息")
public class ChangedFile {
    @Schema(description = "文件路径")
    private String path;
    
    @Schema(description = "变更类型")
    private ChangeType type;

    public enum ChangeType {
        ADD, MODIFY, DELETE, RENAME
    }
}
