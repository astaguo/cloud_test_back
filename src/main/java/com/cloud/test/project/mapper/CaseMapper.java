package com.cloud.test.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.test.project.domain.Case;
import org.apache.ibatis.annotations.Param;

public interface CaseMapper extends BaseMapper<Case> {
    String getEnvironmentByCaseId(Integer caseId);

    void setEnvironmentByCaseId(@Param("caseId") Integer caseId,
                                @Param("environment") String environment);
}
