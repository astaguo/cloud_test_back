package com.cloud.test.project;

import com.cloud.test.project.domain.Module;
import com.cloud.test.project.service.impl.IModuleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class IModuleServiceImplTest {

    @Autowired
    public IModuleServiceImpl moduleService;

    @Test
    public void getModuleByGitHubUrlTest() {
        List<Module> moduleByGitHubUrl = moduleService.getModuleByProjectId(1);

        moduleByGitHubUrl.forEach(System.out::println);
    }
}
