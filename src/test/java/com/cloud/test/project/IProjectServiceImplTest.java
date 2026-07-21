package com.cloud.test.project;

import com.cloud.test.project.service.IProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class IProjectServiceImplTest {

    @Autowired
    private IProjectService projectService;

    @Test
    public void testDemo() throws Exception {
//        LocalControllerAnalyzer analyzer = new LocalControllerAnalyzer();
//        // 替换为实际的GitHub仓库地址（需带.git后缀）
//        String result = analyzer.analyzeLocalControllerChain("https://github.com/astaguo/cloud_test_back");
//        System.out.println(result);
    }
}
