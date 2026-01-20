package com.cloud.test.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.base.utils.GithubModuleAnalyzerUtil;
import com.cloud.test.project.domain.Module;
import com.cloud.test.project.domain.Project;
import com.cloud.test.project.mapper.ModuleMapper;
import com.cloud.test.project.mapper.ProjectMapper;
import com.cloud.test.project.service.IModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements IModuleService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ModuleMapper moduleMapper;

    @Override
    public List<Module> refreshModuleByProjectId(Integer projectId) {
        // 1.获取项目类以及它的url
        Project project = projectMapper.selectById(projectId);

        // 2.通过url获取到当前项目的模块
        GithubModuleAnalyzerUtil githubModuleAnalyzerUtil = new GithubModuleAnalyzerUtil();
        List<Module> modules = githubModuleAnalyzerUtil.analyzeGithubProject(project.getProjectUrl());

        // 3.将当前项目所有的模块删除
        QueryWrapper<Module> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        moduleMapper.delete(queryWrapper);

        // 4.插入新的模块
        modules.forEach(module -> {
            module.setProjectId(projectId);
            moduleMapper.insert(module);
        });

        // 5.返回插入后的数据
        return modules;
    }

    @Override
    public List<Module> getModuleByProjectId(Integer projectId) {
        QueryWrapper<Module> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        return moduleMapper.selectList(queryWrapper);
    }
}
