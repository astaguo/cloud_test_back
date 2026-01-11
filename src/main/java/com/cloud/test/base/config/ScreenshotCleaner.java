package com.cloud.test.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Calendar;

/**
 * 定时清理任务（精简版）
 */
@Component
public class ScreenshotCleaner {

    // 增加日志，方便排查问题
    private static final Logger log = LoggerFactory.getLogger(ScreenshotCleaner.class);

    @Value("${image.storage.path}")
    private String baseSavePath;

    @Value("${image.expire.time}")
    private int expireTimes;

    // 每分钟执行（先保留，后续可验证）
    @Scheduled(cron = "0 */1 * * * ?")
    public void cleanTempImgs() {
        // 第一步：打印日志，确认定时任务触发
        log.info("===== 定时清理任务开始执行 =====");
        log.info("清理目录：{}，过期秒数：{}", baseSavePath, expireTimes);

        File dir = new File(baseSavePath);
        // 检查目录是否存在
        if (!dir.exists()) {
            log.warn("清理目录不存在：{}", baseSavePath);
            return;
        }
        if (!dir.isDirectory()) {
            log.error("指定路径不是目录：{}", baseSavePath);
            return;
        }

        // 筛选.png文件（增加非空判断）
        File[] files = dir.listFiles((file) -> file != null && file.getName().endsWith(".png"));
        if (files == null || files.length == 0) {
            log.info("目录下无PNG图片需要清理，文件数量：{}", files == null ? 0 : files.length);
            return;
        }

        // 计算过期时间阈值（修复时间计算，避免int溢出）
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long expireTime = currentTime - (long) expireTimes * 1000L;
        log.info("当前时间戳：{}，过期阈值：{}（{}秒前）", currentTime, expireTime, expireTimes);

        // 遍历删除过期文件
        int deletedCount = 0;
        int totalCount = files.length;
        for (File file : files) {
            try {
                long fileModifyTime = file.lastModified();
                log.debug("文件{}，最后修改时间：{}，是否过期：{}",
                        file.getName(), fileModifyTime, fileModifyTime < expireTime);

                if (fileModifyTime < expireTime) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        deletedCount++;
                        log.info("成功删除过期图片：{}", file.getAbsolutePath());
                    } else {
                        log.warn("删除失败（权限不足/文件被占用）：{}", file.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                log.error("处理文件{}时出错", file.getName(), e);
            }
        }

        // 执行结果汇总
        log.info("===== 定时清理任务结束 =====");
        log.info("总计检查{}个文件，成功删除{}个过期文件", totalCount, deletedCount);
    }
}
