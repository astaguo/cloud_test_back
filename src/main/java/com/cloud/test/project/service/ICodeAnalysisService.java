package com.cloud.test.project.service;

import com.cloud.test.project.domain.CallGraph;

import java.io.File;
import java.io.IOException;

public interface ICodeAnalysisService {
    CallGraph analyzeProject(File projectRoot) throws IOException;
}
