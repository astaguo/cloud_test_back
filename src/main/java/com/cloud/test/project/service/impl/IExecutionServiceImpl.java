package com.cloud.test.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.project.domain.Execution;
import com.cloud.test.project.mapper.ExecutionMapper;
import com.cloud.test.project.service.IExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IExecutionServiceImpl extends ServiceImpl<ExecutionMapper, Execution> implements IExecutionService {

    @Autowired
    public ExecutionMapper executionMapper;

    @Override
    public List<Execution> listByCaseId(Integer caseId) {
        QueryWrapper<Execution> wrapper = new QueryWrapper<>();
        wrapper.eq("case_id", caseId);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT 20");
        return executionMapper.selectList(wrapper);
    }
}
