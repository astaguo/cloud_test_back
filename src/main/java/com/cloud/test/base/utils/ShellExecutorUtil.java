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
            // iOS
            "xcrun simctl list devices", // 查询当前可用设备列表
            "xcrun simctl list devices booted", // 获取当前已经启动的模拟器列表
            "xcrun simctl list",
            "idevice_id -l",
            "xcrun simctl io booted screenshot -", // 新增截图命令
            "xcrun simctl boot ", // 启动模拟器
            "sleep ", // 等待
            "xcrun simctl launch booted " // 启动某一个App
    );

    /**
     * 执行 Shell 命令
     * @param command 要执行的命令
     * @param timeout 超时时间（秒）
     * @return 命令执行结果（stdout + stderr）
     * @throws Exception 执行异常/超时/命令不在白名单
     */
    public String executeCommand(String command, int timeout) throws Exception {
        // 1. 检查命令
        CommandLine cmdLine = getCommandLine(command);

        // 2. 准备输出流，捕获命令输出
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);

        // 3. 执行命令并设置超时
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0); // 期望正常退出码为0
        executor.setWatchdog(new ExecuteWatchdog(timeout * 1000L)); // 超时时间（毫秒）

        try {
            executor.execute(cmdLine);
            // 4. 拼接标准输出和错误输出
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

    // 新增：执行命令并返回字节流（用于截图）
    public byte[] executeCommandForBytes(String command, int timeout) throws Exception {
        CommandLine cmdLine = getCommandLine(command);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        executor.setWatchdog(new ExecuteWatchdog(timeout * 1000L));

        try {
            executor.execute(cmdLine);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new Exception("命令执行失败：" + e.getMessage(), e);
        } finally {
            outputStream.close();
        }
    }

    // 简化方法：默认超时60秒, 不用截图
    public String executeCommand(String command) throws Exception {
        return executeCommand(command, 60);
    }
    
    // 要截图
    public byte[] executeCommandForScreenshot(String command) throws Exception {
        return executeCommandForBytes(command, 60);
    }


    private static CommandLine getCommandLine(String command) throws Exception {
        // 1. 安全校验：空值/空字符串校验
        if (command == null || command.trim().isEmpty()) {
            throw new Exception("命令不能为空");
        }
        String cmd = command.trim(); // 去除首尾空格，避免空格影响匹配

        // 2. 安全校验：检查命令是否以白名单中的某个前缀开头（替换原来的全等匹配）
        boolean isAllowed = false;
        for (String allowedCmd : ALLOWED_COMMANDS) {
            // 前缀匹配：输入命令以白名单命令开头（核心逻辑）
            // 如果需要"包含匹配"，可改为 cmd.contains(allowedCmd)
            if (cmd.startsWith(allowedCmd)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new IllegalArgumentException("禁止执行未授权的命令：" + command);
        }

        // 3. 构建命令行（拆分命令和参数，避免注入）
        String[] commandArray = cmd.split(" ");
        CommandLine cmdLine = CommandLine.parse(commandArray[0]);
        for (int i = 1; i < commandArray.length; i++) {
            cmdLine.addArgument(commandArray[i], false); // false 表示不转义，保持原参数
        }
        return cmdLine;
    }
}