package com.cloud.test.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.user.domain.Permissions;
import com.cloud.test.user.mapper.PermissionsMapper;
import com.cloud.test.user.service.IPermissionsService;
import org.springframework.stereotype.Service;

@Service
public class IPermissionsServiceImpl extends ServiceImpl<PermissionsMapper, Permissions> implements IPermissionsService {
}
