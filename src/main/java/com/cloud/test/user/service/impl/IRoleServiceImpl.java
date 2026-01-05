package com.cloud.test.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.user.domain.Role;
import com.cloud.test.user.mapper.RoleMapper;
import com.cloud.test.user.service.IRoleService;
import org.springframework.stereotype.Service;

@Service
public class IRoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
}
