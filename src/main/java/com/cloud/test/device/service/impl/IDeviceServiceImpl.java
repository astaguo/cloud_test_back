package com.cloud.test.device.service.impl;

import com.cloud.test.base.utils.ShellExecutorUtil;
import com.cloud.test.device.domain.Devices;
import com.cloud.test.device.mapper.DevicesMapper;
import com.cloud.test.device.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IDeviceServiceImpl implements IDeviceService {

    @Autowired
    private DevicesMapper devicesMapper;

    @Autowired
    private ShellExecutorUtil shellExecutorUtil;

    /**
     * 获取 iOS 模拟器/设备列表（执行 xcrun simctl list devices 命令）
     * @return 设备列表字符串
     */
    @Override
    public List<Devices> getIosDeviceList() throws Exception {
        // 先校验是否已经存在设备列表
        List<Devices> devicesInDB = devicesMapper.selectList(null);
        if (Objects.nonNull(devicesInDB) && !devicesInDB.isEmpty()) {
            return devicesInDB;
        }

        // 1.执行指定命令并获取当前设备列表的字符串
        String command = "xcrun simctl list devices";
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

        // 4.将数据存到DB中
        devicesMapper.insert(devicesList);

        // 5.返回数据
        return devicesList;
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
}
