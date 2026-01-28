package com.cloud.test.project.service;

import com.cloud.test.project.vo.ChangedFile;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IGitService {
    void cloneRepository(String repoUrl, File destination) throws GitAPIException;

    List<ChangedFile> getDiff(File repoDir, String oldCommitId, String newCommitId) throws IOException, GitAPIException;

    List<ChangedFile> getDiffLastCommit(File repoDir) throws IOException, GitAPIException;
}
