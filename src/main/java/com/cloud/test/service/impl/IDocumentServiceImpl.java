package com.cloud.test.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cloud.test.domain.RAG;
import com.cloud.test.mapper.RAGMapper;
import com.cloud.test.service.IDocumentService;
import com.cloud.test.utils.CustomTextSplitter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class IDocumentServiceImpl implements IDocumentService {

    @Autowired
    public RAGMapper ragMapper;

    private final RedisVectorStore redisVectorStore;

    public IDocumentServiceImpl (RedisVectorStore redisVectorStore) {
        this.redisVectorStore = redisVectorStore;
    }

    @Override
    public boolean loadText(Resource resource, String fileName) { // 改成无参方法，固定读document.txt
        try {
            // 检查文件是否重复，重复的话就不需要在上传了， 直接返回。
            QueryWrapper<RAG> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", fileName);
            RAG rag = ragMapper.selectOne(queryWrapper);

            if(Objects.isNull(rag)) {
                // 1. 读取文档
                TextReader textReader = new TextReader(resource);
                textReader.getCustomMetadata().put("fileName", fileName);
                List<Document> documents = textReader.get();

                // 2. 切割文档
                CustomTextSplitter customTextSplitter = new CustomTextSplitter();
                List<Document> list = customTextSplitter.apply(documents);

                // 3. 存入Redis向量库
                redisVectorStore.add(list);

                // 4. 获取所有的id
                List<String> ids = list.stream().map(Document::getId).collect(Collectors.toList());

                // 5. 存到数据库中
                RAG newRAG = new RAG();
                newRAG.setName(fileName);
                newRAG.setRedisIds(JSON.toJSONString(ids));
                ragMapper.insert(newRAG);
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException("加载document.txt失败：" + e.getMessage());
        }
    }

    @Override
    public List<RAG> getRagList() {
        return ragMapper.selectList(null);
    }

    // 适配你的RedisVectorStore版本：先查ID再删除
    public void deleteDocumentTxt(Integer id) {
        try {
            // 通过ids， 然后删除数据
            RAG rag = ragMapper.selectById(id);
            List<String> ids = JSON.parseArray(rag.getRedisIds(), String.class);
            redisVectorStore.delete(ids);
            ragMapper.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("删除document.txt失败：" + e.getMessage());
        }
    }

    @Override
    public List<Document> doSearch(String message) {
        try {
            // 调用Redis向量库检索，返回匹配的Document列表
            return redisVectorStore.similaritySearch(SearchRequest.builder().query(message).topK(5).build());
        } catch (Exception e) {
            throw new RuntimeException("检索document失败：" + e.getMessage());
        }
    }
}
