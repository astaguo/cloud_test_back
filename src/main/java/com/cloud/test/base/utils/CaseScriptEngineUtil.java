package com.cloud.test.base.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.graalvm.polyglot.*;

import java.util.Map;
import java.util.concurrent.*;

public class CaseScriptEngineUtil {
    // 删掉全局 graalContext 成员变量
    private static final long SCRIPT_TIMEOUT_SECONDS = 2;

    public CaseScriptEngineUtil() {
        // 不再提前初始化全局上下文
    }

    // 提取上下文创建逻辑为私有方法，每次执行新建
    private Context createNewContext() {
        Engine engine = Engine.newBuilder("js")
                .option("engine.WarnInterpreterOnly", "false")
                .build();
        Context.Builder contextBuilder = Context.newBuilder("js")
                .engine(engine)
                .allowAllAccess(false)
                .allowHostAccess(HostAccess.NONE)
                .allowHostClassLookup(s -> false);
        return contextBuilder.build();
    }

    public void runUserScript(String userScript, Map<String, String> globalEnv, String responseBody)
            throws ExecutionException, TimeoutException {
        if (userScript == null || userScript.isBlank()) {
            return;
        }
        ExecutorService singlePool = Executors.newSingleThreadExecutor();
        Future<Void> task = singlePool.submit(() -> {
            // 每次执行都新建干净独立JS上下文，无残留变量
            try (Context ctx = createNewContext()) {
                // 步骤1：拼接JS初始化代码
                StringBuilder jsInitObj = new StringBuilder("let envStore = {");
                boolean firstItem = true;
                for (Map.Entry<String, String> entry : globalEnv.entrySet()) {
                    if (!firstItem) jsInitObj.append(",");
                    firstItem = false;
                    String key = entry.getKey().replace("\"", "\\\"");
                    String val = entry.getValue() == null ? "" : entry.getValue().replace("\"", "\\\"");
                    jsInitObj.append("\"").append(key).append("\":\"").append(val).append("\"");
                }
                jsInitObj.append("};");

                String fullScript = getString(userScript, responseBody, jsInitObj);
                Source source = Source.create("js", fullScript);
                ctx.eval(source);

                // 同步环境变量回Java Map
                Value getJsonFunc = ctx.getBindings("js").getMember("getEnvJson");
                String envJson = getJsonFunc.execute().asString();
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> updatedEnv = objectMapper.readValue(envJson, new TypeReference<Map<String, String>>() {});
                globalEnv.clear();
                globalEnv.putAll(updatedEnv);
            }
            return null;
        });

        try {
            task.get(SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            task.cancel(true);
            throw new TimeoutException("脚本执行超时，限制最长" + SCRIPT_TIMEOUT_SECONDS + "秒，请检查是否存在死循环");
        } catch (InterruptedException e) {
            throw new ExecutionException("脚本执行被中断", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            String errMsg;
            if (cause instanceof PolyglotException polyEx) {
                errMsg = "JS脚本异常：" + polyEx.getMessage();
            } else {
                errMsg = "脚本执行失败：" + cause.getMessage();
            }
            throw new ExecutionException(errMsg, cause);
        } finally {
            singlePool.shutdownNow();
        }
    }

    private static @NonNull String getString(String userScript, String responseBody, StringBuilder jsInitObj) {
        String responseStr;
        if (responseBody != null && !responseBody.isEmpty()) {
            responseStr = "let responseBody = " + responseBody + ";";
        } else {
            responseStr = "let responseBody = '';";
        }

        String buildInScript = jsInitObj + responseStr + """
            function getEnv(key) {
                return envStore[key];
            }
            function setEnv(key, value) {
                envStore[key] = String(value);
            }
            function assert(condition, errorMsg) {
                if (!condition) throw "断言失败：" + errorMsg;
            }
            function getEnvJson() {
                return JSON.stringify(envStore);
            }
            function getResponse() {
                try {
                    return JSON.parse(responseBody);
                } catch {
                    return responseBody;
                }
            }
            """;

        return buildInScript + "\n" + userScript;
    }

    public void runUserScript(String userScript, Map<String, String> globalEnv) throws ExecutionException, TimeoutException {
        this.runUserScript(userScript, globalEnv, "");
    }
}