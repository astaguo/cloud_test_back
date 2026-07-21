package com.cloud.test.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.project.domain.Case;
import com.cloud.test.project.dto.RequestInfoDto;
import com.cloud.test.project.vo.ResponseVO;

public interface ICaseService  extends IService<Case> {
    ResponseVO<Object> sendRequest(RequestInfoDto requestInfoDto);

    boolean executionCase(Integer id);
}
