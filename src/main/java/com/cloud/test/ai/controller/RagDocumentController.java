package com.cloud.test.ai.controller;

import com.cloud.test.ai.service.IDocumentService;
import com.cloud.test.base.utils.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "知识库",description = "知识库操作接口")
@RestController
@RequestMapping("/rag")
public class RagDocumentController {

    @Autowired
    public IDocumentService documentService;

    @Operation(summary = "上传知识库文件",description = "上传知识库文件")
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) {
        boolean result = documentService.loadText(file.getResource(),file.getOriginalFilename());
        return AjaxResult.me().setSuccess(result);
    }

    @Operation(summary = "获取知识库列表",description = "获取知识库列表")
    @GetMapping("/list")
    public AjaxResult getRagList() {
        return AjaxResult.me().setResultObj(documentService.getRagList());
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult deleteRag(@PathVariable("id") Integer id) {
        documentService.deleteDocumentTxt(id);
        return AjaxResult.me();
    };
}
