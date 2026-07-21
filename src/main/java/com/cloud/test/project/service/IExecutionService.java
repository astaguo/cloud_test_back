package com.cloud.test.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.project.domain.Execution;

import java.util.List;

public interface IExecutionService extends IService<Execution> {
    List<Execution> listByCaseId(Integer caseId);
}
