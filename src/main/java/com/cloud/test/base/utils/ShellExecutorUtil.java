package com.cloud.test.base.utils;

import org.apache.commons.exec.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 安全的 Shell 命令执行工具类
 * 包含命令白名单、超时控制、结果返回
 */
@Component
public class ShellExecutorUtil {

    // 命令白名单：只允许执行指定的安全命令，防止注入攻击
    private static final List<String> ALLOWED_COMMANDS = Arrays.asList(
            "xcrun simctl list devices",
            "xcrun simctl list",
            "idevice_id -l"
    );

    /**
     * 执行 Shell 命令
     * @param command 要执行的命令
     * @param timeout 超时时间（秒）
     * @return 命令执行结果（stdout + stderr）
     * @throws Exception 执行异常/超时/命令不在白名单
     */
    public String executeCommand(String command, int timeout) throws Exception {
        // 1. 安全校验：检查命令是否在白名单中
        if (!ALLOWED_COMMANDS.contains(command)) {
            throw new IllegalArgumentException("禁止执行未授权的命令：" + command);
        }

        // 2. 构建命令行（拆分命令和参数，避免注入）
        String[] commandArray = command.split(" ");
        CommandLine cmdLine = CommandLine.parse(commandArray[0]);
        for (int i = 1; i < commandArray.length; i++) {
            cmdLine.addArgument(commandArray[i], false); // false 表示不转义，保持原参数
        }

        // 3. 准备输出流，捕获命令输出
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);

        // 4. 执行命令并设置超时
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0); // 期望正常退出码为0
        executor.setWatchdog(new ExecuteWatchdog(timeout * 1000L)); // 超时时间（毫秒）

        try {
            executor.execute(cmdLine);
            // 5. 拼接标准输出和错误输出
            String stdout = outputStream.toString(StandardCharsets.UTF_8);
            String stderr = errorStream.toString(StandardCharsets.UTF_8);
            return stdout + (stderr.isEmpty() ? "" : "\n错误信息：" + stderr);
        } catch (IOException e) {
            throw new Exception("命令执行失败：" + e.getMessage(), e);
        } finally {
            // 关闭流
            outputStream.close();
            errorStream.close();
        }
    }

    // 简化方法：默认超时60秒
    public String executeCommand(String command) throws Exception {
        return executeCommand(command, 60);
    }
}