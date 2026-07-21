package com.cloud.test.project.service;

import com.cloud.test.project.vo.ImpactResult;

public interface IImpactAnalysisService {
    ImpactResult analyze(String repoUrl);
}
