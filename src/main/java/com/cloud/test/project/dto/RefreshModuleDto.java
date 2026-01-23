package com.cloud.test.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "刷新模块的Dto")
public class RefreshModuleDto {

    @Schema(title = "项目的id")
    private Integer projectId;
}
