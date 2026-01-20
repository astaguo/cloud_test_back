package com.cloud.test.base.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PRModuleAnalyzer {

    // 配置项：按需修改
    private static final String REPO_OWNER = "astaguo";
    private static final String REPO_NAME = "cloud_test_back";
    private static final String CONTROLLER_PACKAGE_PREFIX = "com.cloud.test"; // Controller包名前缀
    private static final String LOCAL_REPO_PATH = "./temp-git-repo"; // 本地临时克隆仓库路径

    /**
     * 备选：本地克隆仓库后，对比分支diff获取变更（无GitHub API时使用）
     */
    private List<String> getLocalRepoChangedFiles(String branch1, String branch2) throws GitAPIException, IOException {
        // 克隆仓库（首次执行）
        if (!new File(LOCAL_REPO_PATH + "/.git").exists()) {
            Git.cloneRepository()
                    .setURI("https://github.com/" + REPO_OWNER + "/" + REPO_NAME + ".git")
                    .setDirectory(new File(LOCAL_REPO_PATH))
                    .call()
                    .close();
        }

        // 打开仓库
        Repository repo = new FileRepositoryBuilder()
                .setGitDir(new File(LOCAL_REPO_PATH + "/.git"))
                .build();

        // 构建两个分支的树迭代器
        AbstractTreeIterator oldTree = prepareTreeParser(repo, branch1);
        AbstractTreeIterator newTree = prepareTreeParser(repo, branch2);

        // 获取diff
        List<DiffEntry> diffs = new Git(repo).diff()
                .setOldTree(oldTree)
                .setNewTree(newTree)
                .call();

        // 筛选Controller文件
        List<String> changedFiles = new ArrayList<>();
        DiffFormatter formatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        formatter.setRepository(repo);
        for (DiffEntry entry : diffs) {
            String path = entry.getNewPath();
            if (path.endsWith(".java") && path.contains("controller")
                    && path.startsWith(CONTROLLER_PACKAGE_PREFIX.replace(".", "/"))) {
                changedFiles.add(path);
            }
        }
        formatter.close();
        repo.close();
        return changedFiles;
    }

    // 辅助方法：构建树迭代器
    private AbstractTreeIterator prepareTreeParser(Repository repo, String branchName) throws IOException {
        Ref ref = repo.findRef("refs/heads/" + branchName);
        RevWalk walk = new RevWalk(repo);
        RevCommit commit = walk.parseCommit(ref.getObjectId());
        ObjectId treeId = commit.getTree().getId();
        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        try (var reader = repo.newObjectReader()) {
            treeParser.reset(reader, treeId);
        }
        walk.close();
        return treeParser;
    }
}
