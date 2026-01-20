package com.cloud.test.base.utils;

import com.cloud.test.base.exceptions.UserDefinedException;
import com.cloud.test.project.domain.Module;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GithubModuleAnalyzerUtil {

    // 临时目录， 用于克隆GitHub项目
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/github_project/";
    // 匹配Controller文件的正则 (核心模块识别规则)
    private static final Pattern CONTROLLER_PATTERN = Pattern.compile(".*Controller\\.java$");

    /**
     * 分析GitHub Spring Boot项目模块
     * @param requestUrl 请求地址 GitHub 项目地址
     * @return 模块列表
     */
    public List<Module> analyzeGithubProject(String requestUrl) {
        List<Module> moduleVoList = new ArrayList<>();
        File tempDir = new File(TEMP_DIR);

        // 1. 清理并创建临时目录
        if (tempDir.exists()) {
            deleteDirectory(tempDir);
        }
        boolean isMkdir = tempDir.mkdirs();
        if (!isMkdir) throw new UserDefinedException("创建目录失败！");

        // 2. 克隆GitHub项目到本地
        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(requestUrl)
                .setDirectory(tempDir)
                .setCloneAllBranches(false);
        try (Git git = cloneCommand.call()) {
            // 3. 遍历项目目录，识别模块
            Files.walk(Paths.get(TEMP_DIR))
                    .filter(Files::isRegularFile)
                    .filter(path -> CONTROLLER_PATTERN.matcher(path.getFileName().toString()).matches())
                    .forEach(path -> {
                        // 提取模块名 （如UserController -> User）
                        String fileName = path.getFileName().toString();
                        String modelName = fileName.substring(0, fileName.indexOf("Controller"));
                        // 提取相对路径（去掉临时目录前缀）
                        String relativePath = path.toString().replace(TEMP_DIR, "").replace("\\", "/");
                        Module module = new Module();
                        module.setName(modelName);
                        module.setPath(relativePath);
                        // 封装模块信息
                        moduleVoList.add(module);
                    });
        } catch (Exception e) {
            throw new UserDefinedException("分析项目失败：" + e.getMessage());
        }

        return moduleVoList;
    }

    // 递归删除目录
    private boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        return dir.delete();
    }
}
