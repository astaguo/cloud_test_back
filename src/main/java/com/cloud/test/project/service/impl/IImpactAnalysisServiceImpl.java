package com.cloud.test.project.service.impl;

import com.cloud.test.base.exceptions.UserDefinedException;
import com.cloud.test.project.domain.CallGraph;
import com.cloud.test.project.vo.ChangedFile;
import com.cloud.test.project.vo.ImpactResult;
import com.cloud.test.project.service.ICodeAnalysisService;
import com.cloud.test.project.service.IGitService;
import com.cloud.test.project.service.IImpactAnalysisService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class IImpactAnalysisServiceImpl implements IImpactAnalysisService {

    private final IGitService gitService;
    private final ICodeAnalysisService codeAnalysisService;
    private static final String WORKSPACE_DIR = "workspace_repos";

    public IImpactAnalysisServiceImpl(IGitService gitService, ICodeAnalysisService codeAnalysisService) {
        this.gitService = gitService;
        this.codeAnalysisService = codeAnalysisService;
    }

    @Override
    public ImpactResult analyze(String repoUrl) {
        // 1. Prepare Workspace
        String repoName = repoUrl.substring(repoUrl.lastIndexOf("/") + 1).replace(".git", "");
        File repoDir = new File(WORKSPACE_DIR, repoName);

        try {
            // 2. Clone/Pull
            gitService.cloneRepository(repoUrl, repoDir);

            // 3. Get Diff
            List<ChangedFile> changedFiles = gitService.getDiffLastCommit(repoDir);

            // 4. Build Call Graph
            CallGraph callGraph = codeAnalysisService.analyzeProject(repoDir);

            // 5. Analyze Impact
            Set<ImpactResult.ControllerInfo> affectedControllers = findAffectedControllers(changedFiles, callGraph, repoDir);

            return new ImpactResult(repoUrl, changedFiles, affectedControllers);
        } catch (GitAPIException | IOException e) {
            throw new UserDefinedException(e.getMessage());
        }
    }

    private Set<ImpactResult.ControllerInfo> findAffectedControllers(List<ChangedFile> changedFiles, CallGraph callGraph, File repoDir) {
        Set<ImpactResult.ControllerInfo> results = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visitedMethods = new HashSet<>();

        // 1. Identify initial changed methods/classes
        // We map changed files to classes, then add all methods of those classes to queue
        for (ChangedFile file : changedFiles) {
            String absPath = new File(repoDir, file.getPath()).getAbsolutePath();
            
            // Find class for this file
            // Iterate map because map has absolute paths.
            // Performance note: Inefficient for large projects, but acceptable for tool demo.
            for (Map.Entry<String, String> entry : callGraph.getClassToFileMap().entrySet()) {
                if (entry.getValue().equals(absPath)) {
                    String className = entry.getKey();
                    
                    // Add all methods of this class to queue
                    // We need a way to get all methods of a class. 
                    // Since CallGraph.methodToClassMap is Method->Class, we can iterate it.
                    callGraph.getMethodToClassMap().forEach((methodSig, cls) -> {
                        if (cls.equals(className)) {
                            if (!visitedMethods.contains(methodSig)) {
                                queue.add(methodSig);
                                visitedMethods.add(methodSig);
                            }
                        }
                    });
                    
                    // Also check if the class itself is a controller (direct modification)
                    if (callGraph.getControllerClasses().contains(className)) {
                        results.add(new ImpactResult.ControllerInfo(className, entry.getValue()));
                    }
                }
            }
        }

        // 2. BFS
        while (!queue.isEmpty()) {
            String currentMethod = queue.poll();
            Set<String> callers = callGraph.getCallers(currentMethod);

            for (String caller : callers) {
                if (visitedMethods.contains(caller)) continue;
                
                visitedMethods.add(caller);
                queue.add(caller);

                String callerClass = callGraph.getMethodToClassMap().get(caller);
                if (callerClass != null && callGraph.getControllerClasses().contains(callerClass)) {
                    String filePath = callGraph.getClassToFileMap().get(callerClass);
                    results.add(new ImpactResult.ControllerInfo(callerClass, filePath));
                }
            }
        }

        return results;
    }
}
