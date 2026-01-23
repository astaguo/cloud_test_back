package com.cloud.test.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.project.domain.Module;

import java.util.List;

public interface IModuleService extends IService<Module> {

    List<Module> refreshModuleByProjectId(Integer projectId);

    List<Module> getModuleByProjectId(Integer projectId);
}
