package com.cloud.test.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cloud.test.base.exceptions.UserDefinedException;
import com.cloud.test.base.utils.ImageCompressUtil;
import com.cloud.test.base.utils.ShellExecutorUtil;
import com.cloud.test.device.domain.Devices;
import com.cloud.test.device.mapper.DevicesMapper;
import com.cloud.test.device.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IDeviceServiceImpl implements IDeviceService {

    @Autowired
    private DevicesMapper devicesMapper;

    @Autowired
    private ShellExecutorUtil shellExecutorUtil;

    // 需要保存的路径
    @Value("${image.storage.path}")
    private String baseSavePath;

    // 需要访问的前缀
    @Value("${image.storage.access-prefix}")
    private String accessPath;

    /**
     * 获取 iOS 模拟器/设备列表（执行 xcrun simctl list devices 命令）
     * @return 设备列表字符串
     */
    @Override
    public List<Devices> getIosDeviceList() throws Exception {
        // 先校验是否已经存在设备列表
        List<Devices> devicesInDB = devicesMapper.selectList(null);
        if (Objects.nonNull(devicesInDB) && !devicesInDB.isEmpty()) {
            // 1. 查询所有设备的当前信息
            List<Devices> devicesList = getDevicesList("xcrun simctl list devices");

            // 2. 更新设备
            devicesList.forEach(devices -> {
                QueryWrapper<Devices> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("udid", devices.getUdid());
                devicesMapper.update(devices, queryWrapper);
            });

            // 3. 返回所有数据
            return devicesMapper.selectList(null);
        }

        // 1. 执行命令，并获得设备列表
        List<Devices> devicesList = getDevicesList("xcrun simctl list devices");

        // 2.将数据存到DB中
        devicesMapper.insert(devicesList);

        // 3.返回数据
        return devicesList;
    }

    /**
     * 获取 iOS 模拟器 激活设备列表（执行 xcrun simctl list devices booted 命令）
     * @return 设备列表字符串
     */
    @Override
    public List<Devices> getActiveDeviceList() {
        try {
            // 1. 执行命令，并获得设备列表
            List<Devices> devicesList = getDevicesList("xcrun simctl list devices booted");
            devicesList.forEach(System.out::println);

            // 2. 更新设备列表
            devicesList.forEach(devices -> {
                QueryWrapper<Devices> wrapper = new QueryWrapper<>();
                wrapper.eq("udid", devices.getUdid());
                devicesMapper.update(devices, wrapper);
            });

            // 3. 返回激活的设备列表
            return devicesList;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取真机 UDID 列表（执行 idevice_id -l 命令）
     * @return UDID 列表
     */
    @Override
    public String getRealDeviceUdidList() throws Exception {
        String command = "idevice_id -l";
        return shellExecutorUtil.executeCommand(command);
    }

    @Override
    public boolean startSimulator(String udid) {
        try {
            shellExecutorUtil.executeCommand(
                    "xcrun simctl boot " + udid);
            shellExecutorUtil.executeCommand(
                    "sleep 10");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean startApp(String buildId) {
        try {
            shellExecutorUtil.executeCommand(
                    "xcrun simctl launch booted " + buildId);
            shellExecutorUtil.executeCommand(
                    "sleep 10");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public String executeCommandForBytes() throws Exception {
        // 1. 执行原逻辑获取原始截图字节流
        byte[] originalBytes = shellExecutorUtil.executeCommandForScreenshot(
                "xcrun simctl io booted screenshot -");

        // 2. 调用压缩工具类（默认0.7质量，可调整为0.5/0.8等）并返回压缩后的字节流
        byte[] bytes = ImageCompressUtil.compressImage(originalBytes);

        // 3. 定义唯一文件名
        String fileName = "sim_" + UUID.randomUUID() + ".png";

        // 4. 处理存储路径（解决ResourceUtils在打包后失效问题）
        File dir = new File(baseSavePath);
        if(!dir.exists()) dir.mkdirs(); // 自动创建多级目录
        String savePath = baseSavePath + fileName;

        // 5.保存文件
        try (FileOutputStream fos = new FileOutputStream(savePath)) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new UserDefinedException("图片保存失败" + e.getMessage());
        }

        // 6. 返回文件访问路径
        return accessPath + fileName;
    }

    /**
     * 通过正则匹配出，当前的设备列表
     * @return
     * @throws Exception
     */
    private List<Devices> getDevicesList(String command) throws Exception {
        // 1.执行指定命令并获取当前设备列表的字符串
        String devicesString = shellExecutorUtil.executeCommand(command);

        // 2.通过正则分离出字符串中的设备名称，设备udid和设备状态
        String[] lines = devicesString.split("\n");
        Pattern pattern = Pattern.compile("\\s+(.*?) \\(([0-9A-F-]+)\\) \\((.*?)\\)");

        // 3.将信息存放到devicesList
        List<Devices> devicesList = new ArrayList<>();
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                Devices device = new Devices();
                device.setName(matcher.group(1).trim());    // 设备名称
                device.setUdid(matcher.group(2).trim());    // UDID
                device.setStatus(matcher.group(3).trim());  // 状态
                devicesList.add(device);
            }
        }
        return devicesList;
    }
}
