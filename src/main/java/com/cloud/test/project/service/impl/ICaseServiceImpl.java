package com.cloud.test.project.service.impl;

import com.cloud.test.base.utils.CaseScriptEngineUtil;
import com.cloud.test.project.domain.Execution;
import com.cloud.test.project.mapper.ExecutionMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.base.exceptions.UserDefinedException;
import com.cloud.test.base.utils.HttpUtils;
import com.cloud.test.project.domain.Case;
import com.cloud.test.project.dto.RequestInfoDto;
import com.cloud.test.project.mapper.CaseMapper;
import com.cloud.test.project.service.ICaseService;
import com.cloud.test.project.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ICaseServiceImpl  extends ServiceImpl<CaseMapper, Case> implements ICaseService {
    @Autowired
    private CaseMapper caseMapper;

    @Autowired
    private ExecutionMapper executionMapper;

    // 用于处理JSON字符串和Map类型之间的转换
    final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ResponseVO<Object> sendRequest(RequestInfoDto requestInfoDto) {
        boolean testResult = true;
        ResponseEntity<String> response = null;
        long startTime = 0;
        String errorMsg = "";
        var caseScriptEngineUtil = new CaseScriptEngineUtil();

        try{
            // 1.参数检查
            if (Objects.isNull(requestInfoDto)) throw new UserDefinedException("无效参数");
            String url = requestInfoDto.getUrl();
            String params = mapper.writeValueAsString(requestInfoDto.getRequestParams());
            String headers = mapper.writeValueAsString(requestInfoDto.getRequestHeader());
            String body = mapper.writeValueAsString(requestInfoDto.getRequestBody());

            // 2.查询environment
            var environments = caseMapper.getEnvironmentByCaseId(requestInfoDto.getCaseId());
            // 2.1转换环境变量
            Map<String, String> envs = new HashMap<>();
            if (Objects.nonNull(environments)) {
                envs = mapper.readValue(environments, new TypeReference<>() {});
            }
            // 2.2保留原始变量，便于后面判断是否相等
            Map<String, String> originEnv = new HashMap<>(envs);

            // 3.执行前置脚本，一般是为了准备环境变量
            if(Objects.nonNull(requestInfoDto.getPerScript())){
                caseScriptEngineUtil.runUserScript(requestInfoDto.getPerScript(), envs);
            }

            // 4.设置环境变量到各个参数
            if (Objects.nonNull(environments)) {
                url = replaceEnvPlaceholder(url, envs);
                params = replaceEnvPlaceholder(params, envs);
                headers = replaceEnvPlaceholder(headers, envs);
                body = replaceEnvPlaceholder(body, envs);
            }

            // 5.根据请求类型来确定请求的数据
            startTime = System.nanoTime();
            response = switch (requestInfoDto.getRequestType()) {
                case "GET" -> HttpUtils.doGet(
                        url,
                        mapper.readValue(headers, new TypeReference<>() {}),
                        mapper.readValue(params, new TypeReference<>() {}),
                        String.class
                );
                case "POST" -> HttpUtils.doPost(
                        url,
                        mapper.readValue(headers, new TypeReference<>() {}),
                        mapper.readValue(params, new TypeReference<>() {}),
                        mapper.readValue(body, new TypeReference<>() {}),
                        String.class
                );
                case "PUT" -> HttpUtils.doPut(
                        url,
                        mapper.readValue(headers, new TypeReference<>() {}),
                        mapper.readValue(params, new TypeReference<>() {}),
                        mapper.readValue(body, new TypeReference<>() {}),
                        String.class
                );
                case "DELETE" -> HttpUtils.doDelete(
                        url,
                        mapper.readValue(headers, new TypeReference<>() {}),
                        mapper.readValue(params, new TypeReference<>() {}),
                        String.class
                );
                default -> throw new UserDefinedException("未知类型!");
            };

            // 6.断言内容
            if (Objects.nonNull(requestInfoDto.getExpectState())) {
                testResult = response.getStatusCode().toString().contains(requestInfoDto.getExpectState());
            }

            // 7.执行后置脚本
            if(Objects.nonNull(requestInfoDto.getPostScript())){
                caseScriptEngineUtil.runUserScript(requestInfoDto.getPostScript(), envs, response.getBody());
            }

            // 8.环境变量
            if(!Objects.equals(envs, originEnv)){
                caseMapper.setEnvironmentByCaseId(requestInfoDto.getCaseId(), mapper.writeValueAsString(envs));
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            testResult = false;
        }

        // 8.构建响应内容
        HttpHeaders headerStr = new HttpHeaders();
        String bodyStr = "";
        String statusCode = "";
        if (response != null) {
            // 头部不为空再取
            headerStr = response.getHeaders();
            // body
            bodyStr = response.getBody();
            // 状态码
            statusCode = response.getStatusCode().toString();
        }

        return new ResponseVO<>(
                headerStr,
                bodyStr,
                statusCode,
                String.valueOf(
                        Math.round((double) (System.nanoTime() - startTime) / 1_000_000 * 100) / 100.0
                ),
                testResult,
                errorMsg
        );
    }

    @Override
    public boolean executionCase(Integer id) {
        // 1.获取用例信息，并验证
        Case caseInfo = caseMapper.selectById(id);
        if (Objects.isNull(caseInfo)) throw new UserDefinedException("用例不存在!");

        try{
            // 2.调用接口准备执行
            // 2.1准备参数
            RequestInfoDto requestInfoDto = new RequestInfoDto();
            requestInfoDto.setCaseId(caseInfo.getId());
            requestInfoDto.setUrl(caseInfo.getUrl());
            if(Objects.nonNull(caseInfo.getRequestBody())) {
                requestInfoDto.setRequestBody(mapper.readValue(caseInfo.getRequestBody(), new TypeReference<>() {}));
            }
            if(Objects.nonNull(caseInfo.getRequestHeader())){
                requestInfoDto.setRequestHeader(mapper.readValue(caseInfo.getRequestHeader(), new TypeReference<>() {}));
            }
            if(Objects.nonNull(caseInfo.getRequestParams())){
                requestInfoDto.setRequestParams(mapper.readValue(caseInfo.getRequestParams(), new TypeReference<>() {}));
            }
            requestInfoDto.setRequestType(caseInfo.getRequestType());
            requestInfoDto.setPostScript(caseInfo.getPostScript());
            requestInfoDto.setPerScript(caseInfo.getPreScript());
            requestInfoDto.setExpectState(caseInfo.getExpectState());

            // 2.2调用接口
            ResponseVO<Object> responseVO = this.sendRequest(requestInfoDto);

            // 3.保存执行记录
            Execution execution = new Execution();
            execution.setExecutionTime(responseVO.getTime());
            execution.setBody(mapper.writeValueAsString(responseVO.getData()));
            execution.setHeaders(mapper.writeValueAsString((responseVO.getHeaders())));
            execution.setResult(responseVO.getTestResult() ? 1 : 2);
            execution.setCaseId(id);
            execution.setStatusCode(responseVO.getStatus());
            execution.setError(Objects.isNull(responseVO.getErrorMsg())?"":responseVO.getErrorMsg());
            executionMapper.insert(execution);

            // 4.更新用例的测试结果
            caseInfo.setTestResult(responseVO.getTestResult()? 1 : 2);
            caseMapper.updateById(caseInfo);

            return true;
        } catch (Exception e){
            // 3.保存执行记录
            Execution execution = new Execution();
            execution.setCaseId(id);
            execution.setError(e.getMessage());
            executionMapper.insert(execution);

            // 4.更新用例的测试结果
            caseInfo.setTestResult(2);
            caseMapper.updateById(caseInfo);

            return false;
        }
    }

    /**
     * 替换字符串中所有 ${变量名} 占位符
     * @param originStr 原始url：${url}/user
     * @param envMap 环境变量map key=变量名，value=环境值
     * @return 替换完成后的真实url
     */
    private static String replaceEnvPlaceholder(String originStr, Map<String, String> envMap) {
        // 匹配 ${任意字符} 的正则
        Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

        if (originStr == null || originStr.isEmpty() || envMap == null || envMap.isEmpty()) {
            return originStr;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(originStr);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            // group(1) 获取括号内的变量名，示例：url
            String varName = matcher.group(1);
            // 取出环境变量值，无匹配则保留原占位符不替换
            String realValue = envMap.getOrDefault(varName, matcher.group());
            matcher.appendReplacement(sb, Matcher.quoteReplacement(realValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
