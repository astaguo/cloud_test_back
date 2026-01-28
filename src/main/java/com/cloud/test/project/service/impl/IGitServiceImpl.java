package com.cloud.test.project.service.impl;

import com.cloud.test.project.vo.ChangedFile;
import com.cloud.test.project.service.IGitService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class IGitServiceImpl implements IGitService {

    @Override
    public void cloneRepository(String repoUrl, File destination) throws GitAPIException {
        if (destination.exists() && destination.listFiles() != null && destination.listFiles().length > 0) {
            // Assume already cloned, try pull
            try (Git git = Git.open(destination)) {
                git.pull().call();
            } catch (IOException e) {
                throw new RuntimeException("Failed to open existing repository", e);
            }
        } else {
            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(destination)
                    .call();
        }
    }

    @Override
    public List<ChangedFile> getDiff(File repoDir, String oldCommitId, String newCommitId) throws IOException, GitAPIException {
        try (Git git = Git.open(repoDir); Repository repository = git.getRepository()) {
            ObjectId oldHead = repository.resolve(oldCommitId + "^{tree}");
            ObjectId newHead = repository.resolve(newCommitId + "^{tree}");

            try (ObjectReader reader = repository.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldHead);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, newHead);

                try (DiffFormatter formatter = new DiffFormatter(null)) {
                    formatter.setRepository(repository);
                    List<DiffEntry> diffs = formatter.scan(oldTreeIter, newTreeIter);
                    List<ChangedFile> changedFiles = new ArrayList<>();

                    for (DiffEntry entry : diffs) {
                        ChangedFile.ChangeType type = switch (entry.getChangeType()) {
                            case ADD -> ChangedFile.ChangeType.ADD;
                            case DELETE -> ChangedFile.ChangeType.DELETE;
                            case MODIFY -> ChangedFile.ChangeType.MODIFY;
                            case RENAME -> ChangedFile.ChangeType.RENAME;
                            default -> ChangedFile.ChangeType.MODIFY;
                        };

                        String path = (type == ChangedFile.ChangeType.DELETE) ? entry.getOldPath() : entry.getNewPath();
                        changedFiles.add(new ChangedFile(path, type));
                    }
                    return changedFiles;
                }
            }
        }
    }

    // Default to diff between HEAD and HEAD~1
    @Override
    public List<ChangedFile> getDiffLastCommit(File repoDir) throws IOException, GitAPIException {
        return getDiff(repoDir, "HEAD~1", "HEAD");
    }
}
