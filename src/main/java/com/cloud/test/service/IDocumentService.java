package com.cloud.test.service;

import com.cloud.test.domain.RAG;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface IDocumentService {

    boolean loadText(Resource resource, String fileName);

    void deleteDocumentTxt(Integer id);

    List<Document> doSearch(String message);

    List<RAG> getRagList();
}
